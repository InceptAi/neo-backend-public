package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrawlingInput {
    private int numViews;
    private int rootHeight;
    private int rootWidth;
    private String rootTitle;
    private String lastScreenTitle;
    private String lastScreenPackageName;
    private RenderingView lastViewClicked;
    private String rootPackageName;
    private String lastUIAction;
    private HashMap<Long, RenderingView> viewMap;

    public CrawlingInput(){}

    public CrawlingInput(int numViews, int rootHeight, int rootWidth, String rootTitle,
                         String lastScreenTitle, String lastScreenPackageName,
                         RenderingView lastViewClicked, String lastUIAction,
                         String rootPackageName, HashMap<Long, RenderingView> viewMap) {
        this.numViews = numViews;
        this.rootHeight = rootHeight;
        this.rootWidth = rootWidth;
        this.rootTitle = rootTitle;
        this.lastScreenTitle = lastScreenTitle;
        this.lastViewClicked = lastViewClicked;
        this.lastUIAction = lastUIAction;
        this.lastScreenPackageName = lastScreenPackageName;
        this.rootPackageName = rootPackageName;
        this.viewMap = viewMap;
    }

    public String getRootPackageName() {
        return rootPackageName;
    }

    public String getRootTitle() {
        return rootTitle;
    }

    public int getNumViews() {
        return numViews;
    }

    public int getRootHeight() {
        return rootHeight;
    }

    public int getRootWidth() {
        return rootWidth;
    }

    public HashMap<Long, RenderingView> getViewMap() {
        return viewMap;
    }

    public List<RenderingView> getRenderingViewList() {
        List<RenderingView> list = new ArrayList<RenderingView>(viewMap.values());
        return list;
    }

    public boolean isEmpty() {
        return viewMap.isEmpty();
    }

    public long getParentViewId(long viewId) {
        if (viewMap.get(viewId) != null) {
            return viewMap.get(viewId).getParentViewId();
        }
        return 0;
    }

    public String getLastScreenPackageName() {
        return lastScreenPackageName;
    }

    public RenderingView getLastViewClicked() {
        return lastViewClicked;
    }

    public String getLastUIAction() {
        return lastUIAction;
    }

    public String getLastScreenTitle() {
        return lastScreenTitle;
    }
}
