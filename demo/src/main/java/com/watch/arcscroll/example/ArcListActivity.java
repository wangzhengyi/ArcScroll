package com.watch.arcscroll.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.watch.arcscroll.ArcScrollBarView;
import com.watch.arcscroll.example.adapter.SimpleListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArcListActivity extends Activity {
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_list);

        initData();

        initView();
    }

    private void initData() {
        list.clear();
        for (int i = 1; i < 50; i ++) {
            list.add("第" + i + "天");
        }
    }

    private void initView() {
        // init list view
        ListView listview = (ListView) findViewById(R.id.id_list_view);
        ListAdapter adapter = new SimpleListAdapter(this, list);
        listview.setAdapter(adapter);

        // init arc scroll view
        final ArcScrollBarView mArcScrollBarView =
                (ArcScrollBarView) findViewById(R.id.id_arc_scroll_bar);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 滑动停止
                        if (mArcScrollBarView != null) {
                            mArcScrollBarView.setVisibility(View.GONE);
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 滑动进行中
                        if (mArcScrollBarView != null) {
                            mArcScrollBarView.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                // 无数据项显示时，直接返回
                if (visibleItemCount == 0) {
                    return;
                }

                // 显示Arc Scrollbar
                if (mArcScrollBarView != null) {
                    mArcScrollBarView.setStartAngle(
                            firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
    }
}
