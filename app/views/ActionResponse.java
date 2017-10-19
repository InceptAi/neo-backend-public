package views;

import models.UIPath;
import models.UIStep;

import java.util.ArrayList;
import java.util.List;

public class ActionResponse {

    private List<ActionDetails> actionList;

    public List<ActionDetails> getActionList() {
        return actionList;
    }

    public void setActionList(List<ActionDetails> actionList) {
        this.actionList = actionList;
    }

    public ActionResponse(List<ActionDetails> actionDetails) {
        this.actionList = actionDetails;
    }

    public ActionResponse() {
        this.actionList = new ArrayList<>();
    }

}
