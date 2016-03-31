# ArcScroll

ArcScroll是用来适配圆形屏幕的ScrollBar弧形滚动条控件。目前支持替换ScrollView和ListView等滑动控件的原生ScrollBar。

******

# Demo

跟ListView适配:

![ListView](https://github.com/wangzhengyi/ArcScroll/raw/master/screenshots/list_view_with_arc_scroll.png)

跟ScrollView适配:

![ScrollView](https://github.com/wangzhengyi/ArcScroll/raw/master/screenshots/scrollview_with_arc_scroll.png)

******

# Usage

******

## Gradle

```groovy
dependencies {
    compile project(':library')
}
```

******

## Android.mk

```makefile
arc_scroll_dir := ../ArcScroll
src_dirs := src $(arc_scroll_dir)/src
res_dirs := res $(arc_scroll_dir)/res

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.watch.arcscroll
```

******

## 使用示例

******

### 概述

首先，不论与ListView还是ScrollView相结合，都需要配合FrameLayout。通过FrameLayout的叠加作用，将ArcScrollBar绘制在屏幕右侧。

******

### 自定义属性

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="ArcScrollBarView">
        <attr name="thumb_color" format="color" />
        <attr name="track_color" format="color" />
        <attr name="margin_top" format="dimension" />
        <attr name="stroke_width" format="dimension" />
    </declare-styleable>
</resources>
```

属性的含义如下:

* thumb_color：滑块的颜色.
* track_color：轨道的颜色.
* stroke_width：轨道的宽度.
* margin_top：轨道起始点距离顶部的高度(单位:px).

注意，使用该自定义控件时，ListView和ScrollView必须取消自带的ScrollView控件。
示例代码如下：
```xml
android:scrollbars="none"
```

******

### ArcScrollBar统一布局

抽出ArcScrollBar的统一布局，大家在使用的时候可以直接include这个layout即可。
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.watch.arcscroll.ArcScrollBarView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/id_arc_scroll_bar"
    style="@style/ArcScrollBarView_Default"/>
```

默认style的样式如下:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="ArcScrollBarView_Default">
        <item name="android:layout_width">match_parent</item>
        <item name="android:layout_height">match_parent</item>
        <item name="android:visibility">visible</item>
        <item name="thumb_color">#00b1cb</item>
        <item name="track_color">#9d9d9d</item>
        <item name="stroke_width">4px</item>
        <item name="has_actionbar">false</item>
    </style>
</resources>
```

大家可以参考默认样式，根据UI需求，自行修改。

******
### 与ListView结合使用

布局文件:
```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/black"
        android:orientation="vertical">
        <ListView
            android:id="@+id/id_list_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scrollbars="none"/>

    </LinearLayout>
    <include layout="@layout/arc_scroll_bar_view_layout" />
</FrameLayout>
```

有了外层的FrameLayout，才能将ArcScrollBar绘制在ListView上。

代码:
```java
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
```
需要在ListView的OnScrollListener接口中对ArcScrollBarView进行操作.具体操作细节，大家参考上述代码即可。

******
### 与ScrollView结合

这里需要注意，如果大家要使用ScrollView时，需要用该控件库提供的ArcScrollView进行替代。
布局文件:
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#000000">

    <com.watch.arcscroll.ArcScrollView
        android:id="@+id/id_arc_scroll_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="none">

        <LinearLayout
            android:id="@+id/id_container_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical" />
    </com.watch.arcscroll.ArcScrollView>

    <include layout="@layout/arc_scroll_bar_view_layout" />
</FrameLayout>
```

代码:
```java
    private void initView() {
        ArcScrollView arcScrollView = (ArcScrollView) findViewById(R.id.id_arc_scroll_view);
        ArcScrollBarView arcScrollBarView = (ArcScrollBarView) findViewById(R.id.id_arc_scroll_bar);
        // 绑定ArcScrollView与ArcScrollBarView
        arcScrollView.setArcScrollBarView(arcScrollBarView);
    }
```

******
