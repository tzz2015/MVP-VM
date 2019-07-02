package com.example.mvp_vm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 16 * @ClassName: OutScollView
 * 17 * @Description: java类作用描述
 * 18 * @Author: lyf
 * 19 * @Date: 2019-07-01 15:41
 * 20
 */
public class OutScollView extends ScrollView {
    public OutScollView(Context context) {
        super(context);
    }

    public OutScollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutScollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mStartY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endY = (int) event.getY();
                // 下拉
                if (endY > mStartY) {
                    // 如果不在顶部 自己处理事件 不给子布局
                    if (getScrollY() != 0) {
                        Log.e("OutScollView", "不在顶部");
                        return true;
                    }
                    Log.e("OutScollView", "在顶部");
                } else {
                    // 上拉 完全交给自己处理 不给子布局
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }

        return super.onTouchEvent(event);

    }
}
