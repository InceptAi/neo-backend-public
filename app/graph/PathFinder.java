package graph;

import models.UIPath;
import models.UIScreen;

public abstract class PathFinder {
    public abstract UIPath findPathBetweenScreens(UIScreen srcScreen, UIScreen dstScreen);
}
