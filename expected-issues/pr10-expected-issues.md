# PR10 (3 issues)

Changes
- Add: ChessGame/AIPlayer.java
- Modify: ChessGame/ChessGameEngine.java

Issues

1. [Easy] Missing JavaDoc
   Public class lacks documentation.

2. [Medium] Illegal Move Generation
   AIPlayer generates moves without validation.

3. [Hard] Architecture Violation
   AIPlayer directly manipulates board state.

## PR10 — Detailed Explanation
Changes

Files affected:

ChessGame/AIPlayer.java
ChessGame/ChessGameEngine.java

This PR introduces an AI player module, but the implementation contains issues related to documentation, move validation, and system architecture.

1. Missing JavaDoc (Easy)
File

ChessGame/AIPlayer.java

Problem

The public class AIPlayer is declared without JavaDoc documentation.

Example:

public class AIPlayer {
    ...
}
Why This Is a Problem

Public classes should include JavaDoc explaining:

the purpose of the class

how it interacts with the system

key methods and responsibilities

Without documentation, developers may not understand:

Does AIPlayer generate moves?
Does it evaluate positions?
Does it control the engine?

This becomes more problematic when AI logic becomes complex.

Correct Fix

Add JavaDoc describing the class.

Example:

/**
 * AIPlayer is responsible for generating moves for the computer opponent.
 * It evaluates possible board states and selects a move to execute
 * through the ChessGameEngine.
 */
public class AIPlayer {
}

This improves code readability and maintainability.

2. Illegal Move Generation (Medium)
File

ChessGame/AIPlayer.java

Problem

The AI generates moves directly from the board state without validating them through the engine.

Example problematic logic:

for (Piece piece : pieces) {
    for (Move move : generateMoves(piece)) {
        possibleMoves.add(move);
    }
}

The generated moves are not validated using engine rules.

Missing validation such as:

engine.isLegalMove(move)
Why This Is a Problem

Chess move generation must ensure moves obey all rules:

piece movement rules

check conditions

castling rules

en passant rules

pinned pieces

Without validation, the AI might generate illegal moves such as:

Moving into check
Capturing its own piece
Illegal castling
Invalid pawn moves

This leads to inconsistent gameplay and rule violations.

Correct Fix

Validate generated moves using the engine.

Example:

for (Move move : candidateMoves) {
    if (engine.isLegalMove(move)) {
        legalMoves.add(move);
    }
}

This ensures the AI only considers legal moves.

3. Architecture Violation (Hard)
Files
ChessGame/AIPlayer.java
ChessGame/ChessGameEngine.java
Problem

AIPlayer directly modifies the board state instead of using the engine API.

Example problematic code:

board[toRow][toCol] = piece;
board[fromRow][fromCol] = null;

This bypasses the game engine's control layer.

Why This Is Dangerous

The engine is responsible for:

validating moves

updating turn state

handling special rules

tracking game status

If AI modifies the board directly:

AI → board
Engine unaware of changes

This may cause:

incorrect game state

broken turn management

invalid rule enforcement

Example issue:

AI moves piece
Engine still believes piece at original position
Correct Architecture

The AI should request moves through the engine, not modify state directly.

Correct flow:

AIPlayer
   ↓ choose move
ChessGameEngine.makeMove(move)
   ↓ validate + update
Board State

Example fix:

Move bestMove = selectMove();
engine.makeMove(bestMove);

This ensures:

consistent rule enforcement

centralized state management

clean separation between AI logic and game logic