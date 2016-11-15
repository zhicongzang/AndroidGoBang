package com.example.zzang.gobang.model;

import java.security.PublicKey;

/**
 * Created by ZZANG on 11/15/16.
 */

public enum ScoreType {
    FIVE, LIVE_FOUR, DEAD_FOUR, LIVE_THREE, DEAD_THREE, LIVE_TWO, DEAR_TWO, ONE;

    public static ScoreType evaluate(int[][] data, Position position) {
        return ONE;
    }

    public int getScore() {
        switch (this) {
            case FIVE:
                return 1;
            case LIVE_FOUR:
                return 1;
            case DEAD_FOUR:
                return 1;
            case LIVE_THREE:
                return 1;
            case DEAD_THREE:
                return 1;
            case LIVE_TWO:
                return 1;
            case DEAR_TWO:
                return 1;
            default:
                return 1;
        }
    }
}
