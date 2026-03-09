# PR05 (2 issues)

Changes
- Modify: ChessGame/PieceValidation.java

Issues

1. [Easy] Magic Number
   Hardcoded value `8` used instead of board size constant.

2. [Medium] Friendly Piece Capture
   Piece validation allows capturing a piece of the same color.


## PR05 — Detailed Explanation
Changes

Modified file:

ChessGame/PieceValidation.java

This PR modifies the piece validation logic and introduces two issues: a code quality problem (magic number) and a game rule violation allowing friendly piece capture.

### 1. Magic Number (Easy)
File

ChessGame/PieceValidation.java

What Changed

The PR introduces the hardcoded value: `8`

when checking board boundaries.

**Correct Fix**

Define a constant for board size:
```
public static final int BOARD_SIZE = 8;
```
Then use it in validations:
```
if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
    return true;
}```

### 2. Friendly Piece Capture (Medium)
File

ChessGame/PieceValidation.java

What Changed

The PR modifies move validation logic but does not check whether the target square contains a piece of the same color.

Example problematic logic:
```
Piece target = board[toRow][toCol];

if (target != null) {
    return true;   // ❌ allows capturing any piece
}
```

This condition allows capturing both enemy and friendly pieces.

**Why This Is a Problem**

In chess, a piece cannot capture another piece of the same color.


Correct Fix

Add a color comparison before allowing the capture.

Example corrected logic:
```
Piece target = board[toRow][toCol];

if (target != null && target.getColor() == piece.getColor()) {
    return false;   // prevent capturing friendly piece
}
```
Or equivalently:
```
if (target == null || target.getColor() != piece.getColor()) {
    return true;
}
```
This ensures that a move is valid only if the target square is empty or contains an opponent piece.