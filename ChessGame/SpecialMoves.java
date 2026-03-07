public class SpecialMoves {

    public static void handleCastling(char[][] board,int startRow,int startCol,int endRow,int endCol){

        boolean isKingSide = endCol > startCol;

        int rookStartCol = isKingSide ? 7 : 0;
        int rookEndCol = isKingSide ? endCol-1 : endCol+1;

        char rook = board[startRow][rookStartCol];

        board[startRow][rookStartCol] = ' ';
        board[startRow][rookEndCol] = rook;
    }

    public static void handlePromotion(char[][] board,int endRow,int endCol){

        boolean isWhite = board[endRow][endCol]=='P';
        board[endRow][endCol] = isWhite ? 'Q' : 'q';
    }

    public static void handleEnPassant(char[][] board,int startRow,int endRow,int endCol){

        board[startRow][endCol] = ' ';
    }

    public static boolean isCheckmate(char[][] board,boolean isWhiteKing){

        if (!isInCheck(board,isWhiteKing)) {
            return false;
        }

        for (int startRow=0;startRow<8;startRow++){

            for (int startCol=0;startCol<8;startCol++){

                char piece = board[startRow][startCol];

                if (piece==' ' || Character.isUpperCase(piece)!=isWhiteKing){
                    continue;
                }

                for (int endRow=0;endRow<8;endRow++){

                    for (int endCol=0;endCol<8;endCol++){

                        if (!isValidMove(board,startRow,startCol,endRow,endCol)){
                            continue;
                        }

                        char originalEndPiece = board[endRow][endCol];

                        board[endRow][endCol] = piece;
                        board[startRow][startCol] = ' ';

                        boolean stillInCheck = isInCheck(board,isWhiteKing);

                        board[startRow][startCol] = piece;
                        board[endRow][endCol] = originalEndPiece;

                        if (!stillInCheck){
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean isInCheck(char[][] board,boolean isWhiteKing){

        int[] kingPos = findKing(board,isWhiteKing);

        if (kingPos == null) return false;

        for (int row=0;row<8;row++){

            for (int col=0;col<8;col++){

                char piece = board[row][col];

                if (piece!=' ' && Character.isUpperCase(piece)!=isWhiteKing){

                    if (isValidMove(board,row,col,kingPos[0],kingPos[1])){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isValidMove(char[][] board,int startRow,int startCol,int endRow,int endCol){

        char piece = board[startRow][startCol];

        switch (Character.toLowerCase(piece)){

            case 'p':
                return PieceValidation.validatePawnMove(board,piece,startRow,startCol,endRow,endCol);

            case 'r':
                return PieceValidation.validateRookMove(board,startRow,startCol,endRow,endCol);

            case 'n':
                return PieceValidation.validateKnightMove(board,startRow,startCol,endRow,endCol);

            case 'b':
                return PieceValidation.validateBishopMove(board,startRow,startCol,endRow,endCol);

            case 'q':
                return PieceValidation.validateQueenMove(board,startRow,startCol,endRow,endCol);

            case 'k':
                return PieceValidation.validateKingMove(board,startRow,startCol,endRow,endCol);

            default:
                return false;
        }
    }

    // ❌ BUG 3: duplicated logic from ChessGameEngine
    private static int[] findKing(char[][] board,boolean isWhiteKing){

        char kingChar = isWhiteKing ? 'K' : 'k';

        for (int i=0;i<8;i++){

            for (int j=0;j<8;j++){

                if (board[i][j]==kingChar){
                    return new int[]{i,j};
                }
            }
        }

        return null;
    }
}