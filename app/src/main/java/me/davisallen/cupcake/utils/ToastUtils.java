package me.davisallen.cupcake.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Package Name:   me.davisallen.cupcake.Utils
 * Project:        Cupcake
 * Created by davis, on 8/3/17
 */

public class ToastUtils {

    static Toast sToast;

    public static void makeCustomToast(Context context, String message) {
        if (sToast != null) { sToast.cancel(); }
        sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        sToast.show();
    }
}
