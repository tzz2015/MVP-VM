package com.example.mvp_vm.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * 内部scrollview
 *
 * @author lyf
 */
public class UserDefineScrollView extends ScrollView {

    private static final String TAG = "UserDefineScrollView ";
    private float mStartY;

    /**
     * 用于记录正常的布局位置
     */
    private Rect mOriginalRect = new Rect();
    /**
     * ScrollView的子View， 也是ScrollView的唯一一个子View
     */
    private View mContentView;

    public UserDefineScrollView(Context context) {
        super(context);
    }

    public UserDefineScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserDefineScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 在触摸事件中, 处理上拉和下拉的逻辑
     */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        int action = ev.getAction();
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN");
                mStartY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE");
                float endY = ev.getY();
                int deltaY = (int) ((endY - mStartY) * 0.5);
                if (getScrollY() == 0 && deltaY > 0) {
                    int maxMove = 150;
                    if (deltaY > maxMove) {
                        deltaY = maxMove;
                    }
                    Log.e(TAG, "deltaY:" + deltaY);
                    Log.e(TAG, "我需要消费move事件");
                    mContentView.layout(mOriginalRect.left, mOriginalRect.top + deltaY, mOriginalRect.right, mOriginalRect.bottom + deltaY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP");
                boundBack();
                break;

            default:

                break;

        }

        return super.onTouchEvent(ev);

    }

    private void boundBack() {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -mContentView.getTop());
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearTranslateAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mContentView.startAnimation(anim);
    }

    private void clearTranslateAnimation() {
        if (mContentView != null) {
            mContentView.clearAnimation();
            mContentView.layout(mOriginalRect.left, mOriginalRect.top, mOriginalRect.right, mOriginalRect.bottom);
        }
    }


    @Override

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mContentView == null) {

            return;
        }
        // ScrollView中的唯一子控件的位置信息, 这个位置信息在整个控件的生命周期中保持不变
        mOriginalRect.set(mContentView.getLeft(), mContentView.getTop(), mContentView.getRight(), mContentView.getBottom());

    }

    @Override

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }


}

