package com.example.zzang.gobang.model;

import java.io.Serializable;

/**
 * Created by ZZANG on 11/8/16.
 */

public class Position implements Serializable {

    private int col;
    private int row;

    public Position() { }


    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String toString() {
        return "col: " + String.valueOf(col) + " row: " + String.valueOf(row);
    }

    public Position(String s) {
        String[] ss = s.split(":");
        col = Integer.parseInt(ss[1]);
        row = Integer.parseInt(ss[3]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position p = (Position) obj;
            if (col == p.getCol() && row == p.getRow()) {
                return true;
            }
        }
        return false;
    }
}
