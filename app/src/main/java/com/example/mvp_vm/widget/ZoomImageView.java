package com.example.mvp_vm.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import com.example.mvp_vm.R;

/**
 * @author lyf
 */
public class ZoomImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener
        , View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "ZoomImageView";
    private boolean isInit;


    /**
     * 缩放工具
     */
    private Matrix mMatrix;

    /**
     * 缩放的最小值
     */
    private float mMinScale = 0.5f;
    /**
     * 缩放的中间值
     */
    private float mMidScale = 2f;
    /**
     * 缩放的最大值
     */
    private float mMaxScale = 4f;

    /**
     * 多点手势触 控缩放比率分析器
     */
    private ScaleGestureDetector mScaleGestureDetector;
    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointerCount;
    private CutScannerView mCutScannerView;


    /**
     * 是否做边界检查
     */
    private boolean isCheckBorder = true;

    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;
    private ViewTreeObserver.OnGlobalLayoutListener mScannerGlobalListener;


    public ZoomImageView(Context context) {
        this(context, null);
        initView(context);
    }


    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    private void initView(Context context) {
        setScaleType(ScaleType.MATRIX);
        setBackgroundResource(R.color.common_transparent);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    private class AutoScaleRunnable implements Runnable {
        /**
         * 要缩放的目标值
         */
        private float mTargetScale;
        /**
         * 缩放的中心点x
         */
        private float x;
        /**
         * 缩放的中心点y
         */
        private float y;
        private float tmpScale;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);


            float currentScale = getScale();
            boolean isScale = (tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale);
            if (isScale) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);

            }
        }
    }

    private class AutoTranslateRunnable implements Runnable {

        @Override
        public void run() {
            if (getControlRect() == null) {
                return;
            }
            Rect mControlRect = getControlRect();
            RectF matrixRectF = getMatrixRectF();
            float dx = 0;
            float dy = 0;
            // 图片脱离左边的距离
            float distanceLeft = 0;
            // 图片脱离右边的距离
            float distanceLeRight = 0;
            // 图片脱离上边的距离
            float distanceLeTop = 0;
            // 图片脱离下边的距离
            float distanceLeBottom = 0;
            if (matrixRectF.left > mControlRect.left) {
                distanceLeft = matrixRectF.left - mControlRect.left;
                dx = isMinReduce(matrixRectF.left, mControlRect.left) ? -10 : -(matrixRectF.left - mControlRect.left);
            }
            if (matrixRectF.right < mControlRect.right) {
                distanceLeRight = mControlRect.right - matrixRectF.right;
                dx = isMinReduce(matrixRectF.right, mControlRect.right) ? 10 : (mControlRect.right - matrixRectF.right);
            }
            if (matrixRectF.top > mControlRect.top) {
                distanceLeTop = matrixRectF.top - mControlRect.top;
                dy = isMinReduce(matrixRectF.top, mControlRect.top) ? -10 : -(matrixRectF.top - mControlRect.top);
            }
            if (matrixRectF.bottom < mControlRect.bottom) {
                distanceLeBottom = mControlRect.bottom - matrixRectF.bottom;
                dy = isMinReduce(matrixRectF.bottom, mControlRect.bottom) ? 10 : mControlRect.bottom - matrixRectF.bottom;
            }
            float minMove = 0.5f;
            if (Math.abs(dy) > minMove) {
                float maxDx = Math.max(distanceLeft, distanceLeRight);
                if (maxDx > minMove) {
                    float maxDy = Math.max(distanceLeTop, distanceLeBottom);
                    dy = maxDy / maxDx * dy;
                }
            }
            mMatrix.postTranslate(dx, dy);
            setImageMatrix(mMatrix);

            if (Math.abs(dx) > minMove || Math.abs(dy) > minMove) {
                Log.e(TAG, "dx:" + dx + "--dy:" + dy);
                postDelayed(this, 10);
            }
        }
    }

    private boolean isMinReduce(float x, float y) {
        return Math.abs(x - y) > 10;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    @SuppressWarnings("deprecation")
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCutScannerView != null) {
            mCutScannerView.getViewTreeObserver().removeOnGlobalLayoutListener(mScannerGlobalListener);
            mCutScannerView = null;
        }

        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        if (!isInit) {
            toCenter();
            if (getControlRect() != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        correctOverBorder();
                    }
                }, 100);
            }


        }
    }

    /**
     * 将图片居中显示
     * 原有逻辑
     */
    private void toCenter() {
        int width = getWidth();
        int height = getHeight();
        float screenWeight = height * 1.0f / width;
        // 图片高度
        int imageH = getDrawable().getIntrinsicHeight();
        // 图片宽度
        int imageW = getDrawable().getIntrinsicWidth();
        float imageWeight = imageH * 1.0f / imageW;
        //如果当前屏幕高宽比 大于等于 图片高宽比,就缩放图片
        if (screenWeight >= imageWeight) {
            float scale = 1.0f;
            //图片比当前View宽,但是比当前View矮
            if (imageW > width && imageH < height) {
                // 图片宽度
                scale = width * 1.0f / imageW;
            }

            //图片比当前View窄,但是比当前View高
            if (imageH > height && imageW < width) {
                //根据高度缩放
                scale = height * 1.0f / imageH;
            }

            //图片高宽都大于当前View,那么就根据最小的缩放值来缩放
            if (imageH > height && imageW > width) {
                scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
            }

            if (imageH < height && imageW < width) {
                scale = Math.min(width * 1.0f / imageW, height * 1.0f / imageH);
            }

            /**
             * 设置缩放比率
             */
               /* mMinScale = scale;
                mMidScale = mMinScale * 2;
                mMaxScale = mMinScale * 4;*/
            /**
             * 把图片移动到中心点去
             */
            int dx = getWidth() / 2 - imageW / 2;
            int dy = getHeight() / 2 - imageH / 2;

            /**
             * 设置缩放(全图浏览模式,用最小的缩放比率去查看图片就好了)/移动位置
             */
            mMatrix.postTranslate(dx, dy);
            mMatrix.postScale(scale, scale, width >> 1, height >> 1);
        } else {

            float scale = width * 1.0f / imageW;
            /**
             * 设置缩放比率
             */
            mMaxScale = scale;
            mMidScale = mMaxScale / 2;
            mMinScale = mMaxScale / 4;

            //因为是长图浏览,所以用最大的缩放比率去加载长图
            mMatrix.postScale(mMaxScale, mMaxScale, 0, 0);
        }
        setImageMatrix(mMatrix);
        isInit = true;
    }

    /**
     * 溢出边界修正
     */
    private void correctOverBorder() {
        if (getControlRect() == null) {
            return;
        }
        Rect mControlRect = getControlRect();
        // 指定边界宽高
        int controlWidth = mControlRect.right - mControlRect.left;
        int controlHeight = mControlRect.bottom - mControlRect.top;
        RectF matrixRectF = getMatrixRectF();
        // 图片高度
        int imageHeight = (int) (matrixRectF.height() + 0.5);
        // 图片宽度
        int imageWidth = (int) (matrixRectF.width() + 0.5);

        float scale = 0;

        if (controlHeight > imageHeight && controlWidth < imageWidth) {
            // 指定边界高度大于当前图片高度 放大图片高度
            scale = controlHeight * 1.0f / imageHeight;
        } else if (controlWidth > imageWidth && controlHeight < imageHeight) {
            // 指定边界宽度大于当前图片宽度 放大图片宽度
            scale = controlWidth * 1.0f / controlWidth;
        } else if (controlHeight > imageHeight && controlWidth > imageWidth) {
            // 宽高均大于图片
            scale = Math.max(controlHeight * 1.0f / imageHeight, controlWidth * 1.0f / controlWidth);
        }
        if (scale != 0) {
            mMatrix.postScale(scale, scale, getWidth() >> 1, getHeight() >> 1);
            setImageMatrix(mMatrix);
//           startScaleAnimation(scale);
        }
        postDelayed(new AutoTranslateRunnable(), 16);


    }

    private void startScaleAnimation(final float scale) {
        ScaleAnimation animation = new ScaleAnimation(
                getScaleX(), scale, getScaleY(), scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMatrix.postScale(scale, scale, getWidth() >> 1, getHeight() >> 1);
                setImageMatrix(mMatrix);
                clearAnimation();
                postDelayed(new AutoTranslateRunnable(), 16);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setFillAfter(true);
        startAnimation(animation);
    }

    /**
     * 设置初始化状态为false
     */
    public void reSetState() {
        isInit = false;
        mMatrix.reset();
    }

    /**
     * 设置缩放
     */
    public void setScale(float sx, float sy, float px, float py) {
        if (mMatrix != null) {
            mMatrix.postScale(sx, sy, px, py);
            setImageMatrix(mMatrix);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        //将触摸事件传递给ScaleGestureDetector
        if (motionEvent.getPointerCount() > 1) {
            mScaleGestureDetector.onTouchEvent(motionEvent);
        }


        float x = 0;
        float y = 0;

        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += motionEvent.getX(i);
            y += motionEvent.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                RectF rectF = getMatrixRectF();
                if ((rectF.width() > getWidth() + 0.01f || (rectF.height() > getHeight() + 0.01f))) {
                    if ((rectF.right != getWidth()) && (rectF.left != 0)) {
                        try {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (!isCheckBorder) {
                    isCanDrag = true;
                }

                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();

                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        if (rectF.width() <= getWidth() && isCheckBorder) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }

                        if (rectF.height() <= getHeight() && isCheckBorder) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mMatrix.postTranslate(dx, dy);
                        if (isCheckBorder) {
                            checkBorderWhenTranslate();
                        }
                        setImageMatrix(mMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;

                RectF rect = getMatrixRectF();
                if ((rect.width() > getWidth() + 0.01f || (rect.height() > getHeight() + 0.01f))) {
                    if ((rect.right != getWidth()) && (rect.left != 0)) {
                        try {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
            case MotionEvent.ACTION_UP: {
                mLastPointerCount = 0;
                correctOverBorder();
                break;
            }
            default:
                return true;
        }
        return true;
    }

    /**
     * 在移动图片的时候进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }


        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    /**
     * 判断是否足以触发移动事件
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //获取用户手势判断出来的缩放值
        float scaleFactor = detector.getScaleFactor();
        float scale = getScale();

        /**
         * 没有图片
         */
        if (getDrawable() == null) {
            return true;
        }
        boolean isScale = (scale < mMaxScale && scaleFactor > 1.0f) || (scale > mMinScale && scaleFactor < 1.0f);
        //缩放范围控制
        if (isScale) {
            if (scaleFactor * scale < mMinScale) {
                scaleFactor = mMinScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);
        }
        return true;
    }

    /**
     * 在缩放的时候进行边界,位置 检查
     */
    private void checkBorderAndCenterWhenScale() {
        if (!isCheckBorder) {
            return;
        }
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = 0;
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    /**
     * 获取图片放大缩小后的宽高/top/left/right/bottom
     *
     * @return
     */
    public RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();

        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }

        return rectF;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    /**
     * 获取当前的缩放比率
     *
     * @return
     */
    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        reSetState();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {
        reSetState();
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        reSetState();
        super.setImageURI(uri);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
    }


    public void setCheckBorder(boolean checkBorder) {
        isCheckBorder = checkBorder;
    }

    public void setScale(float scale) {
        mMinScale = scale;
        mMidScale = scale * 4;
        mMaxScale = scale * 8;
    }

    public void binScannerView(CutScannerView cutScannerView) {
        this.mCutScannerView = cutScannerView;
        mScannerGlobalListener = this::correctOverBorder;
        mCutScannerView.getViewTreeObserver().addOnGlobalLayoutListener(mScannerGlobalListener);

    }


    private Rect getControlRect() {
        Rect controlRect = null;
        if (mCutScannerView != null) {
            controlRect = mCutScannerView.getViewRect();
        }
        return controlRect;
    }

}
