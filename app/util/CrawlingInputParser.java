package util;

import models.*;
import storage.SemanticActionStore;
import storage.UIScreenStore;
import views.CrawlingInput;
import views.RenderingView;

import java.util.*;

public class CrawlingInputParser {

    public static UIScreen parseCrawlingInput(CrawlingInput crawlingInput) {
        UIScreen screenToBeCreated = new UIScreen();
        if (crawlingInput.getRootTitle() != null) {
            screenToBeCreated.setTitle(crawlingInput.getRootTitle());
            Utils.printDebug("In parseCrawlingInput currentTitle: " + crawlingInput.getRootTitle());
        }
        if (crawlingInput.getRootPackageName() != null) {
            screenToBeCreated.setPackageName(crawlingInput.getRootPackageName());
            Utils.printDebug("In parseCrawlingInput currentPkg: " + crawlingInput.getRootPackageName());
        }
        if (crawlingInput.getDeviceInfo() != null) {
            screenToBeCreated.setDeviceInfo(crawlingInput.getDeviceInfo());
        }

        if (crawlingInput.getLastScreenTitle() != null &&
                crawlingInput.getLastScreenPackageName() != null &&
                crawlingInput.getLastUIAction() != null &&
                crawlingInput.getLastViewClicked() != null ) {
            String lastClickedText = crawlingInput.getLastViewClicked().getText() != null ?
                    crawlingInput.getLastViewClicked().getText() : Utils.EMPTY_STRING;
            Utils.printDebug("In parseCrawlingInput lastScreenTitle: " +
                    crawlingInput.getLastScreenTitle() + " lastpkg: " +
                    crawlingInput.getLastScreenPackageName() + " lastAction: " +
                    crawlingInput.getLastUIAction() + " lastViewText: " +
                    lastClickedText);

        }


        String currentScreenId = screenToBeCreated.getId();

        List<Long> sortedViewIds= new ArrayList<>(crawlingInput.getViewMap().keySet());
        Collections.sort(sortedViewIds);

        HashMap<Long, UIElement> viewIdToUIElementMap = new HashMap<>();
        for (Long viewId: sortedViewIds) {
        //for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
            //RenderingView renderingView = entry.getValue();
            //Create a UI element from the view -- no child text / no semantic / or navigational stuff for now
            RenderingView renderingView = crawlingInput.getViewMap().get(viewId);
            UIElement uiElement = createUIElementFromRenderingView(renderingView);
            viewIdToUIElementMap.put(renderingView.getFlatViewId(), uiElement);
        }

        //Second pass to assign created elements
        List<UIElement> topLevelElementList = new ArrayList<>();
        List<UIElement> allActionableElementsList = new ArrayList<>();
        for (Long viewId: sortedViewIds) {
            RenderingView renderingView = crawlingInput.getViewMap().get(viewId);
//            for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
//            RenderingView renderingView = entry.getValue();
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
            screenToBeCreated.add(uiElement);
        }

        //Set semantic actions here -- we should only add for stuff that doesn't have navigational action
        for (UIElement uiElement: allActionableElementsList) {
            updateSemanticActions(screenToBeCreated, uiElement);
            //uiElement.updateSemanticActions(screenToBeCreated);
        }

        //Create UIScreen / UIElement / UIAction from last stuff
        UIStep uiStep = getLastUIStep(
                screenToBeCreated,
                crawlingInput.getLastScreenTitle(),
                crawlingInput.getLastScreenPackageName(),
                crawlingInput.getLastViewClicked(),
                crawlingInput.getLastUIAction());


        //Add the uiStep to lastScreen's UIPath and assign to current screen
        if (!uiStep.isUndefined()) {
            Utils.printDebug("Last UI Step is " + uiStep.toString());
            String lastScreenId = uiStep.getSrcScreenId();
            if (!Utils.nullOrEmpty(lastScreenId)) {
                UIScreen lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);
                if (lastScreen != null) {
                    if (uiStep.isInterScreenStep() || uiStep.isSoftStep()) { //navigational step
                        Utils.printDebug("Navigational UI Step");
                        List<UIPath> lastScreenUiPaths = lastScreen.getUiPaths();
                        Utils.printDebug("Last screen paths: " + lastScreenUiPaths.toString());
                        UIElement lastElement = lastScreen.findElementById(uiStep.getUiElementId());
                        if (lastElement != null) {
                            Utils.printDebug("Adding navigational step to element: " + lastElement.toString());
                            lastElement.add(new NavigationalAction(uiStep.getUiActionId(), currentScreenId));
                            lastScreen.add(lastElement);
                            Utils.printDebug("Adding uiStep to lastPaths");
                            screenToBeCreated.setUiPaths(MergeUtils.getUIPathBasedOnLastScreenPath(lastScreenUiPaths, uiStep));
                            Utils.printDebug("New UI Path: " + screenToBeCreated.getUiPaths().toString());
                        }
                    } else if (uiStep.isWithinSameScreen()) {
                        Utils.printDebug("Within Screen UI Step");
                        List<String> differingElementIds = MergeUtils.getDifferingUIElementIdsInCurrentScreen(
                                screenToBeCreated.getUiElements().keySet(),
                                lastScreen.getUiElements().keySet());
                        for (String elementId: differingElementIds) {
                            UIElement differingElement = screenToBeCreated.getUiElements().get(elementId);
                            if (differingElement != null) {
                                Utils.printDebug("Adding last step to get to element: " + differingElement.toString());
                                differingElement.add(uiStep);
                            }
                        }
                    }
                }
            }
        }
        return screenToBeCreated;
    }

    private static UIStep getLastUIStep(UIScreen currentScreen,
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
            Utils.printDebug("Error in getLastUIStep: empty lastPkg: or lastViewClickedPkg");
            return undefinedUIStep;
        }

        //If there is no text in the clicked view, we can't tell anything so its pointless
        //TODO: We should only take into account TYPE_VIEW_CLICKED events ?
//        if (Utils.nullOrEmpty(lastViewClicked.getOverallText())) {
//            Utils.printDebug("Error In getLastUIStep: empty text for lastViewClicked");
//            return undefinedUIStep;
//        }

        //This is not valid since cell settings have a different package name
        //TODO -- commenting this -- see if it works.
//        if (!lastScreenPackageName.toLowerCase().equals(lastViewClicked.getPackageName().toLowerCase())) {
//            return undefinedUIStep;
//        }


        //Check if the text of lastViewClicked matches with
        UIScreen lastScreen =  null;
        UIElement lastElement = null;
        UIAction lastUIAction = UIAction.actionStringToEnum(lastAction);
        UIStep.UIStepType uiStepType = UIStep.UIStepType.UNDEFINED;

        String lastScreenId = Utils.EMPTY_STRING;

        if (!Utils.nullOrEmpty(lastScreenTitle) && !Utils.nullOrEmpty(lastScreenPackageName)) {
            lastScreenId = UIScreen.getScreenId(lastScreenPackageName, lastScreenTitle, currentScreen.getDeviceInfo().toString());
            lastScreen = UIScreenStore.getInstance().getScreen(lastScreenId);
        }

        if (lastScreen == null) { // handle this case later
            Utils.printDebug("Error In getLastUIStep: lastScreenId not found with title/pkg " + lastScreenTitle + " / " + lastScreenPackageName);
            return undefinedUIStep;
        }


        if (lastScreenId.equalsIgnoreCase(currentScreen.getId())) {
            uiStepType = UIStep.UIStepType.WITHIN_SAME_SCREEN;
        } else {
            if (Utils.nullOrEmpty(lastViewClicked.getOverallText())) {
                uiStepType = UIStep.UIStepType.SOFT_STEP_INTER_SCREEN;
            } else {
                uiStepType = UIStep.UIStepType.TO_ANOTHER_SCREEN;
            }
        }

        boolean isSoftLink = uiStepType.equals(UIStep.UIStepType.SOFT_STEP_INTER_SCREEN);
        String textForMatchingElement = isSoftLink ? currentScreen.getTitle() : lastViewClicked.getOverallText();

        //TODO: Replaced getText with getOverallText to include contentDescription -- see if it works
        List <UIElement> matchingElements = lastScreen.findElementsInScreen(
                lastViewClicked.getClassName(),
                lastViewClicked.getPackageName(),
                textForMatchingElement,
                isSoftLink);

        //If more than one, take the top one for now
        // TODO sort the elements based on level of text matching --
        // TODO more matching means higher score so we select the element that matches the most.
        if (!matchingElements.isEmpty()) {
            lastElement = matchingElements.get(0);
        }

//        if (lastElement == null) {
//            if (isSoftLink || Utils.nullOrEmpty(lastViewClicked.getOverallText())) {
//                //Softlink and couldn't find this element in the last screen,
//                //or last view clicked has not meaningful text to create an element, so bail here
//                return undefinedUIStep;
//            }
//            //Create a new element
//            lastElement = createUIElementFromRenderingView(lastViewClicked);
//            lastScreen.add(lastElement);
//            //update the lastScreen in the screenStore
//            UIScreenStore.getInstance().updateScreen(lastScreen);
//        }

        if (lastElement == null) {
            return undefinedUIStep;
        }

        //Create a UI Step and add to the path
        return new UIStep(lastScreenId, currentScreen.getId(), lastElement.getId(), lastUIAction.id(), uiStepType.id());
    }

    public static UIElement createUIElementFromRenderingView(RenderingView renderingView) {
        String primaryText = renderingView.getOverallText();
        String className = renderingView.getClassName();
        String packageName = renderingView.getPackageName();
        String textBasedOnClassName = ViewUtils.getTextBasedOnClass(className, primaryText);
        UIElement uiElement = new UIElement(
                className,
                packageName,
                textBasedOnClassName,
                ViewUtils.isToggleable(className));
        //Add UIActions based on view
        if (renderingView.isClickable() || renderingView.isCheckable()) {
            uiElement.add(UIAction.CLICK);
        }
        return uiElement;
    }

    private static void updateSemanticActions(UIScreen uiScreen, UIElement uiElement) {
        if (uiScreen == null || uiElement == null) {
            return;
        }
        HashMap<String, SemanticAction> semanticActionHashMap = uiElement.getSemanticActions();
        for (UIAction uiAction: uiElement.getUiActions()) {
            if (semanticActionHashMap.get(uiAction.id()) == null) {
                SemanticAction semanticAction = SemanticAction.create(uiScreen, uiElement, uiAction);
                if (!SemanticAction.isUndefined(semanticAction)) {
                    semanticActionHashMap.put(uiAction.id(), semanticAction);
                    SemanticActionStore.getInstance().addSemanticAction(semanticAction);
                    Utils.printDebug("Adding semantic action: " + semanticAction);
                }
            }
        }
    }
}
