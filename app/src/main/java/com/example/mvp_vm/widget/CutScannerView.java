package com.example.mvp_vm.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.mvp_vm.R;
import com.example.mvp_vm.utils.Utils;


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
    private int mFocusFrameWidth;
    private int mFocusFrameHeight;
    private int mFocusFrameTp;
    private int mFocusFrameLt;
    private int mCornerLength;
    private int mCornerBorder;
    private int mBoundLine;
    private int mGridLine;

    /**
     * 是否为当前控件使用事件
     */
    private boolean isUseEven = false;
    /**
     * 移动起始位置
     */
    private int mStartX = 0;
    private int mStartY = 0;
    /**
     * 是否可以控制裁剪框大小
     */
    private boolean isCanDrag = true;

    private PostScaleListener mScaleListener;
    /**
     * 手指松开后是否自动回退到原来位置
     */
    private boolean isScaleOriginal = true;

    /**
     * 是否为自由裁剪框
     */
    private boolean isFreeCut = false;

    /**
     * 宽高比 等比缩放裁剪框  =高/宽
     */
    private float mAspectRatio = 1.0f;
    /**
     * 对外提供出裁剪框的位置范围
     */
    private Rect mViewRect;

    enum Position {
        /**
         * 四个角方向
         */
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM, DEFAULT
    }

    private Position mDirection = Position.DEFAULT;

    public CutScannerView(Context context) {
        this(context, null);
    }

    public CutScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(attrs);
    }

    private void initUI(AttributeSet attrs) {
        mBgPaint = new Paint();
        mBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_black_80));
        mBgPaint.setAntiAlias(true);

        mCornerPaint = new Paint();
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_white));
        initAttributes(attrs);
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CutScannerView);

        try {
            Resources resources = getResources();
            mCornerBorder = (int) a.getDimension(R.styleable.CutScannerView_cut_scanner_sv_corner_thick, resources.getDimensionPixelSize(R.dimen.cut_scanner_sv_corner_thick));
            mCornerLength = (int) a.getDimension(R.styleable.CutScannerView_cut_scanner_sv_corner_width, resources.getDimensionPixelSize(R.dimen.cut_scanner_sv_corner_width));
            mFocusFrameTp = mCornerBorder / 2;
            mFocusFrameLt = mCornerBorder / 2;
            mBoundLine = (int) a.getDimension(R.styleable.CutScannerView_common_cut_scanner_line, resources.getDimensionPixelSize(R.dimen.common_cut_scanner_line));
            mGridLine = (int) a.getDimension(R.styleable.CutScannerView_common_cut_grid_line, resources.getDimensionPixelSize(R.dimen.common_line_height));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        if (mWidth == 0 || mHeight == 0) {
            return;
        }
        if (mFocusFrameRect == null) {
            mFocusFrameWidth = mWidth - mCornerBorder;
            mFocusFrameHeight = mHeight - mCornerBorder;
            // 重新赋值高度=宽度*mAspectRatio
            if (mAspectRatio != 1.0) {
                if (mAspectRatio < 1.0) {
                    mFocusFrameHeight = Utils.intToFloat(mFocusFrameWidth * mAspectRatio);
                    mFocusFrameTp = mFocusFrameTp + (mFocusFrameWidth - mFocusFrameHeight) / 2;
                } else {
                    mFocusFrameWidth = Utils.intToFloat(mFocusFrameHeight / mAspectRatio);
                    mFocusFrameLt = mFocusFrameLt + (mFocusFrameHeight - mFocusFrameWidth) / 2;
                }

            }
            mFocusFrameRect = new Rect(mFocusFrameLt, mFocusFrameTp, mFocusFrameLt + mFocusFrameWidth, mFocusFrameTp + mFocusFrameHeight);
        }
        // 背景和四边
//        drawScannerBg(canvas);
        drawScannerCorner(canvas);
        //绘制边界红色线条
        drawAroundLine(canvas);
        //绘制宫格线
        drawGridLine(canvas);
    }

    /**
     * 设置是否返回原点
     *
     * @param isScaleOriginal
     */
    public void setScaleOginal(boolean isScaleOriginal) {
        this.isScaleOriginal = isScaleOriginal;
    }

    /**
     * 设置是否自由裁剪
     *
     * @param isFreeCut
     */
    public void setFreeCut(boolean isFreeCut) {
        this.isFreeCut = isFreeCut;
    }

    /**
     * 设置裁剪框区域是否可以被拖动修改
     */
    public void setCanDrag(boolean isCanDrag) {
        this.isCanDrag = isCanDrag;
    }

    public boolean isCanDrag() {
        return isCanDrag;
    }

    /**
     * 设置宽高比
     */
    public void setAspectRatio(final float mAspectRatio) {
        this.mAspectRatio = mAspectRatio;
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
        int offsetBorder = mCornerBorder / 2;
        //绘制左上角
        canvas.drawRect(mFocusFrameRect.left - offsetBorder, mFocusFrameRect.top - offsetBorder, mFocusFrameRect.left + mCornerLength, mFocusFrameRect.top + offsetBorder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left - offsetBorder, mFocusFrameRect.top, mFocusFrameRect.left + offsetBorder, mFocusFrameRect.top + mCornerLength, mCornerPaint);

        //绘制左下角
        canvas.drawRect(mFocusFrameRect.left - offsetBorder, mFocusFrameRect.bottom - offsetBorder, mFocusFrameRect.left + mCornerLength, mFocusFrameRect.bottom + offsetBorder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left - offsetBorder, mFocusFrameRect.bottom - mCornerLength, mFocusFrameRect.left + offsetBorder, mFocusFrameRect.bottom + offsetBorder, mCornerPaint);

        //绘制右上角
        canvas.drawRect(mFocusFrameRect.right - mCornerLength, mFocusFrameRect.top - offsetBorder, mFocusFrameRect.right + offsetBorder, mFocusFrameRect.top + offsetBorder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.right - offsetBorder, mFocusFrameRect.top - offsetBorder, mFocusFrameRect.right + offsetBorder, mFocusFrameRect.top + mCornerLength, mCornerPaint);

        //绘制右下角
        canvas.drawRect(mFocusFrameRect.right - mCornerLength, mFocusFrameRect.bottom - offsetBorder, mFocusFrameRect.right + offsetBorder, mFocusFrameRect.bottom + offsetBorder, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.right - offsetBorder, mFocusFrameRect.bottom - mCornerLength, mFocusFrameRect.right + offsetBorder, mFocusFrameRect.bottom + offsetBorder, mCornerPaint);
    }

    /**
     * 绘制四周白线
     */
    private void drawAroundLine(Canvas canvas) {
        // 上
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.top, mFocusFrameRect.right, mFocusFrameRect.top + mBoundLine, mCornerPaint);
        // 下
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.bottom - mBoundLine, mFocusFrameRect.right, mFocusFrameRect.bottom, mCornerPaint);
        // 左
        canvas.drawRect(mFocusFrameRect.left, mFocusFrameRect.top, mFocusFrameRect.left + mBoundLine, mFocusFrameRect.bottom, mCornerPaint);
        // 右
        canvas.drawRect(mFocusFrameRect.right - mBoundLine, mFocusFrameRect.top, mFocusFrameRect.right, mFocusFrameRect.bottom, mCornerPaint);

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
        canvas.drawRect(mFocusFrameRect.left, horPoint1, mFocusFrameRect.right, horPoint1 + mGridLine, mCornerPaint);
        canvas.drawRect(mFocusFrameRect.left, horPoint2, mFocusFrameRect.right, horPoint2 + mGridLine, mCornerPaint);
        // 纵向
        int verPoint1 = mFocusFrameRect.left + (mFocusFrameRect.right - mFocusFrameRect.left) / 3;
        int verPoint2 = mFocusFrameRect.left + (mFocusFrameRect.right - mFocusFrameRect.left) / 3 * 2;
        canvas.drawRect(verPoint1, mFocusFrameRect.top, verPoint1 + mGridLine, mFocusFrameRect.bottom, mCornerPaint);
        canvas.drawRect(verPoint2, mFocusFrameRect.top, verPoint2 + mGridLine, mFocusFrameRect.bottom, mCornerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取屏幕上点击的坐标
                int x = (int) event.getX();
                int y = (int) event.getY();
                pointCheck(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isUseEven) {
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    int needMove = 10;
                    if (Math.abs(endX - mStartX) > needMove || Math.abs(endY - mStartY) > needMove) {
                        moveJude(endX, endY);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isUseEven && isScaleOriginal) {
                    postDelayed(new AutoScaleRunnable(mDirection), 16);
                }
                isUseEven = false;
                mDirection = Position.DEFAULT;
                invalidate();
                break;

            default:
                break;
        }
        // 消费此事件
        if (isUseEven) {
            return true;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 判断是否可以移动九宫格
     *
     * @param endX
     * @param endY
     */
    private void moveJude(int endX, int endY) {
        // 左右拖动范围
        int canMoveX = isCanDragLR(endX);

        if (isFreeCut) {
            // 上下拖动范围
            int canMoveY = isCanDragTB(endY);
            resetCutView(canMoveX, canMoveY);
        } else {
            resetCutView(canMoveX, 0);
        }

    }

    /**
     * 左右可拖动范围
     *
     * @param endX
     */
    private int isCanDragLR(int endX) {
        Log.e(TAG, "endX：" + endX);
        //最小宫格长度为1/4 不能移动
        int minWH = mFocusFrameWidth / 4;
        switch (mDirection) {
            //左侧
            case LEFT_TOP:
            case LEFT_BOTTOM:
                if (Math.abs(endX - mFocusFrameRect.right) <= minWH
                        || endX > mFocusFrameRect.right) {
                    return mFocusFrameRect.left;
                }
                if (endX < mFocusFrameLt) {
                    return mFocusFrameLt;
                }
                if (endX > mFocusFrameLt + mFocusFrameWidth - minWH) {
                    return mFocusFrameLt + mFocusFrameWidth - minWH;
                }
                break;
            //右侧
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
                if (Math.abs(endX - mFocusFrameRect.left) <= minWH
                        || endX < mFocusFrameRect.left) {
                    return mFocusFrameRect.right;
                }
                if (endX < mFocusFrameLt + minWH) {
                    return mFocusFrameLt + minWH;
                }
                if (endX > mFocusFrameLt + mFocusFrameWidth) {
                    return mFocusFrameLt + mFocusFrameWidth;
                }
                break;
            default:
                break;
        }
        return endX;

    }

    /**
     * 上下可拖动范围
     */
    private int isCanDragTB(int endY) {

        //最小宫格长度为1/4 不能移动
        int minWH = mFocusFrameHeight / 4;
        // 右滑
        Log.e(TAG, "endY：" + endY);
        switch (mDirection) {
            //上侧
            case LEFT_TOP:
            case RIGHT_TOP:
                if (Math.abs(endY - mFocusFrameRect.bottom) <= minWH
                        || endY > mFocusFrameRect.bottom) {
                    return mFocusFrameRect.top;
                }
                if (endY < mFocusFrameTp) {
                    return mFocusFrameTp;
                }
                if (endY > mFocusFrameTp + mFocusFrameHeight - minWH) {
                    return mFocusFrameTp + mFocusFrameHeight - minWH;
                }
                break;
            //下侧
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM:
                if (Math.abs(endY - mFocusFrameRect.top) <= minWH
                        || endY < mFocusFrameRect.top) {
                    return mFocusFrameRect.bottom;
                }
                if (endY < mFocusFrameTp + minWH) {
                    return mFocusFrameTp + minWH;
                }
                if (endY > mFocusFrameTp + mFocusFrameHeight) {
                    return mFocusFrameTp + mFocusFrameHeight;
                }
                break;
            default:
                break;
        }
        return endY;
    }


    /**
     * 判断点击的时候 ACTION_DOWN是否落到四个角的区域
     *
     * @param x
     * @param y
     */
    private void pointCheck(int x, int y) {
        if (isCanDrag) {
            //加大点击范围
            int mAddClickSize = 120;
            // 左上角
            if (x < mFocusFrameRect.left + mAddClickSize &&
                    y > mFocusFrameRect.top - mAddClickSize && y < mFocusFrameRect.top + mAddClickSize) {
                Log.e(TAG, "左上角");
                isUseEven = true;
                mDirection = Position.LEFT_TOP;
            } else if (x > mFocusFrameRect.right - mAddClickSize &&
                    y > mFocusFrameRect.top - mAddClickSize && y < mFocusFrameRect.top + mAddClickSize) {
                Log.e(TAG, "右上角");
                isUseEven = true;
                mDirection = Position.RIGHT_TOP;
            } else if (x < mFocusFrameRect.left + mAddClickSize &&
                    y > mFocusFrameRect.bottom - mAddClickSize && y < mFocusFrameRect.bottom + mAddClickSize) {
                Log.e(TAG, "左下角");
                isUseEven = true;
                mDirection = Position.LEFT_BOTTOM;
            } else if (x > mFocusFrameRect.right - mAddClickSize &&
                    y > mFocusFrameRect.bottom - mAddClickSize && y < mFocusFrameRect.bottom + mAddClickSize) {
                Log.e(TAG, "右下角");
                isUseEven = true;
                mDirection = Position.RIGHT_BOTTOM;
            }
            mStartX = x;
            mStartY = y;
        }

    }

    /**
     * 左右滑改变截图的宫格
     *
     * @param offsetX
     * @param offsetY =0时候 等比例缩放
     */
    private void resetCutView(int offsetX, int offsetY) {
        int offset;
        switch (mDirection) {
            case LEFT_TOP:
                offset = (int) ((offsetX - mFocusFrameLt) * mAspectRatio + 0.5);
                mFocusFrameRect.left = offsetX;
                mFocusFrameRect.top = offsetY > 0 ? offsetY : mFocusFrameTp + offset;
                break;
            case RIGHT_TOP:
                offset = (int) ((offsetX - mFocusFrameLt - mFocusFrameWidth) * mAspectRatio + 0.5);
                mFocusFrameRect.right = offsetX;
                mFocusFrameRect.top = offsetY > 0 ? offsetY : mFocusFrameTp - offset;
                break;
            case LEFT_BOTTOM:
                offset = (int) ((offsetX - mFocusFrameLt) * mAspectRatio + 0.5);
                mFocusFrameRect.left = offsetX;
                mFocusFrameRect.bottom = offsetY > 0 ? offsetY : mFocusFrameTp + mFocusFrameHeight - offset;
                break;
            case RIGHT_BOTTOM:
                offset = (int) ((offsetX - mFocusFrameLt - mFocusFrameWidth) * mAspectRatio + 0.5);
                mFocusFrameRect.right = offsetX;
                mFocusFrameRect.bottom = offsetY > 0 ? offsetY : mFocusFrameTp + mFocusFrameHeight + offset;
                break;
            default:
                break;
        }
        invalidate();
    }

    /**
     * 获取指定区域图片
     *
     * @return 截图
     */
    public Bitmap getBitmap(ZoomImageView imageView) {
        Bitmap bitmap = null;
        try {
            bitmap = imageView.getDrawingCache();
            RectF imageRectF = imageView.getMatrixRectF();
            RectF borderRectF = new RectF(getLeft(), getTop(), getLeft() + (mFocusFrameRect.right - mFocusFrameRect.left), getTop() + (mFocusFrameRect.bottom - mFocusFrameRect.top));
            if (imageRectF == null) {
                return null;
            }
            RectF finalRectF = new RectF();
            if (finalRectF.setIntersect(imageRectF, borderRectF)) {
                return Bitmap.createBitmap(bitmap,
                        (int) finalRectF.left > (int) getX() ? (int) finalRectF.left : (int) getX(),
                        (int) finalRectF.top > (int) getY() ? (int) finalRectF.top : (int) getY(),
                        (int) finalRectF.width(),
                        (int) finalRectF.height());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return null;
    }


    /**
     * 自动还原
     */
    private class AutoScaleRunnable implements Runnable {
        private Position mDirection;

        public AutoScaleRunnable(Position direction) {
            this.mDirection = direction;
        }

        @Override
        public void run() {
            int reduceL = 10;
            int reduceR = 10;
            int reduceTB = (int) (10 * mAspectRatio + 0.5);
            if (mFocusFrameRect.left != mFocusFrameLt || mFocusFrameRect.top != mFocusFrameTp ||
                    mFocusFrameRect.right != mFocusFrameLt + mFocusFrameWidth ||
                    mFocusFrameRect.bottom != mFocusFrameTp + mFocusFrameHeight) {
                mFocusFrameRect.left -= reduceL;
                mFocusFrameRect.top -= reduceTB;
                mFocusFrameRect.right += reduceR;
                mFocusFrameRect.bottom += reduceTB;
                if (mFocusFrameRect.left < mFocusFrameLt) {
                    reduceL = reduceL - (mFocusFrameLt - mFocusFrameRect.left);
                    mFocusFrameRect.left = mFocusFrameLt;
                }
                if (mFocusFrameRect.top < mFocusFrameTp) {
                    mFocusFrameRect.top = mFocusFrameTp;
                }
                if (mFocusFrameRect.right > mFocusFrameLt + mFocusFrameWidth) {
                    reduceR = reduceR - (mFocusFrameRect.right - (mFocusFrameLt + mFocusFrameWidth));
                    mFocusFrameRect.right = mFocusFrameLt + mFocusFrameWidth;
                }
                if (mFocusFrameRect.bottom > mFocusFrameTp + mFocusFrameHeight) {
                    mFocusFrameRect.bottom = mFocusFrameTp + mFocusFrameHeight;
                }
                postDelayed(this, 16);
                invalidate();
                float scale = 1 + (float) (reduceL + reduceR) / (float) (mFocusFrameRect.right - mFocusFrameRect.left);
                scaleBack(mDirection, scale);
            }

        }
    }

    /**
     * 还原九宫格监听
     *
     * @param direction
     */
    private void scaleBack(Position direction, float scale) {
        float x = mFocusFrameLt;
        float y = mFocusFrameTp;
        switch (direction) {
            case RIGHT_BOTTOM:
                x = getLeft() + mFocusFrameLt;
                y = getTop() - mFocusFrameTp;
                break;
            case LEFT_BOTTOM:
                x = getRight() - mFocusFrameLt;
                y = getTop() - mFocusFrameTp;
                break;
            case RIGHT_TOP:
                x = getLeft() + mFocusFrameLt;
                y = getTop() + mFocusFrameHeight;
                break;
            case LEFT_TOP:
                x = getRight() - mFocusFrameLt;
                y = getTop() + mFocusFrameHeight;
                break;
            default:
                break;
        }
        if (mScaleListener != null) {
            mScaleListener.scaleListener(scale, scale, x, y);
        }
    }

    /**
     * 将该view的位置信息提供出去
     * 改方法最view绘制完成后获取
     */
    public Rect getViewRect() {

        if (mFocusFrameRect == null) {
            return null;
        }
        if (mViewRect == null) {
            mViewRect = new Rect();
        }
        mViewRect.left = getLeft() + mFocusFrameLt;
        mViewRect.top = getTop() + mFocusFrameTp;
        mViewRect.right = mViewRect.left + (mFocusFrameRect.right - mFocusFrameRect.left);
        mViewRect.bottom = mViewRect.top + (mFocusFrameRect.bottom - mFocusFrameRect.top);


        return mViewRect;
    }


    public void setScaleListener(PostScaleListener listener) {
        this.mScaleListener = listener;
    }


    public interface PostScaleListener {
        /**
         * 扫码框还原监听
         *
         * @param sx X轴缩放比例
         * @param sy Y轴缩放比例
         * @param px 起点X
         * @param py 起点Y
         */
        void scaleListener(float sx, float sy, float px, float py);
    }


}
