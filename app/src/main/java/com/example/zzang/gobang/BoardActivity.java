package com.example.zzang.gobang;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zzang.gobang.controls.BoardView;
import com.example.zzang.gobang.model.AI;
import com.example.zzang.gobang.model.Board;
import com.example.zzang.gobang.model.BoardAgent;
import com.example.zzang.gobang.model.ChessType;
import com.example.zzang.gobang.model.Position;
import com.example.zzang.gobang.model.WiFiAgent;
import com.example.zzang.gobang.model.WiFiGame;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


public class BoardActivity extends AppCompatActivity implements Observer {

    private BoardView boardView;
    private TextView blackTimeTextView;
    private TextView whiteTimeTextView;
    private TextView blackLabel;
    private TextView whiteLabel;
    private ImageView blackImageView;
    private ImageView whiteImageView;
    private SecondsCounter blackSecondsCounter = new SecondsCounter();
    private SecondsCounter whiteSecondsCounter = new SecondsCounter();
    private Timer timer = new Timer();
    private AlertDialog.Builder winAlertDialogBuilder;

    private BoardAgent agent;
    private boolean hasAgent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        boardView = (BoardView) findViewById(R.id.boardView);
        blackTimeTextView = (TextView) findViewById(R.id.blackTimeTextView);
        whiteTimeTextView = (TextView) findViewById(R.id.whiteTimeTextView);
        blackLabel = (TextView) findViewById(R.id.blackLabel);
        whiteLabel = (TextView) findViewById(R.id.whiteLabel);
        blackImageView = (ImageView) findViewById(R.id.blackImageView);
        whiteImageView = (ImageView) findViewById(R.id.whiteImageView);

        boardView.post(new Runnable() {
            @Override
            public void run() {
                boardView.setup(BoardActivity.this);
            }
        });
        winAlertDialogBuilder = new AlertDialog.Builder(BoardActivity.this);
        winAlertDialogBuilder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                reset();
            }
        });
        winAlertDialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        Intent intent = getIntent();
        String mode = intent.getStringExtra("Mode");
        if (mode != null) {
            switch (mode) {
                case "VSAI":
                    int aiPiece = intent.getIntExtra("AIPiece", 2);
                    int aiLevel = intent.getIntExtra("AILevel", 2);
                    agent = new AI(aiPiece, aiLevel, boardView.getBoardData(), this);
                    hasAgent = true;
                    break;
                case "WiFi":
                    WiFiGame game = intent.getParcelableExtra("Game");
                    int piece = intent.getIntExtra("OPPiece", 2);
                    String destinationIP = piece == ChessType.BLACK.ordinal() ? game.getBlackIPAddress() : game.getWhiteIPAddress() ;
                    boolean isDebugMode = intent.getBooleanExtra("DebugMode", false);
                    String debugModeIPAddres = null;
                    if (isDebugMode) {
                        debugModeIPAddres = intent.getStringExtra("DebugModeIPAddres");
                    }
                    agent = new WiFiAgent(piece, destinationIP,isDebugMode, debugModeIPAddres, this);
                    hasAgent = true;
                    setupView(game);
                    break;
                default:
                    break;
            }
        }

        reset();
    }

    private void setupView(WiFiGame game) {
        blackLabel.setText(game.getBlackPlayerName());
        whiteLabel.setText(game.getWhitePlayerName());
    }

    private void checkWin(int isWin) {
        timer.cancel();
        if (isWin == ChessType.WHITE.ordinal()) {
            winAlertDialogBuilder.setMessage("White Win!");
            winAlertDialogBuilder.create().show();
        } else if (isWin == ChessType.BLACK.ordinal()) {
            winAlertDialogBuilder.setMessage("Black Win!");
            winAlertDialogBuilder.create().show();
        } else {
            changeSide(boardView.getChessType());
        }
    }

    private void reset() {
        timer.cancel();
        boardView.reset();
        boardView.postInvalidate();
        blackSecondsCounter.reset();
        whiteSecondsCounter.reset();
        blackTimeTextView.setText(blackSecondsCounter.toString());
        whiteTimeTextView.setText(whiteSecondsCounter.toString());
        timer = new Timer();
        timer.schedule(new UpdateTimeTextViewTimerTask(blackTimeTextView, blackSecondsCounter), 1000, 1000);
        whiteImageView.setVisibility(View.INVISIBLE);
        blackImageView.setVisibility(View.VISIBLE);
        if (hasAgent && agent.getChessType().equals(ChessType.BLACK)) {
            boardView.blockTouchEvent();
            agent.procressNextPosition();
        }
    }

    private void changeSide(ChessType newChessType) {
        if (hasAgent) {
            if (agent.getChessType().equals(newChessType)) {
                boardView.blockTouchEvent();
                agent.procressNextPosition();
            } else {
                boardView.handleTouchEvent();
            }
        }
        switch (newChessType) {
            case WHITE:
                timer.cancel();
                timer = new Timer();
                timer.schedule(new UpdateTimeTextViewTimerTask(whiteTimeTextView, whiteSecondsCounter), 100, 1000);
                whiteImageView.setVisibility(View.VISIBLE);
                blackImageView.setVisibility(View.INVISIBLE);
                break;
            case BLACK:
                timer.cancel();
                timer = new Timer();
                timer.schedule(new UpdateTimeTextViewTimerTask(blackTimeTextView, blackSecondsCounter), 100, 1000);
                blackImageView.setVisibility(View.VISIBLE);
                whiteImageView.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (o instanceof BoardAgent) {
            if (arg != null && arg instanceof String) {
                String s = (String)arg;
                if(s.equals("Leave")) {
                    ((WiFiAgent)agent).stopListening();
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BoardActivity.this);
                    alertDialogBuilder.setPositiveButton("Continue game with AI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            agent = new AI(agent.getChessType().ordinal(), 0, boardView.getBoardData(), BoardActivity.this);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    alertDialogBuilder.setMessage("Your opponent has left.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialogBuilder.create().show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boardView.addChessToBoard(agent.getNextPosition());
                    }
                });
            }

        } else if (o instanceof Board) {
            final int isWin = (int) arg;
            if (agent instanceof WiFiAgent) {
                final Position position = boardView.getBoardData().getLastPosition();
                ((WiFiAgent) agent).sendPosition(position);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkWin(isWin);
                }
            });

        }

    }

    @Override
    public void finish() {
        if (agent instanceof WiFiAgent) {
            ((WiFiAgent) agent).stopListening();
        }
        super.finish();
    }

    class SecondsCounter {

        private int seconds = 0;

        public SecondsCounter() {
            seconds = 0;
        }

        public void reset() {
            seconds = 0;
        }

        public void run() {
            seconds += 1;
        }

        public String toString() {
            return secondsToString();
        }

        private String secondsToString() {
            int hour = 0;
            int min = 0;
            int sec = 0;
            if (seconds <= 0) {
                return "0:00";
            }
            min = seconds / 60;
            if (min >= 60) {
                hour = min / 60;
            }
            min = min % 60;
            sec = seconds % 60;
            if (hour > 0) {
                return String.valueOf(hour) + ":" + unitFormat(min) + ":" + unitFormat(sec);
            }
            return String.valueOf(min) + ":" + unitFormat(sec);
        }

        private String unitFormat(int i) {
            if (i < 10) {
                return "0" + String.valueOf(i);
            }
            return String.valueOf(i);
        }
    }

    class UpdateTimeTextViewTimerTask extends TimerTask {

        private TextView textView;
        private SecondsCounter secondsCounter;

        public UpdateTimeTextViewTimerTask(TextView textView, SecondsCounter secondsCounter) {
            this.textView = textView;
            this.secondsCounter = secondsCounter;
        }

        @Override
        public void run() {
            secondsCounter.run();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(secondsCounter.toString());
                }
            });
        }
    }

}
