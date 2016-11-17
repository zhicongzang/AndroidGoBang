package com.example.zzang.gobang;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zzang.gobang.model.WiFiGame;

public class WiFiBattleInfoActivity extends AppCompatActivity {

//    private Rect vaildRect;
    private WiFiGame game;
    private String myIPAddress;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_battle_info);
        Button button = (Button) findViewById(R.id.startButton);

        final LinearLayout popLayout = (LinearLayout)findViewById(R.id.popLayout);
//        popLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                vaildRect = new Rect(0,0,popLayout.getWidth(),popLayout.getHeight());
//            }
//        });

        Intent intent = getIntent();
        myIPAddress = intent.getStringExtra("IPAddress");
        myName = intent.getStringExtra("Name");
        game = intent.getParcelableExtra("Game");
        if (game == null) {
            game = new WiFiGame(myIPAddress,myName);
            button.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            button.setText("Waiting...");
        } else {
            game.setWhiteIPAddress(myIPAddress);
            game.setWhitePlayerName(myName);
        }

        TextView whitePlayerNameTextView = (TextView)findViewById(R.id.whitePlayerNameTextView);
        TextView blackPlayerNameTextView = (TextView)findViewById(R.id.blackPlayerNameTextView);
        String blackName = game.getBlackPlayerName() == null ? "" : game.getBlackPlayerName();
        String whiteName = game.getWhitePlayerName() == null ? "" : game.getWhitePlayerName();
        blackPlayerNameTextView.setText(blackName);
        whitePlayerNameTextView.setText(whiteName);
        ImageView whiteImageView = (ImageView)findViewById(R.id.whiteImageView);
        ImageView blackImageView = (ImageView)findViewById(R.id.blackImageView);
        if (game.getBlackPlayerName() != null) {
            blackImageView.setVisibility(View.VISIBLE);
        }
        if (game.getWhitePlayerName() != null) {
            whiteImageView.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        if (!vaildRect.contains(x, y)) {
//            finish();
//        }
//
//        return true;
//    }
}
