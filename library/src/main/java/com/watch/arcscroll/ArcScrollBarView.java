package com.watch.arcscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

@SuppressWarnings("unused")
public class ArcScrollBarView extends View {
    private static final int DEFAULT_MARGIN_TOP = 80;
    private static final int DEFAULT_PAINT_STORKE_WIDTH = 4;
    private static final String THUMB_PAINT_COLOR = "#00B1CB";
    private static final String TRACK_PAINT_COLOR = "#1A1A1A";

    /**
     * 起始角度
     */
    private float trackStartAngle;

    /**
     * 结束角度
     */
    private float trackSweepAngle;

    /**
     * 改变的角度
     */
    private float thumbStartAngle;

    /**
     * 扫描角度
     */
    private float thumbSweepAngle;

    /**
     * 圆弧半径
     */
    private float radius;

    /**
     * 距离顶部的高度
     */
    private int marginTop;

    /**
     * 轨道画笔
     */
    private Paint trackPaint;

    /**
     * 轨道画笔颜色
     */
    private int trackPaintColor;

    /**
     * 滑块画笔
     */
    private Paint thumbPaint;

    /**
     * 滑块画笔颜色
     */
    private int thumbPaintColor;

    /**
     * 画笔宽度
     */
    private int paintStrokeWidth;

    private RectF oval;

    private int mActionBarHeight;

    public ArcScrollBarView(Context context) {
        this(context, null, 0);
    }

    public ArcScrollBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcScrollBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mActionBarHeight = getActionBarHeight(context);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcScrollBarView, defStyle, 0);
        for (int i = 0; i < ta.length(); i++) {
            int index = ta.getIndex(i);
            if (index == R.styleable.ArcScrollBarView_margin_top) {
                marginTop = (int) ta.getDimension(index, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MARGIN_TOP,
                        getResources().getDisplayMetrics()));
                marginTop = mActionBarHeight == 0 ? marginTop : mActionBarHeight;
            } else if (index == R.styleable.ArcScrollBarView_thumb_color) {
                thumbPaintColor = ta.getColor(index, Color.parseColor(THUMB_PAINT_COLOR));

            } else if (index == R.styleable.ArcScrollBarView_track_color) {
                trackPaintColor = ta.getColor(index, Color.parseColor(TRACK_PAINT_COLOR));

            } else if (index == R.styleable.ArcScrollBarView_stroke_width) {
                // 画笔宽度应该为特定的像素值
                paintStrokeWidth = (int) ta.getDimension(index, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX, DEFAULT_PAINT_STORKE_WIDTH,
                        getResources().getDisplayMetrics()));
            }
        }



        ta.recycle();

        initData(context);
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initData(Context context) {
        initTrackPaint();
        initThumbPaint();

        // 设置矩形的四角坐标和内切圆半径
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        // 获取屏幕的宽度和高度
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 内切圆外接矩形的左,上,右,下坐标
        float rectfLeft = 0;
        float rectfTop = mActionBarHeight == 0 ? 0 : mActionBarHeight * -1;
        float rectfRight = screenWidth - paintStrokeWidth;
        float rectfBottom = mActionBarHeight == 0 ? screenHeight - paintStrokeWidth :
                screenHeight - mActionBarHeight - paintStrokeWidth;
        radius = (rectfRight - rectfLeft) / 2;
        oval = new RectF(rectfLeft, rectfTop, rectfRight, rectfBottom);

        // 设置圆弧的起始角度和终止角度
        initStartAndEndAngle();
    }

    private void initTrackPaint() {
        trackPaint = new Paint();
        trackPaint.setAntiAlias(true);
        trackPaint.setDither(true);
        trackPaint.setStrokeWidth(paintStrokeWidth);
        trackPaint.setColor(trackPaintColor);
        trackPaint.setAlpha((int) (255 * 0.6));
        trackPaint.setStyle(Paint.Style.STROKE);
    }

    private void initThumbPaint() {
        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        thumbPaint.setDither(true);
        thumbPaint.setStrokeWidth(paintStrokeWidth);
        thumbPaint.setColor(thumbPaintColor);
        thumbPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 设置圆弧的起始角度和终止角度
     */
    private void initStartAndEndAngle() {
        float distance = radius - marginTop;
        double cosAngle = Math.acos(distance * 1.0 / radius) * 180 / Math.PI;
        trackStartAngle = (270 + (int) cosAngle) % 360;
        trackSweepAngle = 2 * (90 - (int) cosAngle);
        thumbStartAngle = trackStartAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制轨道
        canvas.drawArc(oval, trackStartAngle, trackSweepAngle, false, trackPaint);
        // 绘制滑块
        canvas.drawArc(oval, thumbStartAngle, thumbSweepAngle, false, thumbPaint);
    }

    public void setStartAngle(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 计算thumb的弧度值
        thumbSweepAngle = (float) (trackSweepAngle * 1.0 * visibleItemCount / totalItemCount);

        // 计算thumb的起始角度
        thumbStartAngle = ((float)
                (firstVisibleItem * 1.0 / totalItemCount * trackSweepAngle) + trackStartAngle) % 360;
        invalidate();
    }

    public void setStartAngleForScrollView(int firstItemscrollY, int visibleHeight, int totalHeight) {
        if (mActionBarHeight > 0) {
            visibleHeight = visibleHeight - mActionBarHeight;
        }
        // 计算thumb的弧度值
        thumbSweepAngle = (float) (trackSweepAngle * 1.0 * visibleHeight / totalHeight);

        // 计算thumb的起始角度
        thumbStartAngle = (float) ((firstItemscrollY * 1.0 / (totalHeight - visibleHeight)
                * (trackSweepAngle - thumbSweepAngle) + trackStartAngle) % 360);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    private int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
