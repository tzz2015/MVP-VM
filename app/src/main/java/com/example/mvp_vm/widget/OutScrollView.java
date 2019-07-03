package com.example.mvp_vm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 16 * @ClassName: OutScrollView
 * 17 * @Description: java类作用描述
 * 18 * @Author: lyf
 * 19 * @Date: 2019-07-02 20:30
 * 20
 */
public class OutScrollView extends ScrollView {
    private int mStartY = 0;
    private static final String TAG = "OutScrollView ";

    public OutScrollView(Context context) {
        super(context);
    }

    public OutScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                Log.e(TAG, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP");

            default:
                break;
        }

        return super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) ev.getY();
                intercepted = false;
                super.onInterceptTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                int endY = (int) ev.getY();
                if (getScrollY() != 0 || endY - mStartY < 0) {
                    intercepted = true;
                    Log.e(TAG, "自己处理");
                } else {
                    Log.e(TAG, "交给子布局");
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
            default:
                break;
        }
        return intercepted;
    }
}
