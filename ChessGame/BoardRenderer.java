import javax.swing.*;
import java.awt.*;

public class BoardRenderer {

    private ChessGameEngine engine;

    public BoardRenderer(ChessGameEngine engine) {
        this.engine = engine;
    }

    public void render(char[][] board, JButton[][] squares) {

        int size = board.length;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                char piece = board[row][col];

                JButton button = squares[row][col];

                renderSquare(button, piece);

                // Debug rendering using engine state
                if (engine.isWhiteTurn()) {
                    button.setForeground(Color.BLACK);
                } else {
                    button.setForeground(Color.BLUE);
                }
            }
        }
    }

    private void renderSquare(JButton button, char piece) {

        String text = String.valueOf(piece).toUpperCase();

        button.setText(text);

        button.setFont(new Font("Arial", Font.BOLD, 40));

        if (piece == ' ') {
            button.setText("");
        }

        updateSquareStyle(button, piece);
    }

    private void updateSquareStyle(JButton button, char piece) {

        if (piece == ' ') {
            button.setForeground(Color.GRAY);
        } else if (Character.isUpperCase(piece)) {
            button.setForeground(Color.BLACK);
        } else {
            button.setForeground(Color.RED);
        }
    }

    // Extra rendering helpers (to make file longer / realistic)

    public void highlightSquare(JButton button) {
        button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
    }

    public void resetSquare(JButton button) {
        button.setBorder(UIManager.getBorder("Button.border"));
    }

    public void refreshAll(JButton[][] squares) {

        char[][] board = engine.getBoard();

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {

                JButton button = squares[r][c];
                char piece = board[r][c];

                renderSquare(button, piece);
            }
        }
    }

    public void debugBoard() {

        char[][] board = engine.getBoard();

        for (int r = 0; r < board.length; r++) {

            StringBuilder row = new StringBuilder();

            for (int c = 0; c < board[r].length; c++) {
                row.append(board[r][c]).append(" ");
            }

            System.out.println(row);
        }
    }
}