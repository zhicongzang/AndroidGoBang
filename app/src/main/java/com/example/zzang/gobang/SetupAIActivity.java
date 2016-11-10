package com.example.zzang.gobang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SetupAIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_ai);
        final RadioGroup pieceRadioGroup = (RadioGroup)findViewById(R.id.AIPieceRadioGroup);
        pieceRadioGroup.check(R.id.blackPieceRadioButton);
        final RadioGroup levelRadioGroup = (RadioGroup)findViewById(R.id.AILevelRadioGroup);
        levelRadioGroup.check(R.id.normalLevelRadioButton);
        TextView startTextView = (TextView)findViewById(R.id.startTextView);
        startTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TextView textView = (TextView) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextChosenColor));
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextColor));
                        Intent intent = new Intent(SetupAIActivity.this, BoardActivity.class);
                        intent.putExtra("Mode","VSAI");
                        int AIPiece = Integer.parseInt((String)((TextView)findViewById(pieceRadioGroup.getCheckedRadioButtonId())).getTag());
                        int AILevel = Integer.parseInt((String)((TextView)findViewById(levelRadioGroup.getCheckedRadioButtonId())).getTag());
                        intent.putExtra("AIPiece", AIPiece);
                        intent.putExtra("AILevel", AILevel);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        TextView backTextView = (TextView)findViewById(R.id.backTextView);
        backTextView.setOnTouchListener(new View.OnTouchListener() {
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
