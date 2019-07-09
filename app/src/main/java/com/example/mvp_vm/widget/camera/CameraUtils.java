package com.example.mvp_vm.widget.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.widget.Toast;
import com.example.mvp_vm.App;


/**
 * @author lijie
 * 相机工具类
 */
public class CameraUtils {
    /**
     * 检查是否有相机
     *
     * @param context
     * @return
     */
    public static boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * 打开相机
     *
     * @return
     */
    public static Camera openCamera() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Toast.makeText(App.getInstance().getApplicationContext(), "开启相机异常", Toast.LENGTH_SHORT).show();
        }
        return c;
    }
}
