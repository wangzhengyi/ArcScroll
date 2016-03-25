package com.watch.arcscroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

@SuppressWarnings("unused")
public class ArcScrollView extends ScrollView {

    /**
     * 当前屏幕的高度
     */
    private int screenHeight;

    /**
     * ScrollView中第一个View的getTop值
     */
    private int firstItemScrollY;

    /**
     * 侧边栏自定义控件
     */
    private ArcScrollBarView mArcScrollBarView;

    /**
     * Runnable延迟执行的时间
     */
    private long delayMillis = 100;

    /**
     * 上次滑动的时间
     */
    private long lastScrollUpdate = -1;

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > delayMillis) {
                lastScrollUpdate = -1;
                onScrollEnd();
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };

    public ArcScrollView(Context context) {
        this(context, null);
    }

    public ArcScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
    }

    private void initData(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        firstItemScrollY = 0;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (lastScrollUpdate == -1) {
            onScrollStart();
            postDelayed(scrollerTask, delayMillis);
        }
        lastScrollUpdate = System.currentTimeMillis();

        firstItemScrollY = t;
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);

        /** ScrollView中所有View的总高度 */
        int totalViewHeight;
        if (viewGroup != null && viewGroup.getChildCount() > 0) {
            totalViewHeight = viewGroup.getHeight();
        } else {
            return;
        }

        if (totalViewHeight > screenHeight && mArcScrollBarView != null && firstItemScrollY != 0) {
            if (mArcScrollBarView.getVisibility() != View.VISIBLE) {
                mArcScrollBarView.setVisibility(View.VISIBLE);
            }
            mArcScrollBarView.
                    setStartAngleForScrollView(firstItemScrollY, screenHeight, totalViewHeight);
        }
    }

    public void setArcScrollBarView(ArcScrollBarView mArcScrollBarView) {
        this.mArcScrollBarView = mArcScrollBarView;
        mArcScrollBarView.setVisibility(View.GONE);
    }

    private void onScrollStart() {
        if (mArcScrollBarView != null && mArcScrollBarView.getVisibility() != View.VISIBLE) {
            mArcScrollBarView.setVisibility(View.VISIBLE);
        }
    }

    private void onScrollEnd() {
        if (mArcScrollBarView != null) {
            mArcScrollBarView.setVisibility(View.GONE);
        }
    }
}
