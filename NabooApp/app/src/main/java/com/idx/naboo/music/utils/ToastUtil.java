package com.idx.naboo.music.utils;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by sunny on 18-4-24.
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}
