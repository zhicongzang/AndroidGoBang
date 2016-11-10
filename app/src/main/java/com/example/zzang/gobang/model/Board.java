package com.example.zzang.gobang.model;

import java.util.LinkedList;

public class Board {

    //col , row
    private int[][] boardData;
    //add to first
    private LinkedList<Position> whiteChessPositions;
    private LinkedList<Position> blackChessPositions;
    private ChessType chessType;
    public Board() {
        reset();
    }

    public boolean addChess(Position position) {
        int col = position.getCol();
        int row = position.getRow();
        if(checkPositionValid(col, row)) {
            boardData[col][row] = chessType.ordinal();
            switch (chessType) {
                case EMPTY:
                    break;
                case WHITE:
                    whiteChessPositions.addFirst(position);
                    break;
                case BLACK:
                    blackChessPositions.addFirst(position);
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPositionValid(int col, int row) {
        if(col < 0 || col > 14 || row < 0 || row > 14) {
            return false;
        }
        if(boardData[col][row] != ChessType.EMPTY.ordinal()) {
            return false;
        }
        return true;
    }

    public LinkedList<Position> getWhiteChessPositions() {
        return whiteChessPositions;
    }

    public LinkedList<Position> getBlackChessPositions() {
        return blackChessPositions;
    }

    public void reset() {
        boardData = new int[15][15];
        whiteChessPositions = new LinkedList<Position>();
        blackChessPositions = new LinkedList<Position>();
        chessType = ChessType.BLACK;
    }

    private void changeType() {
        switch (chessType) {
            case WHITE:
                chessType = ChessType.BLACK;
                break;
            case BLACK:
                chessType = ChessType.WHITE;
                break;
            default:
                break;
        }
    }

    public ChessType isWin() {
        int col = 0, row = 0;
        switch (chessType) {
            case WHITE:
                col = whiteChessPositions.getFirst().getCol();
                row = whiteChessPositions.getFirst().getRow();
                break;
            case BLACK:
                col = blackChessPositions.getFirst().getCol();
                row = blackChessPositions.getFirst().getRow();
                break;
            default:
                break;
        }
        if(checkColumn(chessType.ordinal(), col, row)
                ||checkRow(chessType.ordinal(), col, row)
                ||checkLeftTop(chessType.ordinal(), col, row)
                ||checkLeftBottom(chessType.ordinal(), col, row)) {
            return chessType;
        }
        changeType();
        return ChessType.EMPTY;
    }

    private boolean checkColumn(int type, int col, int row) {
        int r = row - 1;
        int count = 1;
        while(r>=0) {
            if (boardData[col][r] == type) {
                count += 1;
                r -= 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        r = row + 1;
        while(r<15) {
            if (boardData[col][r] == type) {
                count += 1;
                r += 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        return false;
    }

    private boolean checkRow(int type, int col, int row) {
        int c = col - 1;
        int count = 1;
        while(c>=0) {
            if (boardData[c][row] == type) {
                count += 1;
                c -= 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        c = col + 1;
        while(c<15) {
            if (boardData[c][row] == type) {
                count += 1;
                c += 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        return false;
    }

    private boolean checkLeftTop(int type, int col, int row) {
        int c = col - 1;
        int r = row - 1;
        int count = 1;
        while(c>=0 && r>=0) {
            if (boardData[c][r] == type) {
                count += 1;
                c -= 1;
                r -= 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        c = col + 1;
        r = row + 1;
        while(c<15 && r<15) {
            if (boardData[c][r] == type) {
                count += 1;
                c += 1;
                r +=1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        return false;
    }

    private boolean checkLeftBottom(int type, int col, int row) {
        int c = col - 1;
        int r = row + 1;
        int count = 1;
        while(c>=0 && r<15) {
            if (boardData[c][r] == type) {
                count += 1;
                c -= 1;
                r += 1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        c = col + 1;
        r = row - 1;
        while(c<15 && r>=0) {
            if (boardData[c][r] == type) {
                count += 1;
                c += 1;
                r -=1;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }
        return false;
    }

    public ChessType getChessType() {
        return chessType;
    }

}
