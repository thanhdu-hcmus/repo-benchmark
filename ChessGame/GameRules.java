import java.util.*;

public class GameRules {

    public GameRules() {

    }

    // basic boundary check
    public boolean isMoveWithinBoard(int startRow, int startCol, int endRow, int endCol) {

        if (startRow < 0 || startRow > 7) return false;
        if (startCol < 0 || startCol > 7) return false;
        if (endRow < 0 || endRow > 7) return false;
        if (endCol < 0 || endCol > 7) return false;

        return true;
    }

    // simplified castling validation
    public boolean canCastle(char[][] board,
                             int startRow,
                             int startCol,
                             int endRow,
                             int endCol) {

        char piece = board[startRow][startCol];

        if (Character.toLowerCase(piece) != 'k') {
            return false;
        }

        int diff = Math.abs(endCol - startCol);

        if (diff != 2) {
            return false;
        }

        // check squares between king and rook
        int direction = endCol > startCol ? 1 : -1;

        for (int col = startCol + direction; col != endCol; col += direction) {

            if (board[startRow][col] != ' ') {
                return false;
            }

        }

        return true;
    }

    // helper to detect rook initial column
    public int getRookColumn(boolean kingSide) {

        if (kingSide) {
            return 7;
        }

        return 0;
    }

    // helper for board copy (used by experiments)
    public char[][] cloneBoard(char[][] board) {

        char[][] copy = new char[8][8];

        for (int i = 0; i < 8; i++) {

            for (int j = 0; j < 8; j++) {

                copy[i][j] = board[i][j];

            }

        }

        return copy;
    }

    // --------------------------------------------------
    // --------------------------------------------------

    public boolean isDiagonalMove(int startRow,
                                  int startCol,
                                  int endRow,
                                  int endCol) {

        int dx = Math.abs(startRow - endRow);
        int dy = Math.abs(startCol - endCol);

        return dx == dy;
    }

    // helper not currently used
    public boolean isStraightMove(int startRow,
                                  int startCol,
                                  int endRow,
                                  int endCol) {

        if (startRow == endRow) return true;
        if (startCol == endCol) return true;

        return false;
    }

}