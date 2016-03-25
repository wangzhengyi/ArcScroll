package com.watch.arcscroll.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ArcListActivity.class);
        startActivity(intent);
    }

    public void startScrollActivity(View view) {

    }
}
