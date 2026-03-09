# PR01 (3 issues)

Changes

- Modify: ChessGame/ChessGameEngine.java
- Modify: ChessGame/SpecialMoves.java

Issues

1. [Medium] Incorrect Turn Handling
   Turn toggling happens before validation completes.
2. [Medium] Incorrect Check Detection
   `isInCheck()` uses `isValidMove()` which enforces turn validation.
3. [Easy] Code Duplication
   `findKing()` logic duplicated between classes.

## PR01 — Detailed Explanation

Changes

Modified files:
ChessGame/ChessGameEngine.java
ChessGame/SpecialMoves.java

This PR intentionally introduces several issues related to turn handling, check detection, and code duplication in the chess engine.

### 1. Incorrect Turn Handling (Medium)
   File: ChessGame/ChessGameEngine.java

What Changed
The PR moves the turn toggle logic before move validation.

Example of the introduced bug:

```
public boolean makeMove(Move move) {

isWhiteTurn = !isWhiteTurn;   // ❌ turn toggled before validation

if (!isValidMove(move)) {
return false;
}

applyMove(move);
return true;
}
```

### 2. Incorrect Check Detection (Medium)
   File: ChessGame/ChessGameEngine.java

What Changed
The PR modifies isInCheck() to call:

`isValidMove()`

instead of calling the piece-specific move validation (piece.isValidMove()).

Example of introduced change:
`if (isValidMove(piece, kingPosition)) { return true; }`

instead of:
`if (piece.isValidMove(kingPosition)) { return true;`

Previously, the engine validated the move before switching turns.

Correct Fix

Use piece movement logic independent of turn, such as:

`piece.isValidMove(kingPosition)`

or introduce a dedicated method:

`canAttack()`

### 3. Code Duplication (Easy)
   Files

```
ChessGame/ChessGameEngine.java 
ChessGame/SpecialMoves.java
```

What Changed

The PR copies the findKing() logic into SpecialMoves, creating duplicate code.

Example duplicated method:
```
public Position findKing(Color color) {

    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {

            Piece p = board[r][c];

            if (p != null && p.isKing() && p.color == color) {
                return new Position(r, c);
            }
        }
    }

    return null;
}
```
Now the same logic exists in both:
ChessGameEngine
SpecialMoves