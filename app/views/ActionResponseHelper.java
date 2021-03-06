package views;

import akka.actor.dsl.Creators;
import graph.PathFinder;
import models.*;
import nlu.SimpleTextInterpreter;
import storage.SemanticActionStore;
import storage.UIScreenStore;
import util.Utils;
import util.ViewUtils;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionResponseHelper {

    //Take input the semantic ids and best matching string and create action response
    public static ActionResponse createActionResponse(String inputText, String packageName, String baseScreenTitle,
                                                      String deviceInfo, PathFinder pathFinder) {
        UIScreen startingScreen = UIScreenStore.getInstance().getScreen(packageName, baseScreenTitle, deviceInfo);
        if (startingScreen == null) {
            return new ActionResponse();
        }
        List<ActionDetails> actionDetailsList = new ArrayList<>();
        //find top actions first
        final int MAX_RESULTS = 1;
        HashMap<String, String> topMatchingActions = SemanticActionStore.getInstance().searchActions(inputText,
                deviceInfo, new SimpleTextInterpreter(), MAX_RESULTS);
        for (HashMap.Entry<String, String> entry : topMatchingActions.entrySet()) {
            String actionId = entry.getKey();
            String description = entry.getValue();
            SemanticAction semanticAction = SemanticActionStore.getInstance().getAction(actionId);
            UIScreen dstScreen = UIScreenStore.getInstance().getScreen(semanticAction.getUiScreenId());
            UIScreen srcScreen = UIScreenStore.getInstance().getScreen(startingScreen.getId());
            if (dstScreen == null || srcScreen == null) {
                return null;
            }
            UIPath navigationPathBetweenScreens = pathFinder.findPathBetweenScreens(srcScreen, dstScreen);
            //Create navigation list
            List<NavigationIdentifier> navigationIdentifierList = getNavigationPathForClient(navigationPathBetweenScreens);
            //Last step
            ScreenIdentifier dstScreenIdentifier = new ScreenIdentifier(dstScreen.getTitle(), dstScreen.getPackageName());
            UIElement uiElement = srcScreen.getUiElements().get(semanticAction.getUiElementId());
            ElementIdentifier elementIdentifier = new ElementIdentifier(uiElement.getClassName(), uiElement.getPackageName(),
                    uiElement.getPrimaryText(), uiElement.getChildText());
            ActionIdentifier actionIdentifier = new ActionIdentifier(dstScreenIdentifier, elementIdentifier, description,
                    semanticAction.getUiActionId());
            //Create the condition to check for success
            Condition successCondition = create(semanticAction, description);
            ActionDetails actionDetails = new ActionDetails(successCondition, navigationIdentifierList, actionIdentifier);
            actionDetailsList.add(actionDetails);
        }
        return new ActionResponse(actionDetailsList);
    }


    public static List<NavigationIdentifier> getNavigationPathForClient(UIPath uiPath) {
        if (uiPath == null) {
            return null;
        }
        List<NavigationIdentifier> navigationIdentifierList = new ArrayList<>();
        for (UIStep uiStep: uiPath.getUiSteps()) {
            UIScreen srcScreen = UIScreenStore.getInstance().getScreen(uiStep.getSrcScreenId());
            UIScreen dstScreen = UIScreenStore.getInstance().getScreen(uiStep.getDstScreenId());
            UIElement uiElement = srcScreen.getUiElements().get(uiStep.getUiElementId());
            if (srcScreen == null || dstScreen == null || uiElement == null) {
                return null;
            }
            ScreenIdentifier srcIdentifier = new ScreenIdentifier(srcScreen.getTitle(), srcScreen.getPackageName());
            ScreenIdentifier dstIdentifier = new ScreenIdentifier(dstScreen.getTitle(), dstScreen.getPackageName());
            ElementIdentifier elementIdentifier = new ElementIdentifier(uiElement.getClassName(),
                    uiElement.getPackageName(), uiElement.getPrimaryText(), uiElement.getChildText());
            NavigationIdentifier navigationIdentifier = new NavigationIdentifier(srcIdentifier, dstIdentifier,
                    elementIdentifier, uiStep.getUiActionId());
            navigationIdentifierList.add(navigationIdentifier);
        }
        return navigationIdentifierList;
    }

    public static Condition create(SemanticAction semanticAction, String matchingDescription) {
        Condition condition = null;
        switch (SemanticActionType.typeStringToEnum(semanticAction.getSemanticActionName())) {
            case TOGGLE:
                if (Utils.containsWord(matchingDescription, ViewUtils.ON_TEXT)) {
                    condition = new Condition(ViewUtils.ON_TEXT.toLowerCase());
                } else if (Utils.containsWord(matchingDescription, ViewUtils.OFF_TEXT)) {
                    condition = new Condition(ViewUtils.OFF_TEXT.toLowerCase());
                }
                break;
            case SEEK:
                if (Utils.containsWord(matchingDescription, ViewUtils.ON_TEXT)) {
                    condition = new Condition(ViewUtils.ON_TEXT.toLowerCase());
                } else if (Utils.containsWord(matchingDescription, ViewUtils.OFF_TEXT)) {
                    condition = new Condition(ViewUtils.OFF_TEXT.toLowerCase());
                }
                break;
            default:
                break;
        }
        return condition;
    }

    public static String getBaseSettingsScreenId(String deviceInfo) {
        final String SETTINGS_PACKAGE_NAME = "com.android.settings";
        final String SETTINGS_SCREEN_TITLE = "Settings";
        UIScreen settingsScreen = UIScreenStore.getInstance().getScreen(SETTINGS_PACKAGE_NAME, SETTINGS_SCREEN_TITLE, deviceInfo);
        return settingsScreen.getId();
    }

    public static String getBaseScreenId(String packageName, String title, String deviceInfo) {
        final String SETTINGS_PACKAGE_NAME = "com.android.settings";
        final String SETTINGS_SCREEN_TITLE = "Settings";
        UIScreen settingsScreen = UIScreenStore.getInstance().getScreen(packageName, title, deviceInfo);
        return settingsScreen.getId();
    }

}
