package models;

import java.util.ArrayList;
import java.util.List;

public class UIPath {
    private List<UIStep> uiSteps;
    private SemanticActionType semanticActionType = SemanticActionType.UNDEFINED;

    private UIPath(UIPath uiPath) {
        this.uiSteps = new ArrayList<>();
        for (UIStep uiStep: uiPath.uiSteps) {
            uiSteps.add(UIStep.copyStep(uiStep));
        }
        this.semanticActionType = uiPath.semanticActionType;
    }

    //Factory
    private static UIPath copyPath(UIPath uiPath){
        if (uiPath == null) {
            return null;
        }
        return new UIPath(uiPath);
    }

    public UIPath(SemanticActionType semanticActionType, UIStep uiStep) {
        this.uiSteps = new ArrayList<>();
        this.uiSteps.add(uiStep);
        this.semanticActionType = semanticActionType;
    }

    public UIPath(SemanticActionType semanticActionType) {
        this.semanticActionType = semanticActionType;
        this.uiSteps = new ArrayList<>();
    }

    public List<UIStep> getUiSteps() {
        return uiSteps;
    }

    public SemanticActionType getSemanticActionType() {
        return semanticActionType;
    }

    public static UIPath createNewPath(UIPath uiPath, UIStep uiStep) {
        if (uiPath == null || uiStep == null) {
            return null;
        }
        UIPath newUIPath = UIPath.copyPath(uiPath);
        for (UIStep currentStep: newUIPath.getUiSteps()) {
            if (currentStep.equals(uiStep)) {
                //This new step already exists in the path, so can't add again. invalid path.
                return null;
            }
        }
        newUIPath.uiSteps.add(UIStep.copyStep(uiStep));
        return newUIPath;
    }

    public String getId() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIPath)) return false;

        UIPath uiPath = (UIPath) o;

        return uiSteps.equals(uiPath.uiSteps);
    }

    @Override
    public int hashCode() {
        return uiSteps.hashCode();
    }


    @Override
    public String toString() {
        return "UIPath{" +
                "uiSteps=" + uiSteps +
                ", semanticActionType=" + semanticActionType +
                '}';
    }

    public int length() {
        return uiSteps.size();
    }


    public static UIPath findSubPath(UIPath uiPath, String srcScreenId) {
        if (uiPath == null) {
            return null;
        }
        int pathLength = uiPath.length();
        int srcIndex = -1;
        for (int stepIndex = 0; stepIndex < pathLength; stepIndex++) {
            UIStep uiStep = uiPath.uiSteps.get(stepIndex);
            if (uiStep.getSrcScreenId().equalsIgnoreCase(srcScreenId)) {
                srcIndex = stepIndex;
            }
        }
        if (srcIndex == -1) {
            //Didn't find src, return null
            return null;
        }
        UIPath subPath = new UIPath(SemanticActionType.NAVIGATE);
        for (int stepIndex = srcIndex; stepIndex < pathLength; stepIndex++) {
            UIStep uiStep = uiPath.uiSteps.get(stepIndex);
            subPath.uiSteps.add(UIStep.copyStep(uiStep));
        }
        return subPath;
    }
}
