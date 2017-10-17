package util;

import models.UIElement;
import models.UIPath;
import models.UIScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MergeUtils {

    public static List<UIPath> mergeUIPaths(List<UIPath> uiPathListOld, List<UIPath> uiPathListUpdated) {
        List<UIPath> newUIPathList = new ArrayList<>();
        newUIPathList.addAll(uiPathListOld);
        newUIPathList.addAll(uiPathListUpdated);
        return newUIPathList;
    }

    public static HashMap<String, UIElement> mergeUIElements(HashMap<String, UIElement> uiElementHashMapOld,
                                                             HashMap<String, UIElement> uiElementHashMapUpdated) {
        HashMap<String, UIElement> mergedHashMap = new HashMap<>();
        mergedHashMap.putAll(uiElementHashMapOld);
        mergedHashMap.putAll(uiElementHashMapUpdated);
        return mergedHashMap;
    }

}