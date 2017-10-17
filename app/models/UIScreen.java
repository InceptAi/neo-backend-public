package models;

import storage.UIScreenStore;
import util.MergeUtils;
import util.Utils;
import util.ViewUtils;

import java.util.*;

public class UIScreen {
    private String packageName = Utils.EMPTY_STRING;
    private String title = Utils.EMPTY_STRING;
    private List<UIPath> uiPaths;
    private HashMap<String, UIElement> uiElements;
    private HashMap<String, String> deviceInfo;

    public String id() {
        return getScreenId(packageName, title, deviceInfo.toString());
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
        if (crawlingInput.getDeviceInfo() != null) {
            this.deviceInfo = crawlingInput.getDeviceInfo();
        }

        String currentScreenId = getScreenId(packageName, title, deviceInfo.toString());

        HashMap<Long, UIElement> viewIdToUIElementMap = new HashMap<>();
        for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
            RenderingView renderingView = entry.getValue();
            //Create a UI element from the view -- no child text / no semantic / or navigational stuff for now
            UIElement uiElement = new UIElement(renderingView);
            viewIdToUIElementMap.put(renderingView.getFlatViewId(), uiElement);
        }

        //Second pass to assign created elements
        List<UIElement> topLevelElementList = new ArrayList<>();
        List<UIElement> allActionableElementsList = new ArrayList<>();
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
                topLevelElementList.add(currentElement);
            }

            allActionableElementsList.add(currentElement);
        }


        for (UIElement uiElement: topLevelElementList) {
            uiElements.put(uiElement.id(), uiElement);
        }

        //Set semantic actions here -- we should only add for stuff that doesn't have navigational action
        for (UIElement uiElement: allActionableElementsList) {
            uiElement.updateSemanticActions(currentScreenId, title);
        }

        //Create UIScreen / UIElement / UIAction from last stuff
        UIStep uiStep = getLastUIStep(
                getScreenId(packageName, title, deviceInfo.toString()),
                crawlingInput.getLastScreenTitle(),
                crawlingInput.getLastScreenPackageName(),
                crawlingInput.getLastViewClicked(),
                crawlingInput.getLastUIAction());

        //Add the uiStep to lastScreen's UIPath and assign to current screen
        if (!uiStep.isUndefined()) {
            String lastScreenId = uiStep.getUiScreenId();
            if (!Utils.nullOrEmpty(lastScreenId)) {
                UIScreen lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);
                if (lastScreen != null) {
                    if (uiStep.isInterScreenStep()) {
                        List<UIPath> lastScreenUiPaths = lastScreen.getUiPaths();
                        //Add the navigation action for the UI Element
                        UIElement lastElement = lastScreen.findElementById(uiStep.getUiElementId());
                        if (lastElement != null) {
                            lastElement.add(new NavigationalAction(uiStep.getUiActionId(), id()));
                            lastScreen.add(lastElement);
                            uiPaths = getUIPathBasedOnLastScreenPath(lastScreenUiPaths, uiStep);
                        }
                    } else if (uiStep.isWithinSameScreen()) {
                        List<String> differingElementIds = getDifferingUIElementIds(lastScreen.uiElements);
                        for (String elementId: differingElementIds) {
                            UIElement differingElement = uiElements.get(elementId);
                            if (differingElement != null) {
                                differingElement.add(uiStep);
                            }
                        }
                    }
                }
            }
        }

    }

    private String getTitleFromView(HashMap<Long, RenderingView> renderingViewHashMap) {
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
        UIStep undefinedUIStep = new UIStep();
        if (lastViewClicked == null) {
            return undefinedUIStep;
        }

        if (Utils.nullOrEmpty(lastScreenPackageName) || Utils.nullOrEmpty(lastViewClicked.getPackageName())) {
            return undefinedUIStep;
        }

        if (!lastScreenPackageName.toLowerCase().equals(lastViewClicked.getPackageName().toLowerCase())) {
            return undefinedUIStep;
        }

        UIScreen lastScreen =  null;
        UIElement lastElement = null;
        UIAction lastUIAction = UIAction.actionStringToEnum(lastAction);
        UIStep.UIStepType uiStepType = UIStep.UIStepType.UNDEFINED;

        String lastScreenId = Utils.EMPTY_STRING;

        if (!Utils.nullOrEmpty(lastScreenTitle) && !Utils.nullOrEmpty(lastScreenPackageName)) {
            lastScreenId = getScreenId(lastScreenPackageName, lastScreenTitle, deviceInfo.toString());
        }


        if (Utils.nullOrEmpty(lastScreenId)) {
            //We don't know about the last screen
            return undefinedUIStep;
        }

        if (lastScreenId.equalsIgnoreCase(currentScreenId)) {
            uiStepType = UIStep.UIStepType.WITHIN_SAME_SCREEN;
        } else {
            uiStepType = UIStep.UIStepType.TO_ANOTHER_SCREEN;
        }

        lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);

        if (lastScreen == null) { // handle this case later
            return undefinedUIStep;
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
        return new UIStep(lastScreen, lastElement, lastUIAction, uiStepType);
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(HashMap<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
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


    public static String getScreenId(String packageName, String title, String deviceInfo) {
        String toHash = packageName.toLowerCase() + "#" + title.replaceAll(" ", "_").toLowerCase() + "#" + deviceInfo.replaceAll(" ", "_").toLowerCase();
        int hashCode = (toHash).hashCode();
        return String.valueOf(hashCode);
    }

    public boolean mergeScreen(UIScreen uiScreen) {
        if (!this.equals(uiScreen)) {
            return false;
        }
        uiElements = MergeUtils.mergeUIElements(uiElements, uiScreen.getUiElements());
        uiPaths = MergeUtils.mergeUIPaths(uiPaths, uiScreen.getUiPaths());
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!UIScreen.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final UIScreen other = (UIScreen) obj;

        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(getScreenId(packageName, title, deviceInfo.toString()));
    }


    public List<String> getDifferingUIElementIds(HashMap<String, UIElement> otherUIElementsMap) {
        Set<String> currentElementIds = uiElements.keySet();
        Set<String> otherElementIds = otherUIElementsMap.keySet();
        List<String> differingIds = new ArrayList<>();
        for (String currentId: currentElementIds) {
            if (!otherElementIds.contains(currentId)) {
                differingIds.add(currentId);
            }
        }
        return differingIds;
    }


}
