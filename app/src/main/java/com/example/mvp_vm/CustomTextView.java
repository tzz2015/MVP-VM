package com.example.mvp_vm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author lyf
 * @date 2019/5/7 18:31
 * 描述：去除内边距的textView
 */
public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    private final String TAG = CustomTextView.class.getSimpleName();
    /**
     * 文本画笔
     */
    private TextPaint mTextPaint;
    /**
     * 绘制矩形
     */
    private Rect mRect;
    /**
     * 默认宽度
     */
    private int mLayoutWidth = -1;
    /**
     * 获得每行数据
     */
    private String[] mLineContents;
    /**
     * 获取行间距的额外空间
     */
    private float mLineSpaceHeight = 0.1f;
    /**
     * 获取行间距乘法器
     */
    private float mLineSpaceHeightMult = 1.0f;

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 初始化方法
     */
    @SuppressLint("NewApi")
    private void init(Context context, AttributeSet attrs) {
        //声明画笔对象
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        //声明矩形绘制对象
        mRect = new Rect();
        //获得行间距额外数据
        mLineSpaceHeight = getLineSpacingExtra();
        //获得行间距方法器
        mLineSpaceHeightMult = getLineSpacingMultiplier();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null) {
            //获得文本内容文本内容不可以修改,平切判断当前当前内容是否为null
            final String tvcontent = TextUtils.isEmpty(getText()) ? "" : getText().toString();
            //获取文本长度
            final int tvLenght = tvcontent.length();
            //设置文本宽度
            mTextPaint.getTextBounds(tvcontent, 0, tvLenght, mRect);
            //设置文本大小
            mTextPaint.setTextSize(getTextSize());
            //设置文本颜色
            mTextPaint.setColor(getCurrentTextColor());
            // 字体是否加粗
            Typeface typeface = getTypeface();
            mTextPaint.setFakeBoldText(typeface.getStyle()== Typeface.BOLD);
            //获取行数据
            getTextContentData(layout);
            //获得行高
            int lineHeight = -mRect.top + mRect.bottom;
            //初始化布局
            initLayout(layout);
            //设置布局区域
            int[] area = getWidthAndHeigt(widthMeasureSpec, heightMeasureSpec, mLayoutWidth, layout.getLineCount(), lineHeight);
            //设置布局
            setMeasuredDimension(area[0], area[1]);
        }
    }

    /**
     * 初始化化布局高度
     *
     * @param layout
     */
    private void initLayout(Layout layout) {
        //获得布局大小
        if (mLayoutWidth < 0) {
            //获取第一次测量数据
            mLayoutWidth = layout.getWidth();
        }
    }

    /**
     * 获取布局数据
     *
     * @param pWidthMeasureSpec
     * @param pHeightMeasureSpec
     * @param pWidth
     * @return 返回宽高数组
     */
    private int[] getWidthAndHeigt(int pWidthMeasureSpec, int pHeightMeasureSpec, int pWidth, int pLineCount, int pLineHeight) {
        //获取宽的模式
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);
        //获取高的模式
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        //获取宽的尺寸
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);
        //获取高的尺寸
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        //声明控件尺寸
        int width;
        int height;
        //判断模式
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            width = pWidth - mRect.left;
        }
        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            if (pLineCount > 1) {
                height = pLineHeight * pLineCount + (int) (mLineSpaceHeight * mLineSpaceHeightMult * (pLineCount - 1));
            } else {
                height = pLineHeight * pLineCount;
            }
        }
        //初始化宽高数组
        int[] area = {
                width,
                height
        };
        return area;
    }

    /**
     * 获取行数据
     *
     * @param layout 文本布局对象（注：该布局其实使用的是Layout子类对象StaticLayout）
     */
    private void getTextContentData(Layout layout) {
        //初始化航速数据
        mLineContents = new String[layout.getLineCount()];
        //获得每行数据
        for (int i = 0; i < layout.getLineCount(); i++) {
            int start = layout.getLineStart(i);
            int end = layout.getLineEnd(i);
            mLineContents[i] = getText().subSequence(start, end).toString();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //行高
        float lineHeight = -mRect.top + mRect.bottom;
        //行间距
        float lineSpace = mLineSpaceHeight * mLineSpaceHeightMult;
        //循环获取每行数据内容
        for (int i = 0; i < mLineContents.length; i++) {
            //获得数据
            String drawContent = mLineContents[i];
            //显示日志
            Log.e(TAG, "LINE[" + (i + 1) + "]=" + drawContent);
            //绘制每行数据
            canvas.drawText(drawContent, 0, -mRect.top + (lineHeight + lineSpace) * i, mTextPaint);
        }

    }

}
