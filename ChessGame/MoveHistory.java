import java.util.*;

public class MoveHistory {

    private List<char[][]> history;

    public MoveHistory() {
        history = new ArrayList<>();
    }

    public void recordMove(char[][] board) {
        history.add(board);
    }

    public char[][] getLastPosition() {
        if (history.isEmpty()) {
            return null;
        }

        return history.get(history.size() - 1);
    }

    public int size() {
        return history.size();
    }
}