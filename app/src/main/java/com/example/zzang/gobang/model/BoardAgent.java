package com.example.zzang.gobang.model;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by zzc on 11/10/16.
 */

public abstract class BoardAgent extends Observable {

    protected ChessType chessType;
    protected Position nextPosition;

    private Random random = new Random();

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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int col = random.nextInt() % 15;
                int row = random.nextInt() % 15;
                setNextPosition(new Position(col, row));
            }
        });
        t.start();
    }

}
