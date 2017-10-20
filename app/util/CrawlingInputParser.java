package util;

import models.*;
import scala.util.matching.Regex;
import storage.UIScreenStore;
import views.CrawlingInput;
import views.RenderingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlingInputParser {

    public static UIScreen parseCrawlingInput(CrawlingInput crawlingInput) {
        UIScreen screenToBeCreated = new UIScreen();
        if (crawlingInput.getRootTitle() != null) {
            screenToBeCreated.setTitle(crawlingInput.getRootTitle());
        }
        if (crawlingInput.getRootPackageName() != null) {
            screenToBeCreated.setPackageName(crawlingInput.getRootPackageName());
        }
        if (crawlingInput.getDeviceInfo() != null) {
            screenToBeCreated.setDeviceInfo(crawlingInput.getDeviceInfo());
        }

        String currentScreenId = screenToBeCreated.getId();

        HashMap<Long, UIElement> viewIdToUIElementMap = new HashMap<>();
        for (Map.Entry<Long, RenderingView> entry : crawlingInput.getViewMap().entrySet()) {
            RenderingView renderingView = entry.getValue();
            //Create a UI element from the view -- no child text / no semantic / or navigational stuff for now
            UIElement uiElement = createUIElementFromRenderingView(renderingView);
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
            screenToBeCreated.add(uiElement);
        }

        //Set semantic actions here -- we should only add for stuff that doesn't have navigational action
        for (UIElement uiElement: allActionableElementsList) {
            uiElement.updateSemanticActions(screenToBeCreated);
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
                    if (uiStep.isInterScreenStep()) {
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

    public static UIStep getLastUIStep(UIScreen currentScreen,
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

        //This is not valid
        //TODO -- commenting this -- see if it works.
//        if (!lastScreenPackageName.toLowerCase().equals(lastViewClicked.getPackageName().toLowerCase())) {
//            return undefinedUIStep;
//        }

        UIScreen lastScreen =  null;
        UIElement lastElement = null;
        UIAction lastUIAction = UIAction.actionStringToEnum(lastAction);
        UIStep.UIStepType uiStepType = UIStep.UIStepType.UNDEFINED;

        String lastScreenId = Utils.EMPTY_STRING;

        if (!Utils.nullOrEmpty(lastScreenTitle) && !Utils.nullOrEmpty(lastScreenPackageName)) {
            lastScreenId = UIScreen.getScreenId(lastScreenPackageName, lastScreenTitle, currentScreen.getDeviceInfo().toString());
        }


        if (Utils.nullOrEmpty(lastScreenId)) {
            //We don't know about the last screen
            return undefinedUIStep;
        }

        if (lastScreenId.equalsIgnoreCase(currentScreen.getId())) {
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
        return new UIStep(lastScreenId, currentScreen.getId(), lastElement.getId(), lastUIAction.id(), uiStepType.id());
    }

    public static UIElement createUIElementFromRenderingView(RenderingView renderingView) {
        UIElement uiElement = new UIElement(renderingView.getClassName(),
                renderingView.getPackageName(), renderingView.getOverallText());
        //Add UIActions based on view
        if (renderingView.isClickable() || renderingView.isCheckable()) {
            uiElement.add(UIAction.CLICK);
        }
        return uiElement;
    }
}
