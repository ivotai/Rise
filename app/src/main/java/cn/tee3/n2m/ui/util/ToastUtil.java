package cn.tee3.n2m.ui.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    public static final int LENGTH_NORMOL = 10000;

    public static void showLongToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), LENGTH_NORMOL).show();
    }
}
