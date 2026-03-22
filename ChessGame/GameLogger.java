public class GameLogger {

    public static void log(String message) {
        System.out.println("[Game] " + message);
    }

    public static void logMove(int startRow, int startCol, int endRow, int endCol) {
        log("Move from (" + startRow + "," + startCol + 
            ") to (" + endRow + "," + endCol + ")");
    }

}