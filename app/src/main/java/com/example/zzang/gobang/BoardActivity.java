package com.example.zzang.gobang;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzang.gobang.controls.BoardView;
import com.example.zzang.gobang.model.AI;
import com.example.zzang.gobang.model.Board;
import com.example.zzang.gobang.model.ChessType;
import com.example.zzang.gobang.model.Position;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BoardActivity extends AppCompatActivity {

    private Board boardData = new Board();
    private BoardView boardView;
    private TextView blackTimeTextView;
    private TextView whiteTimeTextView;
    private ImageView blackImageView;
    private ImageView whiteImageView;
    private SecondsCounter blackSecondsCounter = new SecondsCounter();
    private SecondsCounter whiteSecondsCounter = new SecondsCounter();
    private Timer timer = new Timer();
    private AlertDialog.Builder winAlertDialogBulider;

    private AI ai;
    private boolean hasAI = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        boardView = (BoardView) findViewById(R.id.boardView);
        blackTimeTextView = (TextView) findViewById(R.id.blackTimeTextView);
        whiteTimeTextView = (TextView) findViewById(R.id.whiteTimeTextView);
        blackImageView = (ImageView) findViewById(R.id.blackImageView);
        whiteImageView = (ImageView) findViewById(R.id.whiteImageView);
        boardView.post(new Runnable() {
            @Override
            public void run() {
                boardView.setup(BoardActivity.this);
            }
        });
        winAlertDialogBulider = new AlertDialog.Builder(BoardActivity.this);
        winAlertDialogBulider.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                reset();
            }
        });
        winAlertDialogBulider.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        Intent intent = getIntent();
        String mode = intent.getStringExtra("Mode");
        if(mode != null) {
            switch (mode) {
                case "VSAI":
                    int piece = intent.getIntExtra("AIPiece",2);
                    int level = intent.getIntExtra("AILevel",2);
                    ai = new AI(piece, level);
                    hasAI = true;
                    break;
                case "Battle":
                    break;
                default:
                    break;
            }
        }

        reset();

    }

    public boolean addChessToBoard(Position position) {
        Log.d("asd", "col: " + String.valueOf(position.getCol()) + " row: " + String.valueOf(position.getRow()));
        return boardData.addChess(position);
    }

    public LinkedList<Position> getWhiteChessPositionsFromBoardData() {
        return boardData.getWhiteChessPositions();
    }

    public LinkedList<Position> getBlackChessPositionsFromBoardData() {
        return boardData.getBlackChessPositions();
    }

    public void checkWin() {
        timer.cancel();
        switch (boardData.isWin()) {
            case WHITE:
                winAlertDialogBulider.setMessage("White Win!");
                winAlertDialogBulider.create().show();
                break;
            case BLACK:
                winAlertDialogBulider.setMessage("Black Win!");
                winAlertDialogBulider.create().show();
                break;
            default:
                changeSide();
                break;
        }
    }

    private void reset() {
        timer.cancel();
        boardData.reset();
        boardView.invalidate();
        blackSecondsCounter.reset();
        whiteSecondsCounter.reset();
        blackTimeTextView.setText(blackSecondsCounter.toString());
        whiteTimeTextView.setText(whiteSecondsCounter.toString());
        timer = new Timer();
        timer.schedule(new UpdateTimeTextViewTimerTask(blackTimeTextView, blackSecondsCounter), 1000, 1000);
        whiteImageView.setVisibility(View.INVISIBLE);
        blackImageView.setVisibility(View.VISIBLE);
        if(hasAI && ai.getChessType().equals(ChessType.BLACK)) {
            boardView.blockTouchEvent();
        }
    }

    private void changeSide() {
        if(hasAI) {
            if(ai.getChessType().equals(boardData.getChessType())) {
                boardView.blockTouchEvent();
            } else {
                boardView.handleTouchEvent();
            }
        }
        switch (boardData.getChessType()) {
            case WHITE:
                timer = new Timer();
                timer.schedule(new UpdateTimeTextViewTimerTask(whiteTimeTextView, whiteSecondsCounter),500,1000);
                whiteImageView.setVisibility(View.VISIBLE);
                blackImageView.setVisibility(View.INVISIBLE);
                break;
            case BLACK:
                timer = new Timer();
                timer.schedule(new UpdateTimeTextViewTimerTask(blackTimeTextView, blackSecondsCounter),500,1000);
                blackImageView.setVisibility(View.VISIBLE);
                whiteImageView.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }



    class SecondsCounter {

        private int seconds = 0;

        public SecondsCounter() {
            seconds = 0;
        }

        public SecondsCounter(int seconds) {
            this.seconds = seconds;
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
            String str = "";
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
