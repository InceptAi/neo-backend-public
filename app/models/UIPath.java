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

    public String getId() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!UIPath.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final UIPath other = (UIPath) obj;

        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        StringBuilder idBuilder = new StringBuilder();
        for (UIStep uiStep: uiSteps) {
            idBuilder.append(uiStep.getId());
        }
        return idBuilder.toString().hashCode();
    }

}
