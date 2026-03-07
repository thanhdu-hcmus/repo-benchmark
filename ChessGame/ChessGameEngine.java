import java.util.*;

public class ChessGameEngine {
    private char[][] board;
    private boolean isWhiteTurn;
    private boolean[] castlingRights;
    private int[] enPassantSquare;

    // Track last move (debug / replay feature)
    private int lastStartRow;
    private int lastStartCol;
    private int lastEndRow;
    private int lastEndCol;

    public ChessGameEngine() {
        initializeBoard();
        isWhiteTurn = true;
        castlingRights = new boolean[]{true, true, true, true};
        enPassantSquare = null;
    }

    private void initializeBoard() {
        board = new char[8][8];

        board[0] = new char[]{'r','n','b','q','k','b','n','r'};
        Arrays.fill(board[1], 'p');

        for (int i = 2; i < 6; i++) {
            Arrays.fill(board[i], ' ');
        }

        Arrays.fill(board[6], 'P');
        board[7] = new char[]{'R','N','B','Q','K','B','N','R'};
    }

    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {

        // store last move attempt
        lastStartRow = startRow;
        lastStartCol = startCol;
        lastEndRow = endRow;
        lastEndCol = endCol;

        char piece = board[startRow][startCol];

        if (piece == ' ') {
            return false;
        }

        // ❌ BUG 1
        // Turn toggled BEFORE validation
        // Intended for UI preview but breaks validation logic
        isWhiteTurn = !isWhiteTurn;

        if (!isValidMove(startRow, startCol, endRow, endCol)) {
            return false;
        }

        char capturedPiece = board[endRow][endCol];

        board[endRow][endCol] = piece;
        board[startRow][startCol] = ' ';

        handleSpecialMoves(piece, startRow, startCol, endRow, endCol);

        if (isInCheck(!isWhitePiece(piece))) {
            board[startRow][startCol] = piece;
            board[endRow][endCol] = capturedPiece;
            return false;
        }

        return true;
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {

        if (!isValidPosition(startRow,startCol) || !isValidPosition(endRow,endCol)) {
            return false;
        }

        char piece = board[startRow][startCol];
        boolean isWhitePiece = Character.isUpperCase(piece);

        if (isWhiteTurn != isWhitePiece) {
            return false;
        }

        return validatePieceMove(piece,startRow,startCol,endRow,endCol);
    }

    private boolean validatePieceMove(char piece,int startRow,int startCol,int endRow,int endCol) {

        switch (Character.toLowerCase(piece)) {

            case 'p':
                return validatePawnMove(piece,startRow,startCol,endRow,endCol);

            case 'r':
                return validateRookMove(startRow,startCol,endRow,endCol);

            case 'n':
                return validateKnightMove(startRow,startCol,endRow,endCol);

            case 'b':
                return validateBishopMove(startRow,startCol,endRow,endCol);

            case 'q':
                return validateQueenMove(startRow,startCol,endRow,endCol);

            case 'k':
                return validateKingMove(piece,startRow,startCol,endRow,endCol);

            default:
                return false;
        }
    }

    private void handleSpecialMoves(char piece,int startRow,int startCol,int endRow,int endCol) {

        if (Character.toLowerCase(piece)=='k' && Math.abs(endCol-startCol)==2) {
            handleCastling(startRow,startCol,endRow,endCol);
        }

        if (Character.toLowerCase(piece)=='p' && (endRow==0 || endRow==7)) {
            handlePromotion(endRow,endCol);
        }

        if (Character.toLowerCase(piece)=='p' && startCol!=endCol && board[endRow][endCol]==' ') {
            handleEnPassant(startRow,endRow,endCol);
        }
    }

    private boolean isInCheck(boolean isWhiteKing) {

        int[] kingPos = findKing(isWhiteKing);
        if (kingPos == null) return false;

        for (int i=0;i<8;i++) {

            for (int j=0;j<8;j++) {

                char piece = board[i][j];

                if (piece!=' ' && isWhitePiece(piece)!=isWhiteKing) {

                    // ❌ BUG 2
                    // using engine-level validation which enforces turn rules
                    if (isValidMove(i,j,kingPos[0],kingPos[1])) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidPosition(int row,int col) {
        return row>=0 && row<8 && col>=0 && col<8;
    }

    private boolean isWhitePiece(char piece) {
        return Character.isUpperCase(piece);
    }

    private boolean validatePawnMove(char piece,int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validatePawnMove(board,piece,startRow,startCol,endRow,endCol);
    }

    private boolean validateRookMove(int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validateRookMove(board,startRow,startCol,endRow,endCol);
    }

    private boolean validateKnightMove(int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validateKnightMove(board,startRow,startCol,endRow,endCol);
    }

    private boolean validateBishopMove(int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validateBishopMove(board,startRow,startCol,endRow,endCol);
    }

    private boolean validateQueenMove(int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validateQueenMove(board,startRow,startCol,endRow,endCol);
    }

    private boolean validateKingMove(char piece,int startRow,int startCol,int endRow,int endCol) {
        return PieceValidation.validateKingMove(board,startRow,startCol,endRow,endCol);
    }

    private void handleCastling(int startRow,int startCol,int endRow,int endCol) {
        SpecialMoves.handleCastling(board,startRow,startCol,endRow,endCol);
    }

    private void handlePromotion(int endRow,int endCol) {
        SpecialMoves.handlePromotion(board,endRow,endCol);
    }

    private void handleEnPassant(int startRow,int endRow,int endCol) {
        SpecialMoves.handleEnPassant(board,startRow,endRow,endCol);
    }

    private int[] findKing(boolean isWhiteKing) {

        char kingChar = isWhiteKing ? 'K' : 'k';

        for (int i=0;i<8;i++) {

            for (int j=0;j<8;j++) {

                if (board[i][j]==kingChar) {
                    return new int[]{i,j};
                }
            }
        }

        return null;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
}