package com.example.zzang.gobang;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzang.gobang.controls.ButtonTextViewTouchListener;

public class SetupAIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_ai);
        final RadioGroup pieceRadioGroup = (RadioGroup)findViewById(R.id.AIPieceRadioGroup);
        pieceRadioGroup.check(R.id.blackPieceRadioButton);
        final RadioGroup levelRadioGroup = (RadioGroup)findViewById(R.id.AILevelRadioGroup);
        levelRadioGroup.check(R.id.easyLevelRadioButton);
        Button normalLevelButton = (Button)findViewById(R.id.normalLevelRadioButton);
        normalLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelRadioGroup.check(R.id.easyLevelRadioButton);
                Toast.makeText(getApplicationContext(),"Will coming soon.",Toast.LENGTH_SHORT).show();
            }
        });
        Button hardLevelButton = (Button)findViewById(R.id.hardLevelRadioButton);
        hardLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelRadioGroup.check(R.id.easyLevelRadioButton);
                Toast.makeText(getApplicationContext(),"Will coming soon.",Toast.LENGTH_SHORT).show();
            }
        });
        Button godLevelButton = (Button)findViewById(R.id.godLevelRadioButton);
        godLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelRadioGroup.check(R.id.easyLevelRadioButton);
                Toast.makeText(getApplicationContext(),"Will coming soon.",Toast.LENGTH_SHORT).show();
            }
        });
        TextView startTextView = (TextView)findViewById(R.id.startTextView);
        startTextView.setOnTouchListener(new ButtonTextViewTouchListener() {
            @Override
            protected Resources resources() {
                return getResources();
            }
            @Override
            public void touchUpHandle(View v) {
                Intent intent = new Intent(SetupAIActivity.this, BoardActivity.class);
                intent.putExtra("Mode","VSAI");
                int AIPiece = Integer.parseInt((String)((TextView)findViewById(pieceRadioGroup.getCheckedRadioButtonId())).getTag());
                int AILevel = Integer.parseInt((String)((TextView)findViewById(levelRadioGroup.getCheckedRadioButtonId())).getTag());
                intent.putExtra("AIPiece", AIPiece);
                intent.putExtra("AILevel", AILevel);
                startActivity(intent);
            }
        });

        TextView backTextView = (TextView)findViewById(R.id.backTextView);
        backTextView.setOnTouchListener(new ButtonTextViewTouchListener() {
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
