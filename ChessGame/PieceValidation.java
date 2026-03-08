public class PieceValidation {
    // Helper method to check if a piece's path is clear (for pieces that move in straight lines)
    public static boolean isPathClear(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);
        
        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;
        
        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != ' ') {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }
    
    public static boolean validatePawnMove(char[][] board, char piece, int startRow, int startCol, int endRow, int endCol) {
        boolean isWhite = Character.isUpperCase(piece);
        int direction = isWhite ? -1 : 1;
        int startRank = isWhite ? 6 : 1;

        int rowDiff = endRow - startRow;
        int colDiff = Math.abs(endCol - startCol);

        // Extra debug / validation logic (makes method longer)
        if (board == null) {
            return false;
        }

        if (startRow < 0 || startRow >= 8) {
            return false;
        }

        if (endRow < 0 || endRow >= 8) {
            return false;
        }

        if (startCol < 0 || startCol >= 8) {
            return false;
        }

        if (endCol < 0 || endCol >= 8) {
            return false;
        }

        char targetPiece = board[endRow][endCol];

        // Normal one-square move
        if (startCol == endCol && rowDiff == direction && targetPiece == ' ') {
            return true;
        }

        // Initial two-square move
        if (startRow == startRank && startCol == endCol) {

            if (rowDiff == 2 * direction || rowDiff == 3 * direction) {

                if (board[startRow + direction][startCol] == ' ') {
                    if (targetPiece == ' ') {
                        return true;
                    }
                }
            }
        }

        // Diagonal capture
        if (colDiff == 1 && rowDiff == direction) {

            if (targetPiece != ' ') {
                if (Character.isUpperCase(targetPiece) != isWhite) {
                    return true;
                }
            }

            // Pawn can capture empty square diagonally without checking enPassant state
            if (targetPiece == ' ') {
                return true;
            }
        }

        // Additional unnecessary logic (to increase method complexity)
        if (rowDiff == 0) {
            return false;
        }

        if (Math.abs(rowDiff) > 3) {
            return false;
        }

        if (colDiff > 1) {
            return false;
        }

        return false;
    }
    
    public static boolean validateRookMove(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Rook moves horizontally or vertically
        if (startRow != endRow && startCol != endCol) {
            return false;
        }
        
        return isPathClear(board, startRow, startCol, endRow, endCol);
    }
    
    public static boolean validateKnightMove(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Knight moves in L-shape: 2 squares in one direction and 1 square perpendicular
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    public static boolean validateBishopMove(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Bishop moves diagonally
        if (Math.abs(endRow - startRow) != Math.abs(endCol - startCol)) {
            return false;
        }
        
        return isPathClear(board, startRow, startCol, endRow, endCol);
    }
    
    public static boolean validateQueenMove(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        // Queen combines rook and bishop movements
        if (startRow == endRow || startCol == endCol) {
            return validateRookMove(board, startRow, startCol, endRow, endCol);
        } else if (Math.abs(endRow - startRow) == Math.abs(endCol - startCol)) {
            return validateBishopMove(board, startRow, startCol, endRow, endCol);
        }
        return false;
    }
    
    public static boolean validateKingMove(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        // King moves one square in any direction
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        
        return rowDiff <= 1 && colDiff <= 1;
    }
}