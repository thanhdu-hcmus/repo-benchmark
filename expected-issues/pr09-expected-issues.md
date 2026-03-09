# PR09 (2 issues)

Changes
- Modify: ChessGame/ChessGameEngine.java

Issues

1. [Medium] En Passant State Bug
   `enPassantSquare` declared but never updated.

2. [Easy] Dead Field
   Variable exists but is never used.
   
## PR09 — Detailed Explanation
Changes

Modified file:

ChessGame/ChessGameEngine.java

This PR introduces issues related to incomplete En Passant implementation and unused state variables.

1. En Passant State Bug (Medium)
File

ChessGame/ChessGameEngine.java

Problem

The variable:

Square enPassantSquare;

is declared to support En Passant, but the engine never updates its value during gameplay.

Example declaration:

private Square enPassantSquare;

However, after a pawn moves two squares, the engine does not set this variable.

Why This Is a Problem

In chess, En Passant requires tracking the square where a pawn can be captured after a two-square advance.

Example scenario:

White pawn: e2 → e4
Black pawn: d4

The square e3 becomes the enPassantSquare.

Black should be able to capture:

d4 → e3

But because enPassantSquare is never updated, the engine cannot correctly validate the move.

This leads to:

En Passant moves never being allowed

inconsistent rule implementation

broken pawn mechanics

Correct Fix

Update enPassantSquare when a pawn moves two squares.

Example fix:

if (piece instanceof Pawn && Math.abs(fromRow - toRow) == 2) {
    enPassantSquare = new Square((fromRow + toRow) / 2, fromCol);
}

Also reset it when a normal move occurs:

enPassantSquare = null;

This ensures the En Passant opportunity exists only for one turn, as required by chess rules.

2. Dead Field (Easy)
File

ChessGame/ChessGameEngine.java

Problem

The variable enPassantSquare exists but is never used in any move validation logic.

Example:

private Square enPassantSquare;

But no code references:

enPassantSquare

during pawn capture validation.

Why This Is a Problem

A dead field increases code complexity without providing functionality.

Problems caused:

misleading developers (suggests En Passant is implemented)

wasted memory/state tracking

harder debugging

Example confusion:

Developer assumes En Passant works
But engine never reads enPassantSquare
Correct Fix

Two possible fixes:

Option 1 — Implement En Passant properly

Use enPassantSquare inside pawn capture validation:

if (targetSquare.equals(enPassantSquare)) {
    performEnPassantCapture();
}

Option 2 — Remove the field

If the feature is not implemented yet:

// remove unused field
private Square enPassantSquare;