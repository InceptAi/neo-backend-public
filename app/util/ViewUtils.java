package util;

import models.RenderingView;

public class ViewUtils {
    public static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";
    public static final String IMAGE_CLASS_NAME = "android.widget.ImageButton";
    public static final String LINEAR_LAYOUT_CLASS_NAME = "android.widget.LinearLayout";
    public static final String FRAME_LAYOUT_CLASS_NAME = "android.widget.FrameLayout";
    public static final String RELATIVE_LAYOUT_CLASS_NAME = "android.widget.RelativeLayout";
    public static final String CHECK_BOX_CLASS_NAME = "android.widget.CheckBox";
    public static final String SWITCH_CLASS_NAME = "android.widget.Switch";
    public static final String SEEK_BAR_CLASS_NAME = "android.widget.SeekBar";
    public static final String CHECKED_TEXT_VIEW_CLASS_NAME = "android.widget.CheckedTextView";
    public static final String EDIT_TEXT_VIEW_CLASS_NAME = "android.widget.EditText";

    private static final String VIEWPAGER_CLASS = "ViewPager";
    private static final String NULL_STRING = "null";

    //On/off
    private static String ON_TEXT = "on";
    private static String OFF_TEXT = "off";

    //Texts
    private static String SWITCH_TEXT = "SWITCH_ON_OFF";
    private static String ON_OFF_TEXT = "TEXT_ON_OFF";
    private static String CHECK_BOX_TEXT = "CHECK_BOX_ON_OFF";
    private static String SEEK_BAR_TEXT = "SEEK_BAR_VALUE";
    private static String EDIT_TEXT_VIEW_TEXT = "EDIT_TEXT_VIEW_TEXT";

    private ViewUtils() {}

    public static boolean isTextView(RenderingView renderingView) {
        return TEXT_VIEW_CLASS_NAME.equals(renderingView.getClassName());
    }

    public static boolean isImage(RenderingView renderingView) {
        return IMAGE_CLASS_NAME.equals(renderingView.getClassName());
    }

    public static boolean isNotNullValuedString(String target) {
        return !NULL_STRING.equals(target);
    }

    public static boolean isLinearRelativeOrFrameLayout(RenderingView renderingView) {
        String className = renderingView.getClassName();
        return LINEAR_LAYOUT_CLASS_NAME.equals(className) || RELATIVE_LAYOUT_CLASS_NAME.equals(className) || FRAME_LAYOUT_CLASS_NAME.equals(className);
    }

    public static boolean isCheckBox(RenderingView renderingView) {
        return CHECK_BOX_CLASS_NAME.equals(renderingView.getClassName());
    }

    public static String getTextBasedOnClass(String className, String text) {
        String textToReturn = Utils.EMPTY_STRING;
        switch (className) {

            case ViewUtils.SWITCH_CLASS_NAME:
                textToReturn = SWITCH_TEXT;
                break;

            case ViewUtils.SEEK_BAR_CLASS_NAME:
                textToReturn = SEEK_BAR_TEXT;
                break;

            case ViewUtils.CHECK_BOX_CLASS_NAME:
                textToReturn = CHECK_BOX_TEXT;
                break;

            case ViewUtils.EDIT_TEXT_VIEW_CLASS_NAME:
                textToReturn = EDIT_TEXT_VIEW_TEXT;
                break;

            case ViewUtils.TEXT_VIEW_CLASS_NAME:
                textToReturn = ViewUtils.isTextOnOrOff(text) ? ON_OFF_TEXT : text;
                break;

            case ViewUtils.IMAGE_CLASS_NAME:
            case ViewUtils.CHECKED_TEXT_VIEW_CLASS_NAME:
                textToReturn = text;
                break;

            case ViewUtils.FRAME_LAYOUT_CLASS_NAME:
            case ViewUtils.RELATIVE_LAYOUT_CLASS_NAME:
            case ViewUtils.LINEAR_LAYOUT_CLASS_NAME:
            default:
                break;
        }
        return textToReturn;
    }

    public static boolean isTemplateText(String text) {
        String[] templateTexts = {SWITCH_TEXT, SEEK_BAR_TEXT, EDIT_TEXT_VIEW_TEXT, CHECK_BOX_TEXT};
        for (String templateText: templateTexts) {
            if (templateText.trim().equalsIgnoreCase(text.trim())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTextOnOrOff(String text) {
        return text.toLowerCase().equals(ON_TEXT.toLowerCase())
                || text.toLowerCase().equals(OFF_TEXT.toLowerCase());
    }

}

