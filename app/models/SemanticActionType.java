package models;

public enum SemanticActionType {
    UNDEFINED("UNDEFINED"),
    TOGGLE("TOGGLE"),
    SEEK("SEEK"),
    EDIT_TEXT("EDIT"),
    SUBMIT("SUBMIT"),
    SELECT("SELECT"),
    NAVIGATE("NAVIGATE");

    private String id;

    SemanticActionType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
