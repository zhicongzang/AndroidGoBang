package com.example.zzang.gobang.model;


/**
 * Created by ZZANG on 11/9/16.
 */

public class AI {

    private ChessType chessType;
    private int level;

    public AI(int piece, int level) {
        this.level = level;
        if(piece == ChessType.BLACK.ordinal()) {
            chessType = ChessType.BLACK;
        } else {
            chessType = ChessType.WHITE;
        }
    }

    public ChessType getChessType() {
        return chessType;
    }
}
