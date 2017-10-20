package graph;


import models.UIPath;
import models.UIScreen;
import scala.Int;

import java.util.List;

public class SimplePathFinder extends PathFinder {

    @Override
    public UIPath findPathBetweenScreens(UIScreen srcScreen, UIScreen dstScreen) {
        List<UIPath> uiPathList = dstScreen.getUiPaths();
        UIPath shortestSubPath = null;
        int shortestSubPathLength = Int.MaxValue();
        for (UIPath uiPath: uiPathList) {
            UIPath subPath = UIPath.findSubPath(uiPath, srcScreen.getId());
            if (subPath != null && subPath.length() < shortestSubPathLength) {
                shortestSubPath = subPath;
                shortestSubPathLength = subPath.length();
            }
        }
        return shortestSubPath;
    }
}
