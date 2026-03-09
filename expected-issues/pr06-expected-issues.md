# PR6 (4 issues)

Changes
- Modify: ChessGame/ChessGameUI.java
- Add: ChessGame/BoardRenderer.java

Issues

1. [Easy] Magic Number
   Font size hardcoded.

2. [Medium] UI Rendering Bug
   Renderer fails when board contains empty squares.

3. [Medium] Null Handling Missing
   Renderer does not check null board state.

4. [Hard] MVC Violation
   Rendering logic mixed with game state.

## PR06 — Detailed Explanation
Changes

Modified and added files:

ChessGame/ChessGameUI.java
ChessGame/BoardRenderer.java

This PR introduces a new rendering component (BoardRenderer) but also introduces several issues related to UI implementation and architectural design.

1. Magic Number (Easy)
File

ChessGame/ChessGameUI.java

What Changed

The UI code sets the font size using a hardcoded value:

button.setFont(new Font("Arial", Font.BOLD, 42));

The value 42 is directly embedded in the code.

Why This Is a Problem

Using a hardcoded value like 42 is considered a magic number, because its meaning is not clear and it cannot be easily reused or modified.

Problems caused by magic numbers:

unclear purpose of the value

difficult to maintain

requires editing multiple places if UI design changes

For example, if the board UI changes size, the font may need adjustment. With a magic number, the developer must manually locate and update every occurrence.

Correct Fix

Define a constant for the font size:

private static final int PIECE_FONT_SIZE = 42;

Then use the constant:

button.setFont(new Font("Arial", Font.BOLD, PIECE_FONT_SIZE));

This improves readability and maintainability.

2. UI Rendering Bug (Medium)
File

ChessGame/BoardRenderer.java

What Changed

The renderer assumes that every square on the board contains a piece.

Example problematic code:

Piece piece = board[row][col];
button.setText(piece.getSymbol());
Why This Is a Problem

Many squares on a chess board are empty.

When board[row][col] is empty:

piece = null

Calling:

piece.getSymbol()

causes a NullPointerException or incorrect rendering.

Example Bug Scenario

Board state:

[ rook ][ null ][ bishop ]

Rendering logic attempts:

null.getSymbol()

which causes the UI to crash.

Correct Fix

Check if the square is empty before rendering the piece.

Example fix:

Piece piece = board[row][col];

if (piece != null) {
    button.setText(piece.getSymbol());
} else {
    button.setText("");
}

This ensures empty squares are handled correctly.

3. Null Handling Missing (Medium)
File

ChessGame/BoardRenderer.java

What Changed

The renderer assumes that the board object is always initialized.

Example problematic code:

Piece[][] board = engine.getBoard();

for (int row = 0; row < 8; row++) {
    for (int col = 0; col < 8; col++) {
        Piece piece = board[row][col];
    }
}
Why This Is a Problem

If the board has not been initialized yet, the following may occur:

board == null

In that case, accessing:

board[row][col]

will cause a NullPointerException.

This situation may happen:

during game initialization

when resetting the game

if the engine fails to initialize the board

Correct Fix

Add a null check before rendering:

Piece[][] board = engine.getBoard();

if (board == null) {
    return;
}

Or provide a fallback:

if (board == null) {
    clearBoardUI();
    return;
}

This prevents UI crashes when the board state is unavailable.

4. MVC Violation (Hard)
File

ChessGame/BoardRenderer.java

What Changed

The renderer stores a direct reference to the game engine:

private ChessGameEngine engine;

and accesses the engine’s internal board state:

Piece[][] board = engine.getBoard();
Why This Is a Problem

This creates tight coupling between the View and the Model, violating the Model–View–Controller (MVC) architecture.

In MVC design:

Component	Responsibility
Model	Game state and rules
View	Display board and pieces
Controller	Handle player input

The renderer (View) should only render data passed to it, not directly interact with the engine.

Problems caused by this design:

UI becomes dependent on engine implementation

changes in engine structure break rendering code

UI can accidentally manipulate game state

Example of problematic interaction:

Piece[][] board = engine.getBoard();

The renderer now relies on the internal representation of the engine.

Correct Fix

The renderer should receive board data as input, rather than accessing the engine directly.

Example improved design:

public void renderBoard(Piece[][] board) {
    // render board state
}

The UI or controller would call:

renderer.renderBoard(engine.getBoard());

This ensures that:

Renderer → View only
Engine → Model
UI Controller → connects them

and maintains proper architectural separation.