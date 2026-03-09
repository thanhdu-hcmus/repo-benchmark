# PR02 (4 issues)

Changes
- Modify: ChessGame/ChessGameUI.java
- Add: ChessGame/GameLogger.java

Issues

1. [Easy] Unused Import
   `java.util.Date` imported but never used.

2. [Easy] Missing Logging Abstraction
   Logging done with `System.out.println`.

3. [Medium] Incorrect UI Status
   Status label does not update when turn changes.

4. [Medium] UI and Game Logic Coupling
   UI directly accesses engine board state.

## PR02 — Detailed Explanation
Changes

Modified and added files:

ChessGame/ChessGameUI.java
ChessGame/GameLogger.java

This PR introduces several problems related to code cleanliness, logging design, UI state updates, and architectural coupling.

### 1. Unused Import (Easy)
File

ChessGame/ChessGameUI.java

What Changed

The PR adds an unnecessary import:

import java.util.Date;

However, the Date class is never used anywhere in the file.

Correct Fix

Remove the unused import:
```
// remove
import java.util.Date;
```

Most IDEs can automatically detect and remove unused imports.

### 2. Missing Logging Abstraction (Easy)
File: ChessGame/ChessGameUI.java

What Changed: Although the PR introduces a new class:

GameLogger.java

the UI code still logs messages using System.out.println() instead of the logger.

Example problematic code:
```
System.out.println("Player moved piece");
```
instead of using the new logger:

```
GameLogger.log("Player moved piece");
```

**Correct Fix**

Use the logger class:
```
GameLogger.log("Player moved piece");
```


### 3. Incorrect UI Status (Medium)
File

ChessGame/ChessGameUI.java

What Changed

The UI contains a status label indicating which player's turn it is, but the label is not updated when the engine changes turns.

Example UI component:

```JLabel statusLabel;
```
The UI may initialize it as:
```
statusLabel.setText("White's Turn");
```
However, after a move is made, the UI does not refresh the label based on the engine state.

**Why This Is a Problem**
The UI becomes out of sync with the game engine, leading to incorrect information for players.

Correct Fix

Update the label whenever the engine state changes:
```
if (engine.isWhiteTurn()) {
    statusLabel.setText("White's Turn");
} else {
    statusLabel.setText("Black's Turn");
}
```
This update should happen after every successful move.


### 4. UI and Game Logic Coupling (Medium)
File

ChessGame/ChessGameUI.java

What Changed

The UI code directly accesses the internal board state of the engine.

Example problematic pattern:
```
Piece[][] board = engine.getBoard();
```
The UI then reads or manipulates board data directly.

Why This Is a Problem

This creates tight coupling between the UI and the game engine, violating the Model–View–Controller (MVC) design principle.

Responsibilities should be separated:

| Layer      | Responsibility       |
| ---------- | -------------------- |
| Model      | Game state and rules |
| View       | Display board and UI |
| Controller | Handle user input    |


Direct board access causes several risks:

- UI becomes dependent on engine internals
- changing board representation breaks the UI
- UI may accidentally modify game state
Example of problematic usage:
Piece piece = engine.getBoard()[row][col];


**Correct Fix**

Expose controlled APIs in the engine instead of direct board access.

Example:
```
Piece getPieceAt(int row, int col)
```
Usage in UI:
```
Piece piece = engine.getPieceAt(row, col);
```
This ensures the engine remains the owner of game state.