package com.example.zzang.gobang;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.zzang.gobang.controls.ButtonTextViewTouchListener;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView textViewReturn = (TextView) findViewById(R.id.textViewReturn);

        textViewReturn.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }
            @Override
            public void touchUpHandle(View v) {
                finish();
            }
        });

    }
}
