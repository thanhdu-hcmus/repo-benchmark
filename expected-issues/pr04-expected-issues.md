# PR04 (5 issues)

Changes
- Modify: ChessGame/PieceValidation.java
- Modify: ChessGame/ChessGameEngine.java
- Add: ChessGame/MoveHistory.java

Issues

1. [Easy] Long Method
   `validatePawnMove()` expanded beyond recommended complexity.

2. [Medium] Off-by-one Error
   Pawn double move allows illegal forward movement.

3. [Medium] Missing En Passant Validation
   Pawn capture logic ignores en passant state.

4. [Medium] Move History Bug
   MoveHistory stores reference to board instead of move snapshot.

5. [Hard] State Mutation Leak
   Engine exposes internal board reference through `getBoard()`.
   
   
## PR04 — Detailed Explanation
Changes

Modified and added files:
```
ChessGame/PieceValidation.java
ChessGame/ChessGameEngine.java
ChessGame/MoveHistory.java
```
This PR modifies pawn validation logic, introduces a new MoveHistory class, and exposes engine board state in a way that creates multiple correctness and architectural issues.

### 1. Long Method (Easy)
File

ChessGame/PieceValidation.java

What Changed

The method: `validatePawnMove()` was expanded to include multiple responsibilities such as:

- normal pawn movement
- double pawn moves
- captures
- special rules
- additional validation checks

Example simplified structure:
```
public boolean validatePawnMove(...) {

    if (forwardMove) {
        // single move logic
    }

    if (doubleMove) {
        // double move logic
    }

    if (captureMove) {
        // capture logic
    }

    if (specialCondition) {
        // additional rule checks
    }

    // more nested logic...
}
```
**Why This Is a Problem**

The method becomes:
- too long
- hard to read
- hard to maintain

Large methods increase the risk of:

- hidden bugs
- duplicated logic
- difficulty writing unit tests

**Correct Fix**

Break the logic into smaller methods:

Example refactoring:
```
validatePawnMove(...)
validatePawnForwardMove(...)
validatePawnDoubleMove(...)
validatePawnCapture(...)
validateEnPassant(...)
```


### 2. Off-by-one Error (Medium)
File

ChessGame/PieceValidation.java

What Changed

The logic for pawn double moves contains an off-by-one error when checking row movement.

Example buggy logic:
```
if (Math.abs(toRow - fromRow) <= 2) {
    return true;
}
```
This condition allows moves that are not valid pawn moves.

**Why This Is a Problem**

A pawn should only move two squares forward from its starting position.

Correct rule:
```
White pawn: row 6 → row 4
Black pawn: row 1 → row 3
```

The buggy condition may allow:
```
3-square moves
backward moves
illegal forward movement
```


### 3. Missing En Passant Validation (Medium)
File

ChessGame/PieceValidation.java

What Changed

The pawn capture logic only checks standard diagonal captures, but does not validate en passant captures.

Example capture logic:

if (Math.abs(toCol - fromCol) == 1 && toRow == fromRow + direction) {

    if (board[toRow][toCol] != null) {
        return true;
    }
}

3. Missing En Passant Validation (Medium)
File

ChessGame/PieceValidation.java

What Changed

The pawn capture logic only checks standard diagonal captures, but does not validate en passant captures.

Example capture logic:
```
if (Math.abs(toCol - fromCol) == 1 && toRow == fromRow + direction) {

    if (board[toRow][toCol] != null) {
        return true;
    }
}
```

**Correct Fix**
Add validation for the en passant state stored by the engine.

Example:
```
if (isEnPassantSquare(toRow, toCol)) {
    return true;
}
```

### 4. Move History Bug (Medium)
File

ChessGame/MoveHistory.java

What Changed

The new MoveHistory class stores a reference to the board instead of a snapshot of the board state.

Example buggy implementation:
```
public class MoveHistory {

    private Piece[][] board;

    public MoveHistory(Piece[][] board) {
        this.board = board;   // ❌ storing reference
    }

}
```

**Correct Fix**

Store a deep copy of the board.

Example:
```
this.board = copyBoard(board);
```
Example implementation:
```
private Piece[][] copyBoard(Piece[][] original) {

    Piece[][] copy = new Piece[8][8];

    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            copy[r][c] = original[r][c];
        }
    }

    return copy;
}
```

### 5. State Mutation Leak (Hard)
File

ChessGame/ChessGameEngine.java

What Changed

The engine exposes its internal board state through:
```
public Piece[][] getBoard() {
    return board;
}
```

**Why This Is a Problem**

This method returns the actual board object, not a safe copy.

External code (such as the UI) can modify it:
```
Piece[][] board = engine.getBoard();

board[0][0] = null;   // external mutation
```
Now the internal state of the engine has been changed without validation.

**Correct Fix**

Return a copy of the board instead of the original.

Example:
```
public Piece[][] getBoard() {
    return copyBoard(board);
}
```
Or expose safe accessor methods:
```
public Piece getPieceAt(int row, int col)
```
This ensures the engine remains the sole owner of the game state.