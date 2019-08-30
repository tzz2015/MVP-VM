package com.example.mvp_vm.utils;

import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.*;
import java.net.URI;

/**
 * @author danbin
 * @version BitmapUtil.java, v 0.1 2019-06-27 22:48 danbin
 */
public class BitmapUtil {

    private static final String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * 保存图片
     * */
    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bitmap.recycle();
        }
        return false;
    }

    public static boolean saveBitmap(Bitmap bitmap, String path) {
        return saveBitmap(bitmap, new File(path));
    }

    public static boolean saveBitmap(Bitmap bitmap, Uri uri) {
        try {
            File outFile = new File(new URI(uri.toString()));
            return saveBitmap(bitmap, outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算采样率
     *
     * @param options
     * @param reqHeight
     * @param reqWidth
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public static Bitmap createRoundCornerBitmap(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的Matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle) {
            bitMap.recycle();
        }
        return newBitMap;
    }

    /**
     * 将文件转换成Base64
     *
     * @param srcPath 文件路径
     */
    public static String convertFileToBase64(String srcPath) {
        if (TextUtils.isEmpty(srcPath)) {
            return null;
        }
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(srcPath);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(BASE64_PREFIX)
                .append(result.replaceAll("\\s*", ""));
        return builder.toString();
    }

    /**
     * Bitmap转化成Bytes
     *
     * @param bitmap
     */
    public static byte[] convertBitmapToBytes(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        return baos.toByteArray();
    }

    /**
     * 按照宽高缩放图片
     *
     * @param bitmap
     * @param height
     * @param width
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h,
                matrix, true);
    }

    /**
     * 获取bitmap的大小
     *
     * @param bitmap
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
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
           e.printStackTrace();
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
           e.printStackTrace();
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
