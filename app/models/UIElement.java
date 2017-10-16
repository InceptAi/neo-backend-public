package models;

import util.Utils;
import util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class UIElement {


    private String className = Utils.EMPTY_STRING;
    private String packageName = Utils.EMPTY_STRING;
    private String primaryText = Utils.EMPTY_STRING;
    private List<UIElement> childElements;
    private List<UIAction> uiActions;
    private List<SemanticAction> semanticActions;
    private List<NavigationalAction> navigationalActions;
    private List<UIStep> lastStepToGetToThisElement;

    public UIElement() {}

    public UIElement(String className, String packageName, String primaryText) {
        this.className = className;
        this.packageName = packageName;
        this.uiActions = new ArrayList<>();
        this.semanticActions = new ArrayList<>();
        this.navigationalActions = new ArrayList<>();
        this.childElements = new ArrayList<>();
        this.lastStepToGetToThisElement = new ArrayList<>();
        this.primaryText = ViewUtils.getTextBasedOnClass(className, primaryText);
    }

    public String id() {
        return String.valueOf(hashCode());
    }

    public UIElement(RenderingView renderingView) {
        this(renderingView.getClassName(), renderingView.getPackageName(), renderingView.getOverallText());
        //Add UIActions based on view
        if (renderingView.isClickable()) {
            uiActions.add(UIAction.CLICK);
        }
        if (renderingView.isCheckable()) {
            uiActions.add(UIAction.CHECK);
        }
    }


    void updateSemanticActions(String screenTitle) {
        for (UIAction uiAction: uiActions) {
            SemanticAction semanticAction = new SemanticAction(uiAction, className, screenTitle, getAllText());
            if (semanticAction.getSemanticActionType() != SemanticActionType.UNDEFINED) {
                semanticActions.add(semanticAction);
            }
        }
    }

    void addChildren(UIElement uiElement) {
        this.childElements.add(uiElement);
    }

    public void add(UIAction uiAction) {
        this.uiActions.add(uiAction);
    }

    public void add(SemanticAction semanticAction) {
        this.semanticActions.add(semanticAction);
    }

    void add(UIStep uiStep) {
        this.lastStepToGetToThisElement.add(uiStep);
    }

    void add(NavigationalAction navigationalAction) {
        this.navigationalActions.add(navigationalAction);
    }

    String getClassName() {
        return className;
    }

    String getPackageName() {
        return packageName;
    }

    String getPrimaryText() {
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

    public List<SemanticAction> getSemanticActions() {
        return semanticActions;
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

    public void setSemanticActions(List<SemanticAction> semanticActions) {
        this.semanticActions = semanticActions;
    }

    public void setNavigationalActions(List<NavigationalAction> navigationalActions) {
        this.navigationalActions = navigationalActions;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!UIElement.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final UIElement other = (UIElement) obj;

        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        String textToHash = className.toLowerCase() + "#" + primaryText.replace(" ", "_").toLowerCase() + "#" + getChildText();
        return textToHash.hashCode();
    }


    //Helper methods
    public boolean isTextView() {
        return ViewUtils.TEXT_VIEW_CLASS_NAME.equals(className);
    }

    public boolean isImage() {
        return ViewUtils.IMAGE_CLASS_NAME.equals(className);
    }

    public boolean isLinearRelativeOrFrameLayout() {
        return ViewUtils.LINEAR_LAYOUT_CLASS_NAME.equals(className) || ViewUtils.RELATIVE_LAYOUT_CLASS_NAME.equals(className) ||
                ViewUtils.FRAME_LAYOUT_CLASS_NAME.equals(className);
    }

    public boolean isCheckBox() {
        return ViewUtils.CHECK_BOX_CLASS_NAME.equals(className);
    }



    private String getChildText() {
        StringBuilder childTextBuilder = new StringBuilder();
        if (childElements != null && !childElements.isEmpty()) {
            for (UIElement uiElement : childElements) {
                childTextBuilder.append(uiElement.getPrimaryText());
            }
        }
        return childTextBuilder.toString();
    }

    private String getAllText() {
        String toReturn = Utils.EMPTY_STRING;
        String childText = getChildText();
        if (!Utils.nullOrEmpty(primaryText)) {
            toReturn = toReturn + primaryText;
        }
        if (!Utils.nullOrEmpty(childText)) {
            toReturn = toReturn + " " + childText;
        }
        return toReturn;
    }

    boolean isMatchForText(String inputText) {
        String inputToTest = inputText.toLowerCase();
        String childText = getChildText();
        return (primaryText.toLowerCase().contains(inputToTest) ||
                childText.toLowerCase().contains(inputToTest));
    }


}
