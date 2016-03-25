package com.watch.arcscroll.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.watch.arcscroll.ArcScrollBarView;
import com.watch.arcscroll.ArcScrollView;

public class ArcScrollActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acr_scroll);

        initView();
    }

    private void initView() {
        ArcScrollView arcScrollView = (ArcScrollView) findViewById(R.id.id_arc_scroll_view);
        ArcScrollBarView arcScrollBarView = (ArcScrollBarView) findViewById(R.id.id_arc_scroll_bar);
        // 绑定ArcScrollView与ArcScrollBarView
        arcScrollView.setArcScrollBarView(arcScrollBarView);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.id_container_layout);

        // 添加测试数据
        for (int i = 0; i < 50; i ++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            textView.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                    getResources().getDisplayMetrics()));
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setText("数字" + i);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
        }
    }
}
