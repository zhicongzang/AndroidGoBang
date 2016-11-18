package com.example.zzang.gobang.model;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Random;

/**
 * Created by ZZANG on 11/9/16.
 */

public class AI extends BoardAgent {

    private int level;
    private Board boardData;
    private ArrayList<Position> dangerousPositions = new ArrayList<>();

    public AI(int piece, int level, Board boardData, Observer o) {
        super(piece, o);
        this.level = level;
        this.boardData = boardData;
    }

    private void evaluateDangerousPositions(Position originalPosition) {
        dangerousPositions = new ArrayList<>();
        int col = originalPosition.getCol();
        int row = originalPosition.getRow();
        int type = boardData.getType(col, row);
        evaluateDangerousPositionsColumn(type, col, row);
        evaluateDangerousPositionsRow(type, col, row);
        evaluateDangerousPositionsLeftTop(type, col, row);
        evaluateDangerousPositionsLeftBottom(type, col, row);

    }

    private void evaluateDangerousPositionsColumn(int type, int col, int row) {
        int r = row - 1;
        int count = 1;
        // type
        int  t = 0;
        int emptyCount = 0;
        boolean lastIsEmpty = false;
        boolean isDead = false;
        ArrayList<Position> ps = new ArrayList<>();
        while(r>=0) {
            t = boardData.getType(col,r);
            if (t == type) {
                count += 1;
                r -= 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(col, r));
                r -= 1;
                emptyCount += 1;
            }
        }
        r = row + 1;
        lastIsEmpty = false;
        emptyCount = 0;
        while(r<15) {
            t = boardData.getType(col,r);
            if (t == type) {
                count += 1;
                r += 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                    break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(col, r));
                r += 1;
                emptyCount += 1;
            }
        }
        switch (count) {
            case 5:
                dangerousPositions.addAll(ps);
                break;
            case 4:
                for(Position p: ps) {
                    int pc = p.getCol();
                    int pr = p.getRow();
                    if (pr+1<15 && boardData.getType(pc,pr+1)!=ChessType.EMPTY.ordinal()
                            && pr-1>=0 && boardData.getType(pc,pr-1)!=ChessType.EMPTY.ordinal()) {
                        dangerousPositions.add(p);
                        return;
                    }
                }
                dangerousPositions.addAll(ps);
                break;
            case 3:
                if (!isDead) {
                    dangerousPositions.addAll(ps);
                }
                break;
            default:
                break;
        }

    }

    private void evaluateDangerousPositionsRow(int type, int col, int row) {
        int c = col - 1;
        int count = 1;
        // type
        int  t = 0;
        int emptyCount = 0;
        boolean lastIsEmpty = false;
        boolean isDead = false;
        ArrayList<Position> ps = new ArrayList<>();
        while(c>=0) {
            t = boardData.getType(c,row);
            if (t == type) {
                count += 1;
                c -= 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else  if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c, row));
                c -= 1;
                emptyCount += 1;
            }
        }
        c = col + 1;
        lastIsEmpty = false;
        emptyCount = 0;
        while(c<15) {
            t = boardData.getType(c,row);
            if (t == type) {
                count += 1;
                c += 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else  if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c, row));
                c += 1;
                emptyCount += 1;
            }
        }
        switch (count) {
            case 5:
                dangerousPositions.addAll(ps);
                break;
            case 4:
                for(Position p: ps) {
                    int pc = p.getCol();
                    int pr = p.getRow();
                    if (pc-1>=0 && boardData.getType(pc-1,pr)!=ChessType.EMPTY.ordinal()
                            && pc+1<15 && boardData.getType(pc+1,pr)!=ChessType.EMPTY.ordinal()) {
                        dangerousPositions.add(p);
                        return;
                    }
                }
                dangerousPositions.addAll(ps);
                break;
            case 3:
                if (!isDead) {
                    dangerousPositions.addAll(ps);
                }
                break;
            default:
                break;
        }

    }

    private void evaluateDangerousPositionsLeftTop(int type, int col, int row) {
        int c = col - 1;
        int r = row - 1;
        int count = 1;
        // type
        int  t = 0;
        int emptyCount = 0;
        boolean lastIsEmpty = false;
        boolean isDead = false;
        ArrayList<Position> ps = new ArrayList<>();
        while(c>=0 && r >=0) {
            t = boardData.getType(c,r);
            if (t == type) {
                count += 1;
                c -= 1;
                r -= 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c,r));
                c -= 1;
                r -= 1;
                emptyCount += 1;
            }
        }
        c = col + 1;
        r = row + 1;
        lastIsEmpty = false;
        emptyCount = 0;
        while(c<15 && r<15) {
            t = boardData.getType(c,r);
            if (t == type) {
                count += 1;
                c += 1;
                r += 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c,r));
                c += 1;
                r += 1;
                emptyCount += 1;
            }
        }
        switch (count) {
            case 5:
                dangerousPositions.addAll(ps);
                break;
            case 4:
                for(Position p: ps) {
                    int pc = p.getCol();
                    int pr = p.getRow();
                    if (pc-1>=0 && pr-1>=0 && boardData.getType(pc-1,pr-1)!=ChessType.EMPTY.ordinal()
                            && pc+1<15 && pr+1<15 && boardData.getType(pc+1,pr+1)!=ChessType.EMPTY.ordinal()) {
                        dangerousPositions.add(p);
                        return;
                    }
                }
                dangerousPositions.addAll(ps);
                break;
            case 3:
                if (!isDead) {
                    dangerousPositions.addAll(ps);
                }
                break;
            default:
                break;
        }

    }

    private void evaluateDangerousPositionsLeftBottom(int type, int col, int row) {
        int c = col - 1;
        int r = row + 1;
        int count = 1;
        // type
        int  t = 0;
        int emptyCount = 0;
        boolean lastIsEmpty = false;
        boolean isDead = false;
        ArrayList<Position> ps = new ArrayList<>();
        while(c>=0 && r<15) {
            t = boardData.getType(c,r);
            if (t == type) {
                count += 1;
                c -= 1;
                r += 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c,r));
                c -= 1;
                r += 1;
                emptyCount += 1;
            }
        }
        c = col + 1;
        r = row - 1;
        lastIsEmpty = false;
        emptyCount = 0;
        while(c<15 && r>=0) {
            t = boardData.getType(c,r);
            if (t == type) {
                count += 1;
                c += 1;
                r -= 1;
                lastIsEmpty = false;
            } else if (t != ChessType.EMPTY.ordinal()) {
                isDead = true;
                break;
            } else if (lastIsEmpty) {
                break;
            } else if (emptyCount > 1) {
                break;
            } else {
                lastIsEmpty = true;
                ps.add(new Position(c,r));
                c += 1;
                r -= 1;
                emptyCount += 1;
            }
        }
        switch (count) {
            case 5:
                dangerousPositions.addAll(ps);
                break;
            case 4:
                for(Position p: ps) {
                    int pc = p.getCol();
                    int pr = p.getRow();
                    if (pc-1>=0 && pr+1<15 && boardData.getType(pc-1,pr+1)!=ChessType.EMPTY.ordinal()
                            && pc+1<15 && pr-1>=0 && boardData.getType(pc+1,pr-1)!=ChessType.EMPTY.ordinal()) {
                        dangerousPositions.add(p);
                        return;
                    }
                }
                dangerousPositions.addAll(ps);
                break;
            case 3:
                if (!isDead) {
                    dangerousPositions.addAll(ps);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void testRandomNextPosition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Position originalPosition = boardData.getLastPosition();
                if (originalPosition == null) {
                    setNextPosition(new Position(7,7));
                    return;
                }
                evaluateDangerousPositions(originalPosition);
                int size = dangerousPositions.size();
                Log.d("Dangerous Positions", "count: " + size);
                for (Position p: dangerousPositions) {
                    Log.d("Dangerous Positions", p.toString());
                }
                if (size > 0) {
                    setNextPosition(dangerousPositions.get(random.nextInt(size)));
                } else {
                    Position p = generateNearestPosition();
                    while(!boardData.checkPositionValid(p)) {
                        p = generateNearestPosition();
                    }
                    setNextPosition(p);
                }
            }
        }).start();
    }

    private Position generateNearestPosition() {
        int col = boardData.getLastPosition().getCol() + random.nextInt(3) - 1;
        int row = boardData.getLastPosition().getRow() + random.nextInt(3) - 1;
        return new Position(col, row);
    }

    @Override
    public void procressNextPosition() {

    }
}
