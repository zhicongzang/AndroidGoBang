package com.example.zzang.gobang.controls;

import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.zzang.gobang.R;

/**
 * Created by ZZANG on 11/16/16.
 */

public abstract class ButtonTextViewTouchListener implements View.OnTouchListener{

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TextView textView = (TextView) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                textView.setTextColor(resources().getColor(R.color.menuTextChosenColor));
                break;
            case MotionEvent.ACTION_UP:
                textView.setTextColor(resources().getColor(R.color.menuTextColor));
                touchUpHandle(v);
                break;

        }
        return true;
    }

    protected abstract Resources resources();

    public void touchUpHandle(View v) {}
}
