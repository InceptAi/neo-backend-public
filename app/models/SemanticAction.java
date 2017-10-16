package models;

import util.Utils;
import util.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SemanticAction {
    private SemanticActionType semanticActionType;
    private String uiActionId;
    private String semanticActionName;
    private List<String> semanticActionKeywords;

    public SemanticAction(UIAction uiAction, SemanticActionType semanticActionType) {
        this.uiActionId = uiAction.id();
        this.semanticActionType = semanticActionType;
    }

    public SemanticAction(UIAction uiAction, String className,
                          String screenTitle, String elementText) {
        String actionName = Utils.EMPTY_STRING;
        SemanticActionType actionType = SemanticActionType.UNDEFINED;
        switch (className) {
            case ViewUtils.SWITCH_CLASS_NAME:
            case ViewUtils.CHECK_BOX_CLASS_NAME:
                actionName = ViewUtils.isTemplateText(elementText) ? screenTitle : elementText;
                actionType = SemanticActionType.TOGGLE;
                break;
            case ViewUtils.SEEK_BAR_CLASS_NAME:
                actionName = elementText.equals(Utils.EMPTY_STRING) ? screenTitle : elementText;
                actionType = SemanticActionType.SEEK;
                break;
            default:
                break;
        }
        this.semanticActionName = actionName;
        this.semanticActionType = actionType;
        this.uiActionId = uiAction.id();
        this.semanticActionKeywords = new ArrayList<>();
        if (!elementText.equals(Utils.EMPTY_STRING)) {
            semanticActionKeywords.addAll(Utils.splitSentenceToWords(elementText));
        }
        if (!screenTitle.equals(Utils.EMPTY_STRING)) {
            semanticActionKeywords.addAll(Utils.splitSentenceToWords(screenTitle));
        }
    }


    public SemanticActionType getSemanticActionType() {
        return semanticActionType;
    }

    public String getUiActionId() {
        return uiActionId;
    }

    public String getSemanticActionName() {
        return semanticActionName;
    }

    public List<String> getSemanticActionKeywords() {
        return semanticActionKeywords;
    }
}
