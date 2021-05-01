package cn.tee3.n2m.ui.util;

import android.widget.TextView;

public class TextViewUtil {

    public static boolean isNullOrEmpty(TextView textView) {
        if (textView != null && textView.getText() != null) {
            String text = textView.getText().toString().trim();
            if (text != null && !text.equals("")) {
                return true;
            }
        }
        return false;
    }
}
