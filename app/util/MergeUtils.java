package util;

import models.*;

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

    public static List<UIPath> getUIPathBasedOnLastScreenPath(List<UIPath> lastScreenUIPaths, UIStep uiStep) {
        List<UIPath> uiPathList = new ArrayList<>();
        if (lastScreenUIPaths == null || lastScreenUIPaths.isEmpty()) {
            uiPathList.add(new UIPath(SemanticActionType.NAVIGATE, uiStep));
        } else {
            for (UIPath uiPath: lastScreenUIPaths) {
                UIPath updatedPath = UIPath.createNewPath(uiPath, uiStep);
                if (updatedPath != null) {
                    uiPathList.add(updatedPath);
                }
            }
        }
        return uiPathList;
    }


    public static List<String> getDifferingUIElementIdsInCurrentScreen(Set<String> currentElementIds, Set<String> otherElementIds) {
        List<String> differingIds = new ArrayList<>();
        for (String currentId: currentElementIds) {
            if (!otherElementIds.contains(currentId)) {
                differingIds.add(currentId);
            }
        }
        return differingIds;
    }

}