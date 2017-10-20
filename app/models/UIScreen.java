package models;

import util.MergeUtils;
import util.Utils;


import java.util.*;

public class UIScreen {
    private String id = Utils.EMPTY_STRING;
    private String packageName = Utils.EMPTY_STRING;
    private String title = Utils.EMPTY_STRING;
    private List<UIPath> uiPaths;
    private HashMap<String, UIElement> uiElements;
    private HashMap<String, String> deviceInfo;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return getScreenId(packageName, title, deviceInfo.toString());
    }

    public UIScreen() {
        this.uiPaths = new ArrayList<>();
        this.uiElements = new HashMap<>();
        this.deviceInfo = new HashMap<>();
    }

    public void setUiPaths(List<UIPath> uiPaths) {
        this.uiPaths = uiPaths;
    }

    public void add(UIPath uiPath) {
        this.uiPaths.add(uiPath);
    }

    public void add(UIElement uiElement) {
        this.uiElements.put(uiElement.id(), uiElement);
    }

    public void update(String odlElementId, UIElement uiElement) {
        this.uiElements.remove(odlElementId);
        this.uiElements.put(uiElement.id(), uiElement);
    }

    public List<UIPath> getUiPaths() {
        return uiPaths;
    }

    public HashMap<String, UIElement> getUiElements() {
        return uiElements;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(HashMap<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public List<UIElement> findElementsInScreen(String className, String packageName, String text) {
        List<UIElement> uiElementList = new ArrayList<>();
        for (UIElement uiElement: uiElements.values()) {
            if (uiElement.getClassName().equals(className) &&
                    uiElement.getPackageName().equals(packageName) &&
                    uiElement.isMatchForText(text)) {
                uiElementList.add(uiElement);
            }
        }
        return uiElementList;
    }


    public UIElement findElementById(String elementId) {
        return uiElements.get(elementId);
    }


    public static String getScreenId(String packageName, String title, String deviceInfo) {
        String toHash = packageName.toLowerCase() + "#" + title.replaceAll(" ", "_").toLowerCase() + "#" + deviceInfo.replaceAll(" ", "_").toLowerCase();
        int hashCode = (toHash).hashCode();
        return String.valueOf(hashCode);
    }

    public boolean mergeScreen(UIScreen uiScreen) {
        if (!this.equals(uiScreen)) {
            return false;
        }
        uiElements = MergeUtils.mergeUIElements(uiElements, uiScreen.getUiElements());
        uiPaths = MergeUtils.mergeUIPaths(uiPaths, uiScreen.getUiPaths());
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIScreen)) return false;

        UIScreen uiScreen = (UIScreen) o;

        if (!packageName.equals(uiScreen.packageName)) return false;
        if (!title.equals(uiScreen.title)) return false;
        return deviceInfo.equals(uiScreen.deviceInfo);
    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + deviceInfo.hashCode();
        return result;
    }

}
