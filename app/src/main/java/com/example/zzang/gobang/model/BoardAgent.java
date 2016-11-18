package com.example.zzang.gobang.model;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by zzc on 11/10/16.
 */

public abstract class BoardAgent extends Observable {

    protected ChessType chessType;
    protected Position nextPosition;

    protected Random random = new Random();

    public BoardAgent(int piece, Observer o) {
        if(piece == ChessType.BLACK.ordinal()) {
            chessType = ChessType.BLACK;
        } else {
            chessType = ChessType.WHITE;
        }
        this.addObserver(o);
    }

    public ChessType getChessType() {
        return chessType;
    }

    public Position getNextPosition() {
        return nextPosition;
    }


    protected void setNextPosition(Position nextPosition) {
        if (nextPosition != null) {
            this.nextPosition = nextPosition;
            setChanged();
            notifyObservers();
        }
    }

    public void releaseNextPosition() {
        nextPosition = null;
    }

    public void calculateNewNextPosition() {
        testRandomNextPosition();
    }

    public void testRandomNextPosition() {
        int col = random.nextInt(14);
        int row = random.nextInt(14);
        Position p = new Position(col, row);
        Log.d("Pos", "testRandomNextPosition: "+ p.toString());
        setNextPosition(p);
    }

    abstract public void procressNextPosition();

}
