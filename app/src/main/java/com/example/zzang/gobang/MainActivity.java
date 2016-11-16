package com.example.zzang.gobang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzang.gobang.model.WiFiAgent;

public class  MainActivity extends AppCompatActivity {

    private String IPAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IPAddress = WiFiAgent.getWIFILocalIpAdress(getApplicationContext());

        final TextView textViewVSAI = (TextView) findViewById(R.id.textViewVSAI);
        final TextView textViewVSPlayer = (TextView) findViewById(R.id.textViewVSPlayer);
        final TextView textViewWiFiBattle = (TextView) findViewById(R.id.textViewWiFiBattle);
        final TextView textViewAbout = (TextView) findViewById(R.id.textViewAbout);

        textViewVSAI.setOnTouchListener(new MenuTouchListener() {
            @Override
            public void touchUpHandle(View v) {
                Intent intent = new Intent(MainActivity.this, SetupAIActivity.class);
                startActivity(intent);
            }
        });

        textViewVSPlayer.setOnTouchListener(new MenuTouchListener() {
            @Override
            public void touchUpHandle(View v) {
                Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                startActivity(intent);
            }
        });

        textViewWiFiBattle.setOnTouchListener(new MenuTouchListener() {
            @Override
            public void touchUpHandle(View v) {
                if (IPAddress == null || IPAddress.equals("0.0.0.0")) {
                    IPAddress = WiFiAgent.getWIFILocalIpAdress(getApplicationContext());
                    Toast.makeText(getApplicationContext(),"Please connect a WiFi network.",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("IP Address: ", IPAddress);
                    Intent intent = new Intent(MainActivity.this, SetupWiFiBattleActivity.class);
                    intent.putExtra("IPAddress",IPAddress);
                    startActivity(intent);
//                    Intent intent = new Intent(MainActivity.this, BoardActivity.class);
//                    intent.putExtra("Mode","WiFi");
//                    intent.putExtra("OPPiece", 2);
//                    startActivity(intent);
                }

            }
        });

        textViewAbout.setOnTouchListener(new MenuTouchListener() {
            @Override
            public void touchUpHandle(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });


    }

    abstract class MenuTouchListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView textView = (TextView) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextChosenColor));
                    break;
                case MotionEvent.ACTION_UP:
                    ((TextView) v).setTextColor(getResources().getColor(R.color.menuTextColor));
                    touchUpHandle(v);
                    break;

            }
            return true;
        }

        public void touchUpHandle(View v) {}
    }
}
