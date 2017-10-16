package models;

import storage.UIScreenStore;
import util.Utils;
import util.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIScreen {
    private String packageName = Utils.EMPTY_STRING;
    private String title = Utils.EMPTY_STRING;
    private List<UIPath> uiPaths;
    private HashMap<String, UIElement> uiElements;
    private DeviceInfo deviceInfo;

    public String id() {
        return getIdFromPackageAndTitle(packageName,title);
    }

    public UIScreen(CrawlingInput crawlingInput) {
        initialize(crawlingInput);
    }

    public void initialize(CrawlingInput crawlingInput) {
        //Put this in initialize -- move it out of the constructor
        this.uiPaths = new ArrayList<>();
        this.uiElements = new HashMap<>();
        if (crawlingInput.getRootTitle() != null) {
            this.title = crawlingInput.getRootTitle();
        }
        if (crawlingInput.getRootPackageName() != null) {
            this.packageName = crawlingInput.getRootPackageName();
        }

        //Create UIScreen / UIElement / UIAction from last stuff
        UIStep uiStep = getLastUIStep(
                getIdFromPackageAndTitle(packageName, title),
                crawlingInput.getLastScreenTitle(),
                crawlingInput.getLastScreenPackageName(),
                crawlingInput.getLastViewClicked(),
                crawlingInput.getLastUIAction());

        //Add the uiStep to lastScreen's UIPath and assign to current screen
        if (uiStep != null) {
            String lastScreenId = uiStep.getUiScreenId();
            if (!Utils.nullOrEmpty(lastScreenId)) {
                UIScreen lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);
                if (lastScreen != null) {
                    List<UIPath> lastScreenUiPaths = lastScreen.getUiPaths();
                    //Add the navigation action for the UI Element
                    UIElement lastElement = lastScreen.findElementById(uiStep.getUiElementId());
                    if (lastElement != null) {
                        lastElement.add(new NavigationalAction(uiStep.getUiActionId(), id()));
                        lastScreen.add(lastElement);
                        uiPaths = getUIPathBasedOnLastScreenPath(lastScreenUiPaths, uiStep);
                    }
                }
            }
        }

        HashMap<Long, UIElement> viewIdToUIElementMap = new HashMap<>();
        for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
            RenderingView renderingView = entry.getValue();
            //Create a UI element from the view -- no child text / no semantic / or navigational stuff for now
            UIElement uiElement = new UIElement(renderingView);
            viewIdToUIElementMap.put(renderingView.getFlatViewId(), uiElement);
        }

        //Second pass to assign created elements
        List<UIElement> uiElementList = new ArrayList<>();
        for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
            RenderingView renderingView = entry.getValue();
            if (renderingView.isParentOfClickableView() &&
                    !renderingView.isClickable() &&
                    renderingView.getOverallText().equals(Utils.EMPTY_STRING)) {
                //This view is a parent of clickable views but is not clickable itself. Ignore
                continue;
            }
            UIElement currentElement = viewIdToUIElementMap.get(renderingView.getFlatViewId());
            //parent info
            RenderingView parentView = crawlingInput.getViewMap().get(renderingView.getParentViewId());
            UIElement parentElement = viewIdToUIElementMap.get(renderingView.getParentViewId());
            if (parentElement != null && parentView != null && parentView.isClickable()) {
                parentElement.addChildren(currentElement);
            } else {
                uiElementList.add(currentElement);
            }

            //Set semantic actions here
            currentElement.updateSemanticActions(title);
            if (uiStep != null) {
                currentElement.add(uiStep);
            }
        }

        for (UIElement uiElement: uiElementList) {
            uiElements.put(uiElement.id(), uiElement);
        }
    }

    private String getTitle(HashMap<Long, RenderingView> renderingViewHashMap) {
        long topTextViewId = 0;
        long topTextViewYCoordinate = Long.MAX_VALUE;
        long topTextViewXCoordinate = Long.MAX_VALUE;
        String titleToReturn = Utils.EMPTY_STRING;
        for (Map.Entry<Long, RenderingView> entry : renderingViewHashMap.entrySet()) {
            RenderingView renderingView = entry.getValue();
            if (ViewUtils.isTextView(renderingView) &&
                    renderingView.getTopY() < topTextViewYCoordinate ||
                    (renderingView.getTopY() == topTextViewYCoordinate && renderingView.getLeftX() < topTextViewXCoordinate)) {
                topTextViewYCoordinate = renderingView.getTopY();
                topTextViewXCoordinate = renderingView.getLeftX();
                topTextViewId = renderingView.getFlatViewId();
                titleToReturn = renderingView.getText();
            }
        }
        return titleToReturn;
    }

    private List<UIPath> getUIPathBasedOnLastScreenPath(List<UIPath> lastScreenUIPaths, UIStep uiStep) {
        List<UIPath> uiPathList = new ArrayList<>();
        if (lastScreenUIPaths == null || lastScreenUIPaths.isEmpty()) {
            uiPathList.add(new UIPath(SemanticActionType.NAVIGATE, uiStep));
        } else {
            for (UIPath uiPath: lastScreenUIPaths) {
                uiPathList.add(uiPath.addToPath(uiStep));
            }
        }
        return uiPathList;
    }

    private UIStep getLastUIStep(String currentScreenId,
                                 String lastScreenTitle,
                                 String lastScreenPackageName,
                                 RenderingView lastViewClicked,
                                 String lastAction) {
        //Create the last step in UIPath
        //Add navigational action to the last UI Element
        if (lastViewClicked == null) {
            return null;
        }

        if (Utils.nullOrEmpty(lastScreenPackageName) || Utils.nullOrEmpty(lastViewClicked.getPackageName())) {
            return null;
        }

        if (!lastScreenPackageName.toLowerCase().equals(lastViewClicked.getPackageName().toLowerCase())) {
            return null;
        }

        UIScreen lastScreen =  null;
        UIElement lastElement = null;
        UIAction lastUIAction = UIAction.actionStringToEnum(lastAction);
        String lastScreenId = Utils.EMPTY_STRING;

        if (!Utils.nullOrEmpty(lastScreenTitle) && !Utils.nullOrEmpty(lastScreenPackageName)) {
            lastScreenId = getIdFromPackageAndTitle(lastScreenPackageName, lastScreenTitle);
        }
        if (Utils.nullOrEmpty(lastScreenId) || lastScreenId.equalsIgnoreCase(currentScreenId)) {
                //No navigation since we are on the same screen
                return null;
        }

        lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);
        if (lastScreen == null) { // handle this case later
            return null;
        }

        List <UIElement> matchingElements =  lastScreen.findElementsInScreen(
                lastViewClicked.getClassName(),
                lastViewClicked.getPackageName(),
                lastViewClicked.getText());

        //If more than one, take the top one for now
        // TODO sort the elements based on level of text matching --
        // TODO more matching means higher score so we select the element that matches the most.
        if (!matchingElements.isEmpty()) {
            lastElement = matchingElements.get(0);
        }

        if (lastElement == null) {
            //uiElement not found in the screen, create it and assign to the screen
            lastElement = new UIElement(lastViewClicked);
            lastScreen.add(lastElement);
            //update the lastScreen in the screenStore
            UIScreenStore.getInstance().updateScreen(lastScreen);
        }

        //Create a UI Step and add to the path
        return new UIStep(lastScreen, lastElement, lastUIAction);
    }

    public UIScreen(String title, DeviceInfo deviceInfo) {
        this.title = title;
        this.deviceInfo = deviceInfo;
        this.uiPaths = new ArrayList<>();
        this.uiElements = new HashMap<>();
    }

    public void add(UIPath uiPath) {
        this.uiPaths.add(uiPath);
    }

    public void add(UIElement uiElement) {
        this.uiElements.put(uiElement.id(), uiElement);
    }

    public void update(String odlElementId, UIElement uiElement) {
        this.uiElements.remove(odlElementId);
        this.uiElements.put(uiElement.id(), uiElement);
    }

    public List<UIPath> getUiPaths() {
        return uiPaths;
    }

    public HashMap<String, UIElement> getUiElements() {
        return uiElements;
    }

    public String getTitle() {
        return title;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    private List<UIElement> findElementsInScreen(String className, String packageName, String text) {
        List<UIElement> uiElementList = new ArrayList<>();
        for (UIElement uiElement: uiElements.values()) {
            if (uiElement.getClassName().equals(className) &&
                    uiElement.getPackageName().equals(packageName) &&
                    uiElement.isMatchForText(text)) {
                uiElementList.add(uiElement);
            }
        }
        return uiElementList;
    }


    private UIElement findElementById(String elementId) {
        return uiElements.get(elementId);
    }


    public static String getIdFromPackageAndTitle(String packageName, String title) {
        String toHash = packageName.toLowerCase() + "#" + title.replaceAll(" ", "_").toLowerCase();
        int hashCode = (toHash).hashCode();
        return String.valueOf(hashCode);
    }


}
