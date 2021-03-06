package com.example.mvp_vm.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author lijie on 2018/12/4
 * 更改状态栏的颜色
 */
public class StatusBarUtils {

    /**
     * 修改状态栏方法, 为了实现保持跟原有逻辑统一，不要采用该方法
     *
     * @param activity
     * @param colorId
     */
    public static void initStateBar(Activity activity, int colorId) {
        int color = ContextCompat.getColor(activity, colorId);
        initStateBarColor(activity, color);
    }

    public static void initStateBarColor(Activity activity, int color) {
        //设置 paddingTop
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        rootView.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            window.setStatusBarColor(color);
        } else {
            //根布局添加占位状态栏
            ViewGroup viewGroup = (ViewGroup) decorView;
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            viewGroup.addView(statusBarView, lp);
        }
        //更改状态栏字体颜色
        if (ColorUtils.calculateLuminance(color) >= 0.5) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 隐藏状态栏
     *
     * @param context
     */
    public static void hideStatusLan(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }

    /**
     * 设置状态栏颜色
     *
     * @param colorRef
     */
    public static void setStatusBarBackgroundColor(Activity activity, String colorRef) {
        int color = Color.parseColor(colorRef);
        setStatusBarBackgroundColor(activity, color);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    public static void setStatusBarBackgroundColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Log.i("", "setStatusBarBackgroundColor is ignore");
            return;
        }
        boolean darkMode = ColorUtils.calculateLuminance(color) < 0.5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
            View decorView = window.getDecorView();
            if (ColorUtils.calculateLuminance(color) >= 0.5) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (isMIUIStatusBarLightMode(activity, darkMode) || isFlymeStatusBarLightMode(activity, darkMode)) {
                window.setStatusBarColor(color);
            } else {
                window.setStatusBarColor(Color.BLACK);
            }
        } else {
            Log.i("", "setStatusBarBackgroundColor is ignore");
        }
    }

    /**
     * 设置小米手机状态栏颜色
     *
     * @param darkMode
     */
    public static boolean isMIUIStatusBarLightMode(Activity activity, boolean darkMode) {
        boolean result = false;
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
            result = true;
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        return result;
    }

    /**
     * Flyme
     *
     * @param darkMode
     */
    public static boolean isFlymeStatusBarLightMode(Activity activity, boolean darkMode) {
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkMode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            result = true;
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        return result;
    }

    /**
     * 隐藏虚拟按键
     */
    public static void hideNavigation(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getNavigationBarHeight(activity) > 0) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            WindowManager.LayoutParams params = window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            window.setAttributes(params);
        }
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

}
