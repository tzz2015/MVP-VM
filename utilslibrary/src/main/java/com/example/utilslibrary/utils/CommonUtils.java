package com.example.utilslibrary.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 16 * @ClassName: CommonUtils
 * 17 * @Description: java类作用描述
 * 18 * @Author: lyf
 * 19 * @Date: 2019-05-28 18:44
 * 20
 */
public class CommonUtils {
    private static Toast mToast;

    /**
     * 显示toast
     *
     * @param context
     * @param msg
     */
    public static void showTaost(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.setText(msg);
        mToast.show();
    }
}
