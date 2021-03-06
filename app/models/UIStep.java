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

    private String srcScreenId;
    private String uiElementId;
    private String uiActionId;
    private String uiStepTypeId;
    private String dstScreenId;

    public UIStep(String srcScreenId, String dstScreenId,
                  String uiElementId, String uiActionId,
                  String uiStepTypeId) {
        this.srcScreenId = srcScreenId;
        this.dstScreenId = dstScreenId;
        this.uiElementId = uiElementId;
        this.uiActionId = uiActionId;
        this.uiStepTypeId = uiStepTypeId;
    }

    public UIStep() {
        this.uiElementId = Utils.EMPTY_STRING;
        this.srcScreenId = Utils.EMPTY_STRING;
        this.uiActionId = Utils.EMPTY_STRING;
        this.dstScreenId = Utils.EMPTY_STRING;
        this.uiStepTypeId = UIStepType.UNDEFINED.id();
    }

    public UIStep(UIStep uiStep) {
        this.srcScreenId = uiStep.srcScreenId;
        this.dstScreenId = uiStep.dstScreenId;
        this.uiElementId = uiStep.uiElementId;
        this.uiActionId = uiStep.uiActionId;
        this.uiStepTypeId = uiStep.uiStepTypeId;
    }

    //Factory constructor
    public static UIStep copyStep(UIStep uiStep) {
        if (uiStep == null) {
            return null;
        }
        return  new UIStep(uiStep);
    }

    public String getDstScreenId() {
        return dstScreenId;
    }

    public String getSrcScreenId() {
        return srcScreenId;
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
        String idToHash = srcScreenId + "#" + uiElementId + "#" + uiActionId;
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

    @Override
    public String toString() {
        return "UIStep{" +
                "srcScreenId='" + srcScreenId + '\'' +
                ", uiElementId='" + uiElementId + '\'' +
                ", uiActionId='" + uiActionId + '\'' +
                ", uiStepTypeId='" + uiStepTypeId + '\'' +
                ", dstScreenId='" + dstScreenId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIStep)) return false;

        UIStep uiStep = (UIStep) o;

        if (!srcScreenId.equals(uiStep.srcScreenId)) return false;
        if (!uiElementId.equals(uiStep.uiElementId)) return false;
        if (!uiActionId.equals(uiStep.uiActionId)) return false;
        if (!uiStepTypeId.equals(uiStep.uiStepTypeId)) return false;
        return dstScreenId.equals(uiStep.dstScreenId);
    }

    @Override
    public int hashCode() {
        int result = srcScreenId.hashCode();
        result = 31 * result + uiElementId.hashCode();
        result = 31 * result + uiActionId.hashCode();
        result = 31 * result + uiStepTypeId.hashCode();
        result = 31 * result + dstScreenId.hashCode();
        return result;
    }
}
