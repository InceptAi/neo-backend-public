package models;

import util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIElement {
    private String id = Utils.EMPTY_STRING;
    private String className = Utils.EMPTY_STRING;
    private String packageName = Utils.EMPTY_STRING;
    private String primaryText = Utils.EMPTY_STRING;
    private List<UIElement> childElements;
    private List<UIAction> uiActions;
    private HashMap<String, SemanticAction> semanticActions;
    private List<NavigationalAction> navigationalActions;
    private List<UIStep> lastStepToGetToThisElement;
    private int numToggleableChildren;
    private boolean isToggleable;

    public UIElement() {}

    public UIElement(String className, String packageName, String primaryText, boolean isToggleable) {
        this.className = className;
        this.packageName = packageName;
        this.uiActions = new ArrayList<>();
        this.semanticActions = new HashMap<>();
        this.navigationalActions = new ArrayList<>();
        this.childElements = new ArrayList<>();
        this.lastStepToGetToThisElement = new ArrayList<>();
        this.primaryText = primaryText;
        this.numToggleableChildren = 0;
        this.isToggleable = isToggleable;
    }

    public HashMap<String, SemanticAction> getSemanticActions() {
        return semanticActions;
    }

    public boolean isToggleable() {
        return isToggleable;
    }

    public void setIsToggleable(boolean toggleable) {
        this.isToggleable = toggleable;
    }

    public int getNumToggleableChildren() {
        return numToggleableChildren;
    }

    public void setNumToggleableChildren(int numToggleableChildren) {
        this.numToggleableChildren = numToggleableChildren;
    }

    public String getId() {
        return String.valueOf(hashCode());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChildElements(List<UIElement> childElements) {
        this.childElements = childElements;
    }

    public void setLastStepToGetToThisElement(List<UIStep> lastStepToGetToThisElement) {
        this.lastStepToGetToThisElement = lastStepToGetToThisElement;
    }

    public String id() {
        return String.valueOf(hashCode());
    }

//    public void updateSemanticActions(UIScreen uiScreen) {
//        for (UIAction uiAction: uiActions) {
//            if (semanticActions.get(uiAction.getId()) == null) {
//                SemanticAction semanticAction = SemanticAction.create(uiScreen, this, uiAction);
//                if (!SemanticAction.isUndefined(semanticAction)) {
//                    semanticActions.put(uiAction.getId(), semanticAction);
//                    SemanticActionStore.getInstance().addSemanticAction(semanticAction);
//                    Utils.printDebug("Adding semantic action: " + semanticAction);
//                }
//            }
//        }
//    }

    public void addChildren(UIElement uiElement) {
        this.childElements.add(uiElement);
        if (uiElement.isToggleable()) {
            numToggleableChildren++;
        }
        numToggleableChildren += uiElement.getNumToggleableChildren();
    }

    public void add(UIAction uiAction) {
        this.uiActions.add(uiAction);
    }

    public void add(UIStep uiStep) {
        this.lastStepToGetToThisElement.add(uiStep);
    }

    public void add(NavigationalAction navigationalAction) {
        this.navigationalActions.add(navigationalAction);
    }

    public void add(String uiActionId, SemanticAction semanticAction) {
            semanticActions.put(uiActionId, semanticAction);
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public List<UIElement> getChildElements() {
        return childElements;
    }

    public List<UIStep> getLastStepToGetToThisElement() {
        return lastStepToGetToThisElement;
    }

    public List<UIAction> getUiActions() {
        return uiActions;
    }

    public List<NavigationalAction> getNavigationalActions() {
        return navigationalActions;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    public void setUiActions(List<UIAction> uiActions) {
        this.uiActions = uiActions;
    }

    public void setSemanticActions(HashMap<String, SemanticAction> semanticActions) {
        this.semanticActions = semanticActions;
    }

    public void setNavigationalActions(List<NavigationalAction> navigationalActions) {
        this.navigationalActions = navigationalActions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIElement)) return false;

        UIElement uiElement = (UIElement) o;

        if (!className.equals(uiElement.className)) return false;
        if (!packageName.equals(uiElement.packageName)) return false;
        if (!primaryText.equals(uiElement.primaryText)) return false;
        return childElements != null ? childElements.equals(uiElement.childElements) : uiElement.childElements == null;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + primaryText.hashCode();
        int childHashCode = 0;
        for (UIElement childElement: childElements) {
            childHashCode = childHashCode + childElement.hashCode();
        }
        result = 31 * result + childHashCode;
        return result;
    }

    public String getChildText() {
        StringBuilder childTextBuilder = new StringBuilder();
        if (childElements != null && !childElements.isEmpty()) {
            for (UIElement uiElement : childElements) {
                childTextBuilder.append(uiElement.getPrimaryText());
                childTextBuilder.append(" ");
            }
        }
        return childTextBuilder.toString().trim();
    }

    public String getAllText() {
        String toReturn = Utils.EMPTY_STRING;
        String childText = getChildText();
        if (!Utils.nullOrEmpty(primaryText)) {
            toReturn = toReturn + primaryText;
        }
        if (!Utils.nullOrEmpty(childText)) {
            toReturn = toReturn + " " + childText;
        }
        return toReturn.trim();
    }

    boolean isMatchForText(String inputText) {
        String inputToTest = inputText.toLowerCase();
        String childText = getChildText();
        return (primaryText.toLowerCase().contains(inputToTest) ||
                childText.toLowerCase().contains(inputToTest));
    }

    @Override
    public String toString() {
        return "UIElement{" +
                "getId='" + id() + '\'' +
                ", className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", primaryText='" + primaryText + '\'' +
                ", childElements=" + childElements +
                ", uiActions=" + uiActions +
                ", semanticActions=" + semanticActions +
                ", navigationalActions=" + navigationalActions +
                ", lastStepToGetToThisElement=" + lastStepToGetToThisElement +
                '}';
    }

}
