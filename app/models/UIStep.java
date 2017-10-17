package models;

import util.Utils;

public class UIStep {

    public enum UIStepType {
        TO_ANOTHER_SCREEN("INTER_SCREEN"),
        WITHIN_SAME_SCREEN("WITHIN_SAME_SCREEN"),
        UNDEFINED("UNDEFINED");

        private String id;

        UIStepType(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    private String uiScreenId;
    private String uiElementId;
    private String uiActionId;
    private String uiStepTypeId;

    public UIStep(UIScreen uiScreen, UIElement uiElement, UIAction uiAction, UIStepType uiStepType) {
        this.uiScreenId = uiScreen.id();
        this.uiElementId = uiElement.id();
        this.uiActionId = uiAction.id();
        this.uiStepTypeId = uiStepType.id();
    }

    public UIStep() {
        this.uiElementId = Utils.EMPTY_STRING;
        this.uiScreenId = Utils.EMPTY_STRING;
        this.uiActionId = Utils.EMPTY_STRING;
        this.uiStepTypeId = UIStepType.UNDEFINED.id();
    }

    public String getUiScreenId() {
        return uiScreenId;
    }

    public String getUiElementId() {
        return uiElementId;
    }

    public String getUiActionId() {
        return uiActionId;
    }

    public String getUiStepTypeId() {
        return uiStepTypeId;
    }

    public String getId() {
        String idToHash = uiScreenId + "#" + uiElementId + "#" + uiActionId;
        return String.valueOf(idToHash.hashCode());
    }

    public boolean isUndefined() {
        return uiStepTypeId.equalsIgnoreCase(UIStepType.UNDEFINED.id());
    }

    public boolean isInterScreenStep() {
        return uiStepTypeId.equalsIgnoreCase(UIStepType.TO_ANOTHER_SCREEN.id());
    }

    public boolean isWithinSameScreen() {
        return uiStepTypeId.equalsIgnoreCase(UIStepType.WITHIN_SAME_SCREEN.id());
    }
}
