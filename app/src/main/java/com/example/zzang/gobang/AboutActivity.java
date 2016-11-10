package com.example.zzang.gobang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView textViewReturn = (TextView) findViewById(R.id.textViewReturn);


        textViewReturn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = (TextView) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextChosenColor));
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextColor));
                        finish();
                        break;
                }
                return true;
            }
        });

    }
}
