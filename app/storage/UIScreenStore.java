package storage;

import models.UIScreen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UIScreenStore {
    private static UIScreenStore instance;
    private Map<String, UIScreen> uiScreenMap = new HashMap<>();

    public static UIScreenStore getInstance() {
        if (instance == null) {
            instance = new UIScreenStore();
        }
        return instance;
    }

    public UIScreen addScreen(UIScreen uiScreen) {
        String key = uiScreen.id();
        uiScreenMap.put(key, uiScreen);
        return uiScreen;
    }

    public UIScreen getScreen(String packageName, String title) {
        return uiScreenMap.get(UIScreen.getIdFromPackageAndTitle(packageName, title ));
    }

    public UIScreen getScreen(String id) {
        return uiScreenMap.get(id);
    }


    public Set<UIScreen> getAllScreens() {
        return new HashSet<>(uiScreenMap.values());
    }

    public UIScreen updateScreen(UIScreen uiScreen) {
        String id = uiScreen.id();
        if (uiScreenMap.containsKey(id)) {
            uiScreenMap.put(id, uiScreen);
            return uiScreen;
        }
        return null;
    }

    public boolean deleteScreen(String id) {
        return uiScreenMap.remove(id) != null;
    }
}
