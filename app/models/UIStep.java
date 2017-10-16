package models;

public class UIStep {
    private String uiScreenId;
    private String uiElementId;
    private String uiActionId;

    public UIStep(UIScreen uiScreen, UIElement uiElement, UIAction uiAction) {
        this.uiScreenId = uiScreen.id();
        this.uiElementId = uiElement.id();
        this.uiActionId = uiAction.id();
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
}
