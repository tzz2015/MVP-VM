package com.example.mvp_vm.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.example.mvp_vm.R;

/**
 * 创建者：刘宇飞
 * 时间：2019/5/1 16:41
 * 描述：自定义截图
 *
 * @author lyf
 */
public class CutScannerView extends View {

    private static final String TAG = CutScannerView.class.getSimpleName();
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 白线画笔
     */
    private Paint mCornerPaint;
    private Rect mFocusFrameRect;
    private int mWidth, mHeight;
    private int mFocusFrameWh;
    private int mFocusFrameTp;
    private int mFocusFrameLt;
    private int mCornerLength;
    private int mCornerBorder;
    private int mRedLine;
    private Toast mToast;
    /**
     * 是否为当前控件使用事件
     */
    private boolean isUse = false;
    /**
     * 移动起始位置
     */
    private int mStartX = 0;
    private PostScaleListener mScaleListener;

    enum Position {
        /**
         * 四个角方向
         */
        LEFTTOP, LEFTBOTTOM, RIGHTTOP, RIGHTBOTTM, DEFAULT
    }

    private Position mDrection = Position.DEFAULT;

    public CutScannerView(Context context) {
        this(context, null);
    }

    public CutScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    private void initUI() {
        mBgPaint = new Paint();
        mBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_black_80));
        mBgPaint.setAntiAlias(true);

        mCornerPaint = new Paint();
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_white));

        Resources resources = getResources();

        mFocusFrameWh = resources.getDimensionPixelSize(R.dimen.cut_scanner_iv_wh);
        mFocusFrameTp = resources.getDimensionPixelSize(R.dimen.cut_scanner_iv_margin_top);
        mFocusFrameLt = resources.getDimensionPixelSize(R.dimen.cut_scanner_sv_margin_left);
        mCornerLength = resources.getDimensionPixelSize(R.dimen.cut_scanner_sv_corner_width);
        mCornerBorder = resources.getDimensionPixelSize(R.dimen.cut_scanner_sv_corner_thick);
        mRedLine = resources.getDimensionPixelSize(R.dimen.common_line_height);
        mFocusFrameRect = new Rect(mFocusFrameLt, mFocusFrameTp,
                mFocusFrameLt + mFocusFrameWh, mFocusFrameTp + mFocusFrameWh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        // 背景和四边
        drawScannerBg(canvas);
        drawScannerCorner(canvas);
        //绘制边界红色线条
        drawAroundLine(canvas);
        //绘制宫格线
        drawGridLine(canvas);


    }


    /**
     * 绘制周边背景
     *
     * @param canvas
     */
    private void drawScannerBg(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mFocusFrameRect.top, mBgPaint);
        canvas.drawRect(0, mFocusFrameRect.top, mFocusFrameRect.left, mFocusFrameRect.bottom, mBgPaint);
        canvas.drawRect(mFocusFrameRect.right, mFocusFrameRect.top, mWidth, mFocusFrameRect.bottom, mBgPaint);
        canvas.drawRect(0, mFocusFrameRect.bottom, mWidth, mHeight, mBgPaint);
    }

    /**
     * 绘制矩形框的四个角
     *
     * @param canvas
     */
    private void drawScannerCorner(Canvas canvas) {
        int offsetBoder = mCornerBorder / 2;
        //绘制左上角
        canvas.drawRect(mFocusFrameRect.left - offsetBoder, mFocusFrameRect.top - offsetBoder, mFocusFrameRect.left + mCornerLength, mFocusFrameRect.top + offsetBoder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left - offsetBoder, mFocusFrameRect.top, mFocusFrameRect.left + offsetBoder, mFocusFrameRect.top + mCornerLength, mCornerPaint);

        //绘制左下角
        canvas.drawRect(mFocusFrameRect.left - offsetBoder, mFocusFrameRect.bottom - offsetBoder, mFocusFrameRect.left + mCornerLength, mFocusFrameRect.bottom + offsetBoder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left - offsetBoder, mFocusFrameRect.bottom - mCornerLength, mFocusFrameRect.left + offsetBoder, mFocusFrameRect.bottom + offsetBoder, mCornerPaint);

        //绘制右上角
        canvas.drawRect(mFocusFrameRect.right - mCornerLength, mFocusFrameRect.top - offsetBoder, mFocusFrameRect.right + offsetBoder, mFocusFrameRect.top + offsetBoder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.right - offsetBoder, mFocusFrameRect.top - offsetBoder, mFocusFrameRect.right + offsetBoder, mFocusFrameRect.top + mCornerLength, mCornerPaint);

        //绘制右下角
        canvas.drawRect(mFocusFrameRect.right - mCornerLength, mFocusFrameRect.bottom - offsetBoder, mFocusFrameRect.right + offsetBoder, mFocusFrameRect.bottom + offsetBoder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.right - offsetBoder, mFocusFrameRect.bottom - mCornerLength, mFocusFrameRect.right + offsetBoder, mFocusFrameRect.bottom + offsetBoder, mCornerPaint);
    }

    /**
     * 绘制四周白线
     */
    private void drawAroundLine(Canvas canvas) {
        // 上
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.top, mFocusFrameRect.right, mFocusFrameRect.top + mRedLine, mCornerPaint);
        // 下
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.bottom - mRedLine, mFocusFrameRect.right, mFocusFrameRect.bottom, mCornerPaint);
        // 左
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.top, mFocusFrameRect.left + mRedLine, mFocusFrameRect.bottom, mCornerPaint);
        // 右
        canvas.drawRect(mFocusFrameRect.right - mRedLine, mFocusFrameRect.top, mFocusFrameRect.right, mFocusFrameRect.bottom, mCornerPaint);

    }

    /**
     * 绘制宫格线
     *
     * @param canvas
     */
    private void drawGridLine(Canvas canvas) {
        // 横向
        int horPoint1 = mFocusFrameRect.top + (mFocusFrameRect.bottom - mFocusFrameRect.top) / 3;
        int horPoint2 = mFocusFrameRect.top + (mFocusFrameRect.bottom - mFocusFrameRect.top) / 3 * 2;
        canvas.drawRect(mFocusFrameRect.left, horPoint1, mFocusFrameRect.right, horPoint1 + mRedLine, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left, horPoint2, mFocusFrameRect.right, horPoint2 + mRedLine, mCornerPaint);
        // 纵向
        int verPoint1 = mFocusFrameRect.left + (mFocusFrameRect.right - mFocusFrameRect.left) / 3;
        int verPoint2 = mFocusFrameRect.left + (mFocusFrameRect.right - mFocusFrameRect.left) / 3 * 2;
        canvas.drawRect(verPoint1, mFocusFrameRect.top, verPoint1 + mRedLine, mFocusFrameRect.bottom, mCornerPaint);
        canvas.drawRect(verPoint2, mFocusFrameRect.top, verPoint2 + mRedLine, mFocusFrameRect.bottom, mCornerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                //加大点击范围
                int mAddClickSize = 120;
                // 左上角
                if (x < mFocusFrameRect.left + mAddClickSize &&
                        y > mFocusFrameRect.top - mAddClickSize && y < mFocusFrameRect.top + mAddClickSize) {
                    Log.e(TAG, "左上角");
                    isUse = true;
                    mDrection = Position.LEFTTOP;
                } else if (x > mFocusFrameRect.right - mAddClickSize &&
                        y > mFocusFrameRect.top - mAddClickSize && y < mFocusFrameRect.top + mAddClickSize) {
                    Log.e(TAG, "右上角");
                    isUse = true;
                    mDrection = Position.RIGHTTOP;
                } else if (x < mFocusFrameRect.left + mAddClickSize &&
                        y > mFocusFrameRect.bottom - mAddClickSize && y < mFocusFrameRect.bottom + mAddClickSize) {
                    Log.e(TAG, "左下角");
                    isUse = true;
                    mDrection = Position.LEFTBOTTOM;
                } else if (x > mFocusFrameRect.right - mAddClickSize &&
                        y > mFocusFrameRect.bottom - mAddClickSize && y < mFocusFrameRect.bottom + mAddClickSize) {
                    Log.e(TAG, "右下角");
                    isUse = true;
                    mDrection = Position.RIGHTBOTTM;
                }
                mStartX = x;


            case MotionEvent.ACTION_MOVE:
                if (isUse) {
                    int endX = (int) event.getRawX();
                    int offset = Math.abs(endX - mStartX);
                    int move = 20;
                    // 右滑
                    if (offset > move) {
                        Log.e(TAG, "移动：" + offset);
                        resetDraw(offset);
                        if (endX > mStartX) {
                            Log.e(TAG, "右滑");
                            // 左滑
                        } else {
                            Log.e(TAG, "左滑");
                            resetDraw(offset);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isUse) {
                    postDelayed(new AutoScaleRunnable(mDrection), 16);
                }
                isUse = false;
                mDrection = Position.DEFAULT;
                break;

            default:
                break;
        }

        if (isUse) {
            return true;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 左右滑
     *
     * @param offset
     */
    private void resetDraw(int offset) {

        int mFrameWh = mFocusFrameWh - offset;
        if (mFrameWh < mFocusFrameWh / 4) {
            return;
        }
        switch (mDrection) {
            case LEFTTOP:
                mFocusFrameRect.left = mFocusFrameLt + offset;
                mFocusFrameRect.top = mFocusFrameTp + offset;
                break;
            case RIGHTTOP:
                mFocusFrameRect.right = mFocusFrameLt + mFocusFrameWh - offset;
                mFocusFrameRect.top = mFocusFrameTp + offset;
                break;
            case LEFTBOTTOM:
                mFocusFrameRect.left = mFocusFrameLt + offset;
                mFocusFrameRect.bottom = mFocusFrameTp + mFocusFrameWh - offset;
                break;
            case RIGHTBOTTM:
                mFocusFrameRect.right = mFocusFrameLt + mFocusFrameWh - offset;
                mFocusFrameRect.bottom = mFocusFrameTp + mFocusFrameWh - offset;
                break;
            default:
                break;
        }
        invalidate();
    }

    /**
     * 自动还原
     */
    private class AutoScaleRunnable implements Runnable {
        private Position mDrection;

        public AutoScaleRunnable(Position drection) {
            this.mDrection = drection;
        }

        @Override
        public void run() {

            if (mFocusFrameRect.left != mFocusFrameLt || mFocusFrameRect.top != mFocusFrameTp ||
                    mFocusFrameRect.right != mFocusFrameLt + mFocusFrameWh ||
                    mFocusFrameRect.bottom != mFocusFrameTp + mFocusFrameWh) {
                mFocusFrameRect.left -= 15;
                mFocusFrameRect.top -= 15;
                mFocusFrameRect.right += 15;
                mFocusFrameRect.bottom += 15;
                if (mFocusFrameRect.left < mFocusFrameLt) {
                    mFocusFrameRect.left = mFocusFrameLt;
                }
                if (mFocusFrameRect.top < mFocusFrameTp) {
                    mFocusFrameRect.top = mFocusFrameTp;
                }
                if (mFocusFrameRect.right > mFocusFrameLt + mFocusFrameWh) {
                    mFocusFrameRect.right = mFocusFrameLt + mFocusFrameWh;
                }
                if (mFocusFrameRect.bottom > mFocusFrameTp + mFocusFrameWh) {
                    mFocusFrameRect.bottom = mFocusFrameTp + mFocusFrameWh;
                }
                postDelayed(this, 16);
                invalidate();
                scaleBack(mDrection);
            }

        }
    }

    private void scaleBack(Position mDrection) {
        float scale = 1 - (float) (mFocusFrameRect.right - mFocusFrameRect.left) / mFocusFrameWh + 1;
        int x = mFocusFrameLt;
        int y = mFocusFrameTp;
        switch (mDrection) {
            case RIGHTBOTTM:
                x = mFocusFrameLt;
                y = mFocusFrameTp;
                break;
            case LEFTBOTTOM:
                x = mFocusFrameLt + mFocusFrameWh;
                y = mFocusFrameTp;
                break;
            case RIGHTTOP:
                x = mFocusFrameLt;
                y = mFocusFrameTp + mFocusFrameWh;
                break;
            case LEFTTOP:
                x = mFocusFrameLt + mFocusFrameWh;
                y = mFocusFrameTp + mFocusFrameWh;
                break;
            default:
                break;
        }
        if (mScaleListener != null) {
            mScaleListener.scaleListener(scale, scale, (float) x, (float) y);
        }
    }

    public void setScaleListener(PostScaleListener listener) {
        this.mScaleListener = listener;
    }

    public interface PostScaleListener {
        void scaleListener(float sx, float sy, float px, float py);
    }
}
