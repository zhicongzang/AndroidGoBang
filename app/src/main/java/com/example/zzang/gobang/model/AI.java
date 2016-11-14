package com.example.zzang.gobang.model;


import java.util.Observer;

/**
 * Created by ZZANG on 11/9/16.
 */

public class AI extends BoardAgent {

    private int level;

    public AI(int piece, int level, Observer o) {
        super(piece, o);
        this.level = level;

    }



}
