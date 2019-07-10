package com.example.mvp_vm.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();

        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(
                    bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true
            );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 缩放法压缩图片大小
     */
    public static Bitmap compressBitmapByScale(Bitmap bm, float scale) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(
                    bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true
            );
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }


}
