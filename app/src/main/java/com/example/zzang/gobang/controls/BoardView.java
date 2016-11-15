package com.example.zzang.gobang.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.zzang.gobang.BoardActivity;
import com.example.zzang.gobang.R;
import com.example.zzang.gobang.model.Board;
import com.example.zzang.gobang.model.ChessType;
import com.example.zzang.gobang.model.Position;

import java.util.LinkedList;
import java.util.List;



/**
 * Created by ZZANG on 11/1/16.
 */

public class BoardView extends View {

    private Paint paint = new Paint();

    private Board boardData = new Board();
    private List<Position> blackChesses = new LinkedList<>();
    private List<Position> whiteChesses = new LinkedList<>();
    private int boardWidth;
    private int lineWidth;
    private int borderWidth;
    private int chessWidth;
    private Bitmap whiteChessBitmap;
    private Bitmap blackChessBitmap;
    private ChessType chessType = ChessType.BLACK;
    private int isWin = 0;

    private boolean isBlockTouchEvent = false;
    private BoardActivity boardActivity;

    public BoardView(Context context) {
        super(context);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
        boardWidth = getWidth();
        lineWidth = (int) ((float)boardWidth / (22*2 + 30*14) *30);
        borderWidth = (int) ((float)boardWidth / (22*2 + 30*14) *22);
        chessWidth = lineWidth;
        whiteChessBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.stone_white),chessWidth,chessWidth,true);
        blackChessBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.stone_black),chessWidth,chessWidth,true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(!isBlockTouchEvent) {
            addChessToBoard(new Position(getChessCol(x), getChessRow(y)));
        } else {
            Toast.makeText(getContext(),"Please wait...", Toast.LENGTH_SHORT).show();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Position position: blackChesses) {
            canvas.drawBitmap(blackChessBitmap, borderWidth + position.getCol() * lineWidth - chessWidth / 2, borderWidth + position.getRow() * lineWidth - chessWidth / 2, paint);
        }
        for (Position position: whiteChesses) {
            canvas.drawBitmap(whiteChessBitmap, borderWidth + position.getCol() * lineWidth - chessWidth / 2, borderWidth + position.getRow() * lineWidth - chessWidth / 2, paint);
        }

    }

    private int getChessCol(float x) {
        return Math.round((x - (float)borderWidth) / (float)lineWidth);
    }

    private int getChessRow(float y) {
        return Math.round((y - (float)borderWidth) / (float)lineWidth);
    }

    public void addChessToBoard(Position position) {
        if (boardData.addChess(position, chessType)) {
            if(chessType.ordinal() == ChessType.BLACK.ordinal()) {
                blackChesses.add(position);
                chessType = ChessType.WHITE;
            } else {
                whiteChesses.add(position);
                chessType = ChessType.BLACK;
            }
            invalidate();
            isWin = boardData.checkWin();
            boardActivity.checkWin(isWin, chessType);
        }
    }

    public void blockTouchEvent() {
        isBlockTouchEvent = true;
    }

    public void handleTouchEvent() {
        isBlockTouchEvent = false;
    }

    public void reset() {
        isWin = 0;
        chessType = ChessType.BLACK;
        boardData.reset();
        blackChesses = new LinkedList<>();
        whiteChesses = new LinkedList<>();
        invalidate();
    }



}
