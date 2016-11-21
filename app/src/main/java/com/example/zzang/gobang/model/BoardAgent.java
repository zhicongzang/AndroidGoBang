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

    abstract public void procressNextPosition();

}
