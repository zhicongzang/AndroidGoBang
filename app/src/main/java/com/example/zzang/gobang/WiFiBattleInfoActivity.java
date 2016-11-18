package com.example.zzang.gobang;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Message;
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

import com.alibaba.fastjson.JSON;
import com.example.zzang.gobang.model.ChessType;
import com.example.zzang.gobang.model.WiFiGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WiFiBattleInfoActivity extends AppCompatActivity {

    private final static int WIFI_GAMES_PORT = 44444;
    private final static int SEARCH_WIFI_GAMES_PORT = 33333;


//    private Rect vaildRect;
    private WiFiGame game;
    private String myIPAddress;
    private String myName;

    private ServerSocket serverSocket;
    private ExecutorService serverThreadPool;
    private Socket recieveSocket;
    // Server is visible or not.
    private boolean isVisible = false;
    // Server is listening or not.
    private boolean isListening = false;

    private TextView whitePlayerNameTextView;
    private TextView blackPlayerNameTextView;
    private ImageView whiteImageView;
    private ImageView blackImageView;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_battle_info);
        startButton = (Button) findViewById(R.id.startButton);

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
        // Create a new game or add a game
        if (game == null) {
            game = new WiFiGame(myIPAddress,myName);
            startButton.setVisibility(View.VISIBLE);
            startButton.setEnabled(false);
            startButton.setText("Waiting...");
            isVisible = true;
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket(game.getWhiteIPAddress(), WIFI_GAMES_PORT);
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(getResources().getString(R.string.start_request));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    // Set a white Agent.
                    Intent intentToBoardActivity = new Intent(WiFiBattleInfoActivity.this, BoardActivity.class);
                    intentToBoardActivity.putExtra("Game", game);
                    intentToBoardActivity.putExtra("Mode", "WiFi");
                    intentToBoardActivity.putExtra("OPPiece", ChessType.WHITE.ordinal());
                    startActivity(intentToBoardActivity);
                    finish();
                }
            });
        } else {
            game.setWhiteIPAddress(myIPAddress);
            game.setWhitePlayerName(myName);
            isVisible = false;
        }


        whitePlayerNameTextView = (TextView)findViewById(R.id.whitePlayerNameTextView);
        blackPlayerNameTextView = (TextView)findViewById(R.id.blackPlayerNameTextView);
        whiteImageView = (ImageView)findViewById(R.id.whiteImageView);
        blackImageView = (ImageView)findViewById(R.id.blackImageView);
        setupView();

        try {
            serverSocket = new ServerSocket(WIFI_GAMES_PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        serverThreadPool = Executors.newFixedThreadPool(2);
        startServer();
    }

    private void setupView() {
        String blackName = game.getBlackPlayerName() == null ? "" : game.getBlackPlayerName();
        String whiteName = game.getWhitePlayerName() == null ? "" : game.getWhitePlayerName();
        blackPlayerNameTextView.setText(blackName);
        whitePlayerNameTextView.setText(whiteName);
        if (game.getBlackPlayerName() != null) {
            blackImageView.setVisibility(View.VISIBLE);
        }
        if (game.getWhitePlayerName() != null) {
            whiteImageView.setVisibility(View.VISIBLE);
        }
        if (startButton.getVisibility() == View.VISIBLE && WiFiGame.isComplete(game)) {
            startButton.setText("Start");
            startButton.setEnabled(true);
        }
    }

    private void startServer() {
        if (isListening) {
            return;
        }
        isListening = true;
        serverThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while(isListening) {
                    try {
                        recieveSocket = serverSocket.accept();
                        ObjectInputStream ois = new ObjectInputStream(recieveSocket.getInputStream());
                        String request = (String) ois.readObject();
                        Log.d("Request", request);
                        if (request.equals(getResources().getString(R.string.searching_request))){
                            if (isVisible) {
                                Socket socket = new Socket(recieveSocket.getInetAddress().getHostAddress(), SEARCH_WIFI_GAMES_PORT);
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(JSON.toJSONString(game));
                            }
                        } else if (request.equals(getResources().getString(R.string.start_request))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Set a black agent.
                                    Intent intentToBoardActivity = new Intent(WiFiBattleInfoActivity.this, BoardActivity.class);
                                    intentToBoardActivity.putExtra("Game", game);
                                    intentToBoardActivity.putExtra("Mode", "WiFi");
                                    intentToBoardActivity.putExtra("OPPiece", ChessType.BLACK.ordinal());
                                    startActivity(intentToBoardActivity);
                                    finish();
                                }
                            });
                        } else {
                            WiFiGame completeGame = JSON.parseObject(request, WiFiGame.class);
                            if (game.compareGame(completeGame)) {

                                game = completeGame;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setupView();
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void stopServer() {
        if (!isListening) {
            return;
        }
        isListening = false;
        try {
            if(recieveSocket != null) {
                recieveSocket.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            serverThreadPool.shutdown();
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        stopServer();
        super.finish();
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
