package com.example.zzang.gobang.model;



import java.util.Observable;

import static com.example.zzang.gobang.model.ChessType.*;

public class Board extends Observable {

    //col , row
    private int[][] boardData;

    private Position lastPosition;

    public Board() {
        reset();
    }

    public boolean addChess(Position position, ChessType chessType) {
        int col = position.getCol();
        int row = position.getRow();
        if(checkPositionValid(col, row)) {
            boardData[col][row] = chessType.ordinal();
            lastPosition = position;
            return true;
        } else {
            return false;
        }
    }

    private boolean checkPositionValid(int col, int row) {
        if(col < 0 || col > 14 || row < 0 || row > 14) {
            return false;
        }
        if(boardData[col][row] != EMPTY.ordinal()) {
            return false;
        }
        return true;
    }

    public boolean checkPositionValid(Position position) {
        return checkPositionValid(position.getCol(),position.getRow());
    }


    public void reset() {
        boardData = new int[15][15];
        lastPosition = null;
    }




    public void checkWin() {
        int col = lastPosition.getCol();
        int row = lastPosition.getRow();
        int chessType = boardData[col][row];
        if(checkColumn(chessType, col, row)
                ||checkRow(chessType, col, row)
                ||checkLeftTop(chessType, col, row)
                ||checkLeftBottom(chessType, col, row)) {
            setChanged();
            notifyObservers(chessType);
        }
        setChanged();
        notifyObservers(ChessType.EMPTY.ordinal());
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

    public Position getLastPosition() {
        return lastPosition;
    }

    public int getType(int col, int row) {
        return boardData[col][row];
    }

    public int getType(Position position) {
        return boardData[position.getCol()][position.getRow()];
    }
}
