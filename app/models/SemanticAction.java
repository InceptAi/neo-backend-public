package models;

import util.Utils;
import util.ViewUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SemanticAction {
    private String screenTitle;
    private String packageName;
    private String semanticActionDescription;
    private String semanticActionName;
    private String uiScreenId;
    private String uiElementId;
    private String uiActionId;

    private SemanticAction(UIScreen uiScreen, UIElement uiElement, UIAction uiAction) {
        //Utils.printDebug("Adding semantic action with className:" + className + " elementText:" + elementText);
        String actionName = SemanticActionType.UNDEFINED.id();
        switch (uiElement.getClassName()) {
            case ViewUtils.LINEAR_LAYOUT_CLASS_NAME:
            case ViewUtils.RELATIVE_LAYOUT_CLASS_NAME:
            case ViewUtils.FRAME_LAYOUT_CLASS_NAME:
                //We have a ll, rl, or fl which is clickable
                //Check if element has a child switch/checkbox -- assign toggle
                if (uiElement.getNumToggleableChildren() == 1) {
                    actionName = SemanticActionType.TOGGLE.id();
                }
//              else if (uiElement.getNumToggleableChildren() > 1){
//                    //Not sure how to handle this -- leave it undefined
//              }
                break;
            case ViewUtils.CHECKED_TEXT_VIEW_CLASS_NAME:
                actionName = SemanticActionType.TOGGLE.id();
                break;
//            case ViewUtils.SWITCH_CLASS_NAME:
//            case ViewUtils.CHECK_BOX_CLASS_NAME:
//                actionDescription = ViewUtils.isTemplateText(elementText) ? screenTitle : elementText;
//                actionName = SemanticActionType.TOGGLE.id();
//                break;
            case ViewUtils.SEEK_BAR_CLASS_NAME:
                actionName = SemanticActionType.SEEK.id();
                break;
            case ViewUtils.BUTTON_CLASS_NAME:
            case ViewUtils.IMAGE_BUTTON_CLASS_NAME:
                //TODO validate this
                actionName = uiElement.getAllText();
                break;
            default:
                break;
        }
        this.semanticActionDescription = uiElement.getAllText();
        this.semanticActionName = actionName;
        this.uiScreenId = uiScreen.id();
        this.uiElementId = uiElement.id();
        this.uiActionId = uiAction.id();
        this.screenTitle = uiScreen.getTitle();
        this.packageName = uiScreen.getPackageName();
    }


    public SemanticAction() {
        screenTitle = Utils.EMPTY_STRING;
        semanticActionDescription = SemanticActionType.UNDEFINED.id();
        semanticActionName = Utils.EMPTY_STRING;
        uiScreenId = Utils.EMPTY_STRING;
        uiElementId = Utils.EMPTY_STRING;
        uiActionId = Utils.EMPTY_STRING;
        screenTitle = Utils.EMPTY_STRING;
        packageName = Utils.EMPTY_STRING;
    }

    /**
     * Factory constructor to create an instance
     * @return Instance of SemanticAction or null on error.
     */
    public static SemanticAction create(UIScreen uiScreen, UIElement uiElement, UIAction uiAction) {
        if (uiAction.equals(UIAction.CLICK) || uiAction.equals(UIAction.SELECT)) {
            return new SemanticAction(uiScreen, uiElement, uiAction);
        }
        return new SemanticAction();
    }


    public String getSemanticActionName() {
        return semanticActionName;
    }

    public void setSemanticActionName(String semanticActionName) {
        this.semanticActionName = semanticActionName;
    }

    public void setUiActionId(String uiActionId) {
        this.uiActionId = uiActionId;
    }

    public void setSemanticActionDescription(String semanticActionDescription) {
        this.semanticActionDescription = semanticActionDescription;
    }

    public String getUiScreenId() {
        return uiScreenId;
    }

    public void setUiScreenId(String uiScreenId) {
        this.uiScreenId = uiScreenId;
    }

    public String getUiElementId() {
        return uiElementId;
    }

    public void setUiElementId(String uiElementId) {
        this.uiElementId = uiElementId;
    }

    public String getUiActionId() {
        return uiActionId;
    }

    public String getSemanticActionDescription() {
        return semanticActionDescription;
    }

    public String getId() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!SemanticAction.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final SemanticAction other = (SemanticAction) obj;

        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        String toHash = semanticActionDescription + "#" + semanticActionName + "#" + uiScreenId + "#" + uiElementId + "#" + uiActionId;
        return toHash.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "SemanticAction{" +
                "screenTitle='" + screenTitle + '\'' +
                ", packageName='" + packageName + '\'' +
                ", semanticActionDescription='" + semanticActionDescription + '\'' +
                ", semanticActionName='" + semanticActionName + '\'' +
                ", uiScreenId='" + uiScreenId + '\'' +
                ", uiElementId='" + uiElementId + '\'' +
                ", uiActionId='" + uiActionId + '\'' +
                '}';
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public void setScreenTitle(String screenTitle) {
        this.screenTitle = screenTitle;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static boolean isUndefined(SemanticAction semanticAction) {
        return semanticAction.getSemanticActionName().equalsIgnoreCase(SemanticActionType.UNDEFINED.id());
    }
}
