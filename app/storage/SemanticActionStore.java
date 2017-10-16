package storage;

import models.*;

import java.util.*;

public class SemanticActionStore {
    private static SemanticActionStore instance;
    private Map<String, List<UIPath>> semanticActionMap = new HashMap<>();

    public static SemanticActionStore getInstance() {
        if (instance == null) {
            instance = new SemanticActionStore();
        }
        return instance;
    }

    public List<UIPath> addSemanticAction(SemanticActionType semanticActionType, UIPath uiPath) {
        String key = semanticActionType.id();
        List<UIPath> currentPathList;
        if (semanticActionMap.containsKey(key)) {
            currentPathList = semanticActionMap.get(key);
            currentPathList.add(uiPath);
        } else {
            currentPathList = new ArrayList<>();
            currentPathList.add(uiPath);
        }
        semanticActionMap.put(key, currentPathList);
        return currentPathList;
    }

    public List<UIPath> getPaths(String id) {
        return semanticActionMap.get(id);
    }

    public Set<List<UIPath>> getAllActions() {
        return new HashSet<>(semanticActionMap.values());
    }

    public boolean deleteAction(String id) {
        return semanticActionMap.remove(id) != null;
    }

}
