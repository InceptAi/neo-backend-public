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

    public static SemanticActionType typeStringToEnum(String typeString) {
        switch (typeString) {
            case "TOGGLE":
                return TOGGLE;
            case "SEEK":
                return SEEK;
            case "EDIT":
                return EDIT_TEXT;
            case "SUBMIT":
                return SUBMIT;
            case "SELECT":
                return SELECT;
            case "NAVIGATE":
                return NAVIGATE;
        }
        return UNDEFINED;
    }
}
