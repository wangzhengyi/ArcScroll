package com.watch.arcscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

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
    private boolean once;

    public ArcScrollBarView(Context context) {
        this(context, null, 0);
    }

    public ArcScrollBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcScrollBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取ActionBar的高度
        getActionBarHeight();

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcScrollBarView, defStyle, 0);
        marginTop = (int) ta.getDimension(R.styleable.ArcScrollBarView_margin_top, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, DEFAULT_MARGIN_TOP,
                getResources().getDisplayMetrics()));
        marginTop += mActionBarHeight;
        thumbPaintColor = ta.getColor(R.styleable.ArcScrollBarView_thumb_color,
                Color.parseColor(THUMB_PAINT_COLOR));
        trackPaintColor = ta.getColor(R.styleable.ArcScrollBarView_track_color,
                Color.parseColor(TRACK_PAINT_COLOR));
        paintStrokeWidth = (int) ta.getDimension(R.styleable.ArcScrollBarView_stroke_width,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, DEFAULT_PAINT_STORKE_WIDTH,
                getResources().getDisplayMetrics()));
        ta.recycle();

        initPaint();
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initPaint() {
        initTrackPaint();
        initThumbPaint();
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
        Log.e("TAG", "radius=" + radius + ", marginTop=" + marginTop + ", distance=" + distance);
        double cosAngle = Math.acos(distance * 1.0 / radius) * 180 / Math.PI;
        trackStartAngle = (270 + (int) cosAngle) % 360;
        trackSweepAngle = 2 * (90 - (int) cosAngle);
        thumbStartAngle = trackStartAngle;
        Log.e("TAG", "startAngle=" + trackStartAngle + ", sweepAngle=" + trackSweepAngle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once && !isInEditMode()) {
            once = true;
            setup();
        }
    }

    private void setup() {
        // 设置矩形的四角坐标和内切圆半径
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        // 获取屏幕的宽度和高度
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int viewHeight = getMeasuredHeight();
        int viewWidth = getMeasuredWidth();
        Log.e("TAG", "view height=" + viewHeight + ", view width=" + viewWidth);

        // 内切圆外接矩形的左,上,右,下坐标
        float rectangleLeft = 0;
        float rectangleTop = mActionBarHeight == 0 ? 0 : mActionBarHeight * -1;
        float rectangleRight = screenWidth - paintStrokeWidth;
        float rectangleBottom = mActionBarHeight == 0 ? screenHeight - paintStrokeWidth :
                screenHeight - mActionBarHeight - paintStrokeWidth;
        radius = (rectangleRight - rectangleLeft) / 2;
        oval = new RectF(rectangleLeft, rectangleTop, rectangleRight, rectangleBottom);

        // 设置圆弧的起始角度和终止角度
        initStartAndEndAngle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            // 绘制轨道
            canvas.drawArc(oval, trackStartAngle, trackSweepAngle, false, trackPaint);
            // 绘制滑块
            canvas.drawArc(oval, thumbStartAngle, thumbSweepAngle, false, thumbPaint);
        }
    }

    public void setStartAngle(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 计算thumb的弧度值
        thumbSweepAngle = (float) (trackSweepAngle * 1.0 * visibleItemCount / totalItemCount);

        // 计算thumb的起始角度
        thumbStartAngle = ((float)
                (firstVisibleItem * 1.0 / totalItemCount * trackSweepAngle) + trackStartAngle) % 360;
        invalidate();
    }

    public void setStartAngleForScrollView(int firstItemScrollY, int visibleHeight, int totalHeight) {
        if (mActionBarHeight > 0) {
            visibleHeight = visibleHeight - mActionBarHeight;
        }
        // 计算thumb的弧度值
        thumbSweepAngle = (float) (trackSweepAngle * 1.0 * visibleHeight / totalHeight);

        // 计算thumb的起始角度
        thumbStartAngle = (float) ((firstItemScrollY * 1.0 / (totalHeight - visibleHeight)
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

    private int getActionBarHeight() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[] {android.R.attr.actionBarSize, android.R.attr.windowActionBar});

        mActionBarHeight = (int) styledAttributes.getDimension(0, 0);
        boolean windowActionBar = styledAttributes.getBoolean(1, false);
        if (!windowActionBar) {
            // 当前Theme不包含ActionBar，则将其高度设置为0
            mActionBarHeight = 0;
        }
        styledAttributes.recycle();

        return mActionBarHeight;
    }
}