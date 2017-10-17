package models;

import util.Utils;
import util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class SemanticAction {
    private String semanticActionDescription;
    private String semanticActionName;
    private String uiScreenId;
    private String uiElementId;
    private String uiActionId;
    private List<String> semanticActionKeywords;

    public SemanticAction(String uiScreenId, String uiElementId,
                          String uiActionId, String className,
                          String screenTitle, String elementText) {
        String actionDescription = Utils.EMPTY_STRING;
        String actionName = SemanticActionType.UNDEFINED.id();
        switch (className) {
            case ViewUtils.SWITCH_CLASS_NAME:
            case ViewUtils.CHECK_BOX_CLASS_NAME:
                actionDescription = ViewUtils.isTemplateText(elementText) ? screenTitle : elementText;
                actionName = SemanticActionType.TOGGLE.id();
                break;
            case ViewUtils.SEEK_BAR_CLASS_NAME:
                actionDescription = elementText.equals(Utils.EMPTY_STRING) ? screenTitle : elementText;
                actionName = SemanticActionType.SEEK.id();
                break;
            case ViewUtils.BUTTON_CLASS_NAME:
            case ViewUtils.IMAGE_BUTTON_CLASS_NAME:
                //TODO validate this
                actionDescription = screenTitle;
                actionName = elementText;
                break;
            default:
                break;
        }
        this.semanticActionDescription = actionDescription;
        this.semanticActionName = actionName;
        this.uiScreenId = uiScreenId;
        this.uiElementId = uiElementId;
        this.uiActionId = uiActionId;
        this.semanticActionKeywords = new ArrayList<>();
        if (!elementText.equals(Utils.EMPTY_STRING)) {
            semanticActionKeywords.addAll(Utils.splitSentenceToWords(elementText));
        }
        if (!screenTitle.equals(Utils.EMPTY_STRING)) {
            semanticActionKeywords.addAll(Utils.splitSentenceToWords(screenTitle));
        }
    }

    public String getSemanticActionName() {
        return semanticActionName;
    }

    public void setSemanticActionName(String semanticActionName) {
        this.semanticActionName = semanticActionName;
    }

    public void setUiActionId(String uiActionId) {
        this.uiActionId = uiActionId;
    }

    public void setSemanticActionDescription(String semanticActionDescription) {
        this.semanticActionDescription = semanticActionDescription;
    }

    public void setSemanticActionKeywords(List<String> semanticActionKeywords) {
        this.semanticActionKeywords = semanticActionKeywords;
    }

    public String getUiScreenId() {
        return uiScreenId;
    }

    public void setUiScreenId(String uiScreenId) {
        this.uiScreenId = uiScreenId;
    }

    public String getUiElementId() {
        return uiElementId;
    }

    public void setUiElementId(String uiElementId) {
        this.uiElementId = uiElementId;
    }

    public String getUiActionId() {
        return uiActionId;
    }

    public String getSemanticActionDescription() {
        return semanticActionDescription;
    }

    public List<String> getSemanticActionKeywords() {
        return semanticActionKeywords;
    }

    public String getId() {
        return String.valueOf(hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!SemanticAction.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final SemanticAction other = (SemanticAction) obj;

        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        String toHash = semanticActionDescription + "#" + semanticActionName + "#" + uiScreenId + "#" + uiElementId + "#" + uiActionId;
        return toHash.toLowerCase().hashCode();
    }
}
