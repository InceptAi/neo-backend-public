package models;

import java.util.ArrayList;
import java.util.List;

public class UIPath {
    private List<UIStep> uiSteps;
    private SemanticActionType semanticActionType = SemanticActionType.UNDEFINED;

    public UIPath(SemanticActionType semanticActionType, List<UIStep> uiSteps) {
        this.semanticActionType = semanticActionType;
        this.uiSteps = uiSteps;
    }

    public UIPath(List<UIStep> uiSteps) {
        this.uiSteps = uiSteps;
    }

    public UIPath(UIStep uiStep) {
        this.uiSteps = new ArrayList<>();
        this.uiSteps.add(uiStep);
    }

    public UIPath(SemanticActionType semanticActionType, UIStep uiStep) {
        this.uiSteps = new ArrayList<>();
        this.uiSteps.add(uiStep);
        this.semanticActionType = semanticActionType;
    }


    public List<UIStep> getUiSteps() {
        return uiSteps;
    }

    public SemanticActionType getSemanticActionType() {
        return semanticActionType;
    }

    public UIPath addToPath(UIStep uiStep) {
        if (uiSteps == null) {
            uiSteps = new ArrayList<>();
        }
        uiSteps.add(uiStep);
        return new UIPath(uiSteps);
    }
}
