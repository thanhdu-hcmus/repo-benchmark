# PR08 (6 issues)

Changes
- Modify: ChessGame/ChessGameEngine.java
- Modify: ChessGame/SpecialMoves.java
- Add: ChessGame/GameRules.java

Issues

1. [Easy] Magic Number
   Hardcoded castling column positions.

2. [Easy] Dead Code
   Unused helper method in GameRules.

3. [Medium] Incorrect Castling Rule
   Castling allowed even if king passes through check.

4. [Medium] Missing Castling Rights Update
   Castling rights not revoked when rook moves.

5. [Hard] Rule Engine Fragmentation
   Rules spread across Engine, SpecialMoves, and GameRules.

6. [Hard] Tight Coupling
   SpecialMoves directly mutates board.
   
## PR08 — Detailed Explanation
Changes

Files modified or added:

ChessGame/ChessGameEngine.java
ChessGame/SpecialMoves.java
ChessGame/GameRules.java

This PR attempts to refactor rule logic (especially castling) by introducing a GameRules class. However, it introduces six issues related to maintainability, rule correctness, and architecture.

1. Magic Number (Easy)
File

ChessGame/ChessGameEngine.java

Problem

The implementation hardcodes castling column positions:

if (kingCol == 4 && targetCol == 6) { // king-side castling

or

rookCol == 7
rookCol == 0
targetCol == 2
Why This Is a Problem

These numbers represent board positions, but their meaning is unclear without context.

Example:

4 → king starting column
6 → king-side castling target
2 → queen-side castling target
0 / 7 → rook starting columns

Hardcoding them causes:

poor readability

fragile code if board representation changes

Correct Fix

Define constants:

public static final int KING_START_COL = 4;
public static final int ROOK_KINGSIDE_COL = 7;
public static final int ROOK_QUEENSIDE_COL = 0;
public static final int KING_CASTLE_KINGSIDE = 6;
public static final int KING_CASTLE_QUEENSIDE = 2;

Then use them:

if (kingCol == KING_START_COL && targetCol == KING_CASTLE_KINGSIDE)
2. Dead Code (Easy)
File

ChessGame/GameRules.java

Problem

A helper method is defined but never used anywhere in the codebase.

Example:

public boolean isSquareSafe(int row, int col) {
    ...
}

but no class calls it.

Why This Is a Problem

Dead code causes:

unnecessary code complexity

confusion for future developers

maintenance overhead

Developers may incorrectly assume the method is part of active logic.

Correct Fix

Either:

Remove the method if unused:

// delete unused helper

or

Integrate it properly into rule validation logic.

3. Incorrect Castling Rule (Medium)
File

ChessGame/SpecialMoves.java

Problem

The castling validation does not check if the king passes through a square that is under attack.

Example simplified logic:

if (!kingMoved && !rookMoved && pathClear) {
    return true;
}
Why This Is Wrong

According to chess rules:

A king cannot castle if:

The king is in check

The king passes through a square under attack

The king ends on a square under attack

Example illegal scenario:

White King: e1
White Rook: h1
Black Bishop attacking f1

Castling path:

King: e1 → f1 → g1

Since f1 is attacked, castling must be illegal.

However, the current code allows it.

Correct Fix

Add intermediate square checks:

if (isSquareUnderAttack(row, 5) || isSquareUnderAttack(row, 6)) {
    return false;
}
4. Missing Castling Rights Update (Medium)
File

ChessGame/ChessGameEngine.java

Problem

When a rook moves from its starting position, the castling right is not revoked.

Example scenario:

White rook moves from h1 → h3

Later:

King attempts to castle king-side

The engine still allows castling even though the rook already moved.

Why This Is Incorrect

In chess rules:

If rook moves → castling right permanently lost

Even if the rook returns to its original square.

Correct Fix

Track rook movement:

boolean whiteKingSideRookMoved;
boolean whiteQueenSideRookMoved;

Update when rook moves:

if (rookCol == 7 && rookRow == 0) {
    whiteKingSideRookMoved = true;
}
5. Rule Engine Fragmentation (Hard)
Files
ChessGameEngine.java
SpecialMoves.java
GameRules.java
Problem

Game rule logic is scattered across multiple classes without a central rule manager.

Example rule distribution:

Rule Type	Class
basic move validation	ChessGameEngine
special moves	SpecialMoves
rule helpers	GameRules
Why This Is a Problem

Fragmentation causes:

inconsistent rule enforcement

difficult debugging

unclear architecture

Example issue:

Castling validation in SpecialMoves
Castling rights tracked in Engine
Board safety logic in GameRules

Developers must check 3 files to understand one rule.

Correct Architecture

Use a central rule system:

ChessGameEngine
    ↓
GameRules
    ↓
SpecialMoves

Or even better:

GameRuleEngine
   ├── MoveValidator
   ├── CastlingRule
   ├── CheckRule

This improves separation of concerns.

6. Tight Coupling (Hard)
File

ChessGame/SpecialMoves.java

Problem

SpecialMoves directly modifies the board state.

Example:

board[row][rookTargetCol] = rook;
board[row][rookStartCol] = null;
Why This Is Dangerous

This creates tight coupling between rule logic and board state management.

Problems caused:

bypasses engine validation

harder to track state changes

increases bug risk

Example bug scenario:

SpecialMoves moves rook
Engine still thinks rook at original position
Correct Fix

All board updates should go through the engine API.

Example:

engine.movePiece(rookStartRow, rookStartCol, rookTargetRow, rookTargetCol);

Architecture becomes:

SpecialMoves
   ↓ request
ChessGameEngine
   ↓ updates
Board State

This ensures single source of truth for game state.