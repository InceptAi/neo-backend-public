package views;

import java.util.ArrayList;
import java.util.List;

public class ElementIdentifier {
    private String className;
    private String packageName;
    private List<String> primaryTextList;
    private List<String> childTextList;

    public ElementIdentifier(String className, String packageName, String primaryText, String childText) {
        this.className = className;
        this.packageName = packageName;
        this.primaryTextList = new ArrayList<>();
        this.childTextList = new ArrayList<>();
        this.primaryTextList.add(primaryText);
        this.childTextList.add(childText);
    }

    public ElementIdentifier(String className, String packageName, List<String> primaryTextList, List<String> childTextList) {
        this.className = className;
        this.packageName = packageName;
        this.primaryTextList = primaryTextList;
        this.childTextList = childTextList;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getPrimaryTextList() {
        return primaryTextList;
    }

    public void setPrimaryTextList(List<String> primaryTextList) {
        this.primaryTextList = primaryTextList;
    }

    public List<String> getChildTextList() {
        return childTextList;
    }

    public void setChildTextList(List<String> childTextList) {
        this.childTextList = childTextList;
    }
}
