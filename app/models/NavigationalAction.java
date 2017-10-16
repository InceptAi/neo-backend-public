package models;

public class NavigationalAction {
    private String uiActionId;
    private String nextUIScreenId;

    public NavigationalAction(String uiActionId, String uiScreenId) {
        this.uiActionId = uiActionId;
        this.nextUIScreenId = uiScreenId;
    }

    public String getUiActionId() {
        return uiActionId;
    }

    public String getNextUIScreenId() {
        return nextUIScreenId;
    }
}
