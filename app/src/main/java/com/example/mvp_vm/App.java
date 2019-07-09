package com.example.mvp_vm;

import android.app.Application;

/**
 * 16 * @ClassName: App
 * 17 * @Description: java类作用描述
 * 18 * @Author: lyf
 * 19 * @Date: 2019-07-09 16:24
 * 20
 */
public class App extends Application {
    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
    public static App getInstance() {
        return app;
    }
}
