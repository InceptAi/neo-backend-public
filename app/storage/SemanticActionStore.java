package storage;

import models.*;

import java.util.*;

public class SemanticActionStore {
    private static SemanticActionStore instance;
    private Map<String, SemanticAction> semanticActionMap = new HashMap<>();

    public static SemanticActionStore getInstance() {
        if (instance == null) {
            instance = new SemanticActionStore();
        }
        return instance;
    }

    public SemanticAction addSemanticAction(SemanticAction semanticAction) {
        String key = semanticAction.getId();
        semanticActionMap.put(key, semanticAction);
        return semanticAction;
    }

    public SemanticAction getAction(String id) {
        return semanticActionMap.get(id);
    }

    public Set<SemanticAction> getAllActions() {
        return new HashSet<>(semanticActionMap.values());
    }

    public boolean deleteAction(String id) {
        return semanticActionMap.remove(id) != null;
    }

}
