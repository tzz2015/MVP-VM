package com.example.mvp_vm.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.mvp_vm.widget.camera.SensorControl.TAG;

/**
 * 16 * @ClassName: Utils
 * 17 * @Description: java类作用描述
 * 18 * @Author: lyf
 * 19 * @Date: 2019-07-02 08:53
 * 20
 */
public class Utils {
    public static int intToFloat(float value) {
        return (int) (value + 0.5);
    }

    /**
     * 获取bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        FileOutputStream fileOutputStream = null;
        try {
            File outFile = new File(path);
            fileOutputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Log.e(TAG, "width:" + width + "height:" + height);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
    }

}
