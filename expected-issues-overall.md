Dưới đây là phiên bản benchmark suite cải tiến theo đúng yêu cầu của bạn:
- 15 PR
- mỗi PR có 1–8 lỗi (randomized)
- PR có nhiều lỗi → thay đổi nhiều file / tạo file mới
- lỗi dựa trên repo context
- mô phỏng lỗi junior dev thật
- đa dạng loại lỗi: linter, logic, security, architecture
- Tôi cũng mô tả file thay đổi để sau này bạn dễ tạo patch.

# AI Code Review Benchmark – Expected Issues

Repository: repo-benchmark

Severity Levels
- Easy = Linter / Style
- Medium = Business Logic
- Hard = Architecture / Security

| PR   | Files Changed | Files Added | Total Issues | Easy | Medium | Hard | Notes                               |
| ---- | ------------- | ----------- | ------------ | ---- | ------ | ---- | ----------------------------------- |
| PR01 | 2             | 0           | 3            | 1    | 2      | 0    | Turn logic + duplicated utility     |
| PR02 | 1             | 1           | 4            | 2    | 2      | 0    | UI state + logging practices        |
| PR03 | 1             | 0           | 1            | 1    | 0      | 0    | Small trivial linter issue          |
| PR04 | 2             | 1           | 5            | 1    | 3      | 1    | Pawn rules + state exposure         |
| PR05 | 1             | 0           | 2            | 1    | 1      | 0    | Validation bug                      |
| PR06 | 1             | 1           | 4            | 1    | 2      | 1    | Renderer + MVC violation            |
| PR07 | 2             | 1           | 7            | 2    | 3      | 2    | Solidity contract design + safety   |
| PR08 | 2             | 1           | 6            | 2    | 2      | 2    | Castling rules + rule fragmentation |
| PR09 | 1             | 0           | 2            | 1    | 1      | 0    | En passant state bug                |
| PR10 | 1             | 1           | 3            | 1    | 1      | 1    | AI move generation architecture     |
| PR11 | 2             | 2           | 8            | 3    | 3      | 2    | Smart contract security heavy       |
| PR12 | 1             | 1           | 5            | 1    | 2      | 2    | Withdrawal queue logic              |
| PR13 | 2             | 1           | 7            | 2    | 3      | 2    | Reward logic + contract dependency  |
| PR14 | 2             | 1           | 6            | 2    | 2      | 2    | Board utilities misuse              |
| PR15 | 2             | 2           | 8            | 2    | 3      | 3    | Game state architecture issues      |


## Distribution Summary
| Level     | Count         |
| --------- | ------------- |
| Easy      | 23            |
| Medium    | 30            |
| Hard      | 21            |
| **Total** | **74 issues** |

## Repo context
| PR   | Issue                                | Level  | Vì sao cần repo context                                           |
| ---- | ------------------------------------ | ------ | ----------------------------------------------------------------- |
| PR01 | Incorrect Check Detection            | Medium | Phải hiểu `isValidMove()` có check `turn` trong `PieceValidation` |
| PR01 | Code Duplication (`findKing`)        | Easy   | Phải biết `findKing()` tồn tại ở class khác                       |
| PR02 | UI and Game Logic Coupling           | Medium | Phải biết UI đang gọi trực tiếp engine board                      |
| PR04 | Missing En Passant Validation        | Medium | Phải biết state `enPassantSquare` tồn tại ở engine                |
| PR04 | MoveHistory reference bug            | Medium | Phải biết board là mutable object                                 |
| PR04 | State Mutation Leak (`getBoard()`)   | Hard   | Phải biết engine expose internal board                            |
| PR06 | MVC Violation                        | Hard   | Phải biết UI layer structure của repo                             |
| PR07 | Contract Modularization duplication  | Hard   | Phải đọc `BankUtils` và `SimpleBank`                              |
| PR08 | Rule Engine Fragmentation            | Hard   | Logic spread across Engine + SpecialMoves + GameRules             |
| PR08 | Tight Coupling                       | Hard   | SpecialMoves mutate board state                                   |
| PR09 | En Passant State Bug                 | Medium | Biết `enPassantSquare` phải update sau pawn move                  |
| PR10 | AIPlayer architecture violation      | Hard   | AIPlayer mutate board bypass engine                               |
| PR11 | Contract Architecture Violation      | Hard   | Logic split across contracts without interface                    |
| PR12 | Contract Interaction Risk            | Hard   | Shared mutable state between contracts                            |
| PR13 | Contract Dependency Misconfiguration | Hard   | Migration + contract dependencies                                 |
| PR13 | Upgrade Pattern Misuse               | Hard   | Phải hiểu upgrade pattern                                         |
| PR14 | Utility Class Misuse                 | Hard   | Utility class mutate board                                        |
| PR14 | Encapsulation Violation              | Hard   | Board exposed outside engine                                      |
| PR15 | Rule Engine Duplication              | Hard   | MoveValidator duplicates PieceValidation                          |
| PR15 | Tight Coupling Between Components    | Hard   | GameStateManager + Engine dependency                              |
| PR15 | State Management Fragmentation       | Hard   | State split across Engine + GameStateManager                      |


---

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

| # | Issue                     | Difficulty | File                                                            | Vị trí / Nguyên nhân                                                                                                                                   |
| - | ------------------------- | ---------- | --------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1 | Incorrect Turn Handling   | Medium     | `ChessGame/ChessGameEngine.java`                                | Logic chuyển lượt (`currentPlayer` toggle) xảy ra trước khi quá trình validation nước đi hoàn tất, dẫn đến trạng thái lượt bị sai nếu move bị từ chối. |
| 2 | Incorrect Check Detection | Medium     | `ChessGame/ChessGameEngine.java`                                | Hàm `isInCheck()` gọi `isValidMove()` để kiểm tra threat, nhưng `isValidMove()` lại kiểm tra turn → làm sai logic phát hiện check.                     |
| 3 | Code Duplication          | Easy       | `ChessGame/ChessGameEngine.java`, `ChessGame/SpecialMoves.java` | Logic tìm vị trí vua (`findKing()`) được viết lặp lại ở cả hai class thay vì dùng một utility chung.                                                   |

---

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

| # | Issue                       | Difficulty | File                         | Vị trí / Nguyên nhân                                                                                                          |
| - | --------------------------- | ---------- | ---------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| 1 | Unused Import               | Easy       | `ChessGame/ChessGameUI.java` | `import java.util.Date;` được khai báo nhưng không được sử dụng.                                                              |
| 2 | Missing Logging Abstraction | Easy       | `ChessGame/ChessGameUI.java` | Logging được thực hiện trực tiếp bằng `System.out.println` thay vì thông qua lớp `GameLogger`.                                |
| 3 | Incorrect UI Status         | Medium     | `ChessGame/ChessGameUI.java` | Label trạng thái lượt chơi không được cập nhật khi engine chuyển turn.                                                        |
| 4 | UI and Game Logic Coupling  | Medium     | `ChessGame/ChessGameUI.java` | UI truy cập trực tiếp trạng thái bàn cờ của engine (`engine.getBoard()` hoặc tương tự) thay vì thông qua API hoặc controller. |

---

# PR03 (1 issue)

Changes
- Modify: ChessGame/ChessGameUI.java

Issues

1. [Easy] Unused Variable
   A variable `selectedPiece` is declared in `handleSquareClick()` but never used.

| # | Issue           | Difficulty | File                         | Vị trí / Nguyên nhân                                                                                            |
| - | --------------- | ---------- | ---------------------------- | --------------------------------------------------------------------------------------------------------------- |
| 1 | Unused Variable | Easy       | `ChessGame/ChessGameUI.java` | Biến `selectedPiece` được khai báo trong `handleSquareClick()` nhưng không được sử dụng trong bất kỳ logic nào. |

---

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

| # | Issue                         | Difficulty | File                             | Vị trí / Nguyên nhân                                                                                               |
| - | ----------------------------- | ---------- | -------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| 1 | Long Method                   | Easy       | `ChessGame/PieceValidation.java` | Hàm `validatePawnMove()` trở nên quá dài và chứa nhiều nhánh logic phức tạp.                                       |
| 2 | Off-by-one Error              | Medium     | `ChessGame/PieceValidation.java` | Logic pawn double move kiểm tra sai khoảng cách hoặc vị trí hàng bắt đầu → cho phép di chuyển không hợp lệ.        |
| 3 | Missing En Passant Validation | Medium     | `ChessGame/PieceValidation.java` | Logic capture của pawn không xét trạng thái en passant.                                                            |
| 4 | Move History Bug              | Medium     | `ChessGame/MoveHistory.java`     | `MoveHistory` lưu reference tới board hiện tại thay vì snapshot của move → lịch sử bị thay đổi khi board thay đổi. |
| 5 | State Mutation Leak           | Hard       | `ChessGame/ChessGameEngine.java` | Method `getBoard()` trả về trực tiếp reference của board nội bộ → code bên ngoài có thể thay đổi state engine.     |

---

# PR05 (2 issues)

Changes
- Modify: ChessGame/PieceValidation.java

Issues

1. [Easy] Magic Number
   Hardcoded value `8` used instead of board size constant.

2. [Medium] Friendly Piece Capture
   Piece validation allows capturing a piece of the same color.

| # | Issue                  | Difficulty | File                             | Vị trí / Nguyên nhân                                                                              |
| - | ---------------------- | ---------- | -------------------------------- | ------------------------------------------------------------------------------------------------- |
| 1 | Magic Number           | Easy       | `ChessGame/PieceValidation.java` | Giá trị `8` được hardcode khi kiểm tra giới hạn bàn cờ thay vì sử dụng constant như `BOARD_SIZE`. |
| 2 | Friendly Piece Capture | Medium     | `ChessGame/PieceValidation.java` | Logic validate move không kiểm tra trường hợp piece capture quân cùng màu.                        |

---
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

| Issue                    | File                   | Lý do                                    |
| ------------------------ | ---------------------- | ---------------------------------------- |
| Magic Number (font size) | **ChessGameUI.java**   | UI hardcode font size                    |
| UI Rendering Bug         | **BoardRenderer.java** | renderer xử lý sai empty square          |
| Null Handling Missing    | **BoardRenderer.java** | renderer không check `board == null`     |
| MVC Violation            | **BoardRenderer.java** | renderer giữ reference `ChessGameEngine` |

| # | Issue                 | Difficulty | File                                                         | Vị trí / Nguyên nhân                                                                                                                   |
| - | --------------------- | ---------- | ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Magic Number          | Easy       | `ChessGame/BoardRenderer.java`                               | Font size được hardcode (`new Font("Arial", Font.BOLD, 42)`) thay vì dùng constant hoặc config.                                        |
| 2 | UI Rendering Bug      | Medium     | `ChessGame/BoardRenderer.java`                               | Renderer cố render piece trên ô trống và không kiểm tra square empty → gây lỗi hiển thị.                                               |
| 3 | Null Handling Missing | Medium     | `ChessGame/BoardRenderer.java`                               | Renderer không kiểm tra `board == null` hoặc `board[row][col] == null` trước khi truy cập piece.                                       |
| 4 | MVC Violation         | Hard       | `ChessGame/ChessGameUI.java`, `ChessGame/BoardRenderer.java` | Logic rendering truy cập trực tiếp state game hoặc board model thay vì thông qua controller, vi phạm separation giữa UI và game logic. |


---

# PR07 (7 issues)

Changes
- Modify: SmartConstract/contracts/SimpleBank.sol
- Modify: SmartConstract/test/simpleBank.test.js
- Add: SmartConstract/contracts/BankUtils.sol

Issues

1. [Easy] Naming Convention
   Function name `getbalance()` violates Solidity naming conventions.

2. [Easy] Missing Event
   Deposit does not emit event in new helper function.

3. [Medium] Missing Client Check
   Users can call `enroll()` multiple times.

4. [Medium] Incorrect Reward Logic
   Reward overwritten when enrolling again.

5. [Medium] Improper Error Handling
   Withdrawal silently fails instead of reverting.

6. [Hard] Integer Overflow Risk
   Balance arithmetic without SafeMath.

7. [Hard] Poor Contract Modularization
   BankUtils duplicates logic from SimpleBank.

| # | Issue                            | Difficulty | File                                      | Vị trí / Nguyên nhân                                                    |
| - | -------------------------------- | ---------- | ----------------------------------------- | ----------------------------------------------------------------------- |
| 1 | Naming Convention (`getbalance`) | Easy       | `SmartConstract/contracts/SimpleBank.sol` | Hàm `getbalance()` không theo camelCase chuẩn Solidity (`getBalance`)   |
| 2 | Missing Event                    | Easy       | `SmartConstract/contracts/BankUtils.sol`  | Hàm `depositBalance()` cập nhật balance nhưng không emit event          |
| 3 | Missing Client Check             | Medium     | `SmartConstract/contracts/SimpleBank.sol` | `enroll()` không kiểm tra user đã enroll chưa                           |
| 4 | Incorrect Reward Logic           | Medium     | `SmartConstract/contracts/SimpleBank.sol` | `balances[msg.sender] = reward` overwrite reward nếu gọi `enroll()` lại |
| 5 | Improper Error Handling          | Medium     | `SmartConstract/contracts/SimpleBank.sol` | `withdraw()` silently ignore nếu withdraw > balance                     |
| 6 | Integer Overflow Risk            | Hard       | `SmartConstract/contracts/BankUtils.sol`  | `currentBalance + amount` không dùng SafeMath                           |
| 7 | Poor Contract Modularization     | Hard       | `SmartConstract/contracts/BankUtils.sol`  | Logic reward bị duplicate với `SimpleBank`                              |


---

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

| # | Issue                          | Difficulty | File                                                                                        | Vị trí / Nguyên nhân                                                                                                 |
| - | ------------------------------ | ---------- | ------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------- |
| 1 | Magic Number                   | Easy       | `ChessGame/ChessGameEngine.java`                                                            | Các cột castling (`0`, `7`, `2`, `6`) được hardcode thay vì dùng constant hoặc enum.                                 |
| 2 | Dead Code                      | Easy       | `ChessGame/GameRules.java`                                                                  | Một helper method trong `GameRules` được định nghĩa nhưng không được gọi ở bất kỳ nơi nào trong codebase.            |
| 3 | Incorrect Castling Rule        | Medium     | `ChessGame/SpecialMoves.java`                                                               | Logic kiểm tra castling không kiểm tra trường hợp vua đi qua ô đang bị attack (king passes through check).           |
| 4 | Missing Castling Rights Update | Medium     | `ChessGame/ChessGameEngine.java`                                                            | Khi rook di chuyển khỏi vị trí ban đầu, quyền castling không bị revoke.                                              |
| 5 | Rule Engine Fragmentation      | Hard       | `ChessGame/ChessGameEngine.java`, `ChessGame/SpecialMoves.java`, `ChessGame/GameRules.java` | Logic luật cờ bị phân tán giữa nhiều class khác nhau mà không có rule manager trung tâm.                             |
| 6 | Tight Coupling                 | Hard       | `ChessGame/SpecialMoves.java`                                                               | `SpecialMoves` trực tiếp thay đổi trạng thái board (ví dụ di chuyển rook khi castling) thay vì thông qua engine API. |


| # | Issue                           | Difficulty | File                                        |
| - | ------------------------------- | ---------- | ------------------------------------------- |
| 1 | Magic Number (castling columns) | Easy       | `ChessGameEngine.java`, `SpecialMoves.java` |
| 2 | Dead Code                       | Easy       | `GameRules.java`                            |
| 3 | Incorrect Castling Rule         | Medium     | `GameRules.java`                            |
| 4 | Missing Castling Rights Update  | Medium     | `ChessGameEngine.java`                      |
| 5 | Rule Engine Fragmentation       | Hard       | Engine + SpecialMoves + GameRules           |
| 6 | Tight Coupling                  | Hard       | `SpecialMoves.java`                         |

---


# PR09 (2 issues)

Changes
- Modify: ChessGame/ChessGameEngine.java

Issues

1. [Medium] En Passant State Bug
   `enPassantSquare` declared but never updated.

2. [Easy] Dead Field
   Variable exists but is never used.

| # | Issue                | Difficulty | File                             | Vị trí / Nguyên nhân                                                                                                              |
| - | -------------------- | ---------- | -------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| 1 | En Passant State Bug | Medium     | `ChessGame/ChessGameEngine.java` | Biến `enPassantSquare` được khai báo để lưu trạng thái ô en passant nhưng không bao giờ được cập nhật sau khi pawn di chuyển 2 ô. |
| 2 | Dead Field           | Easy       | `ChessGame/ChessGameEngine.java` | Một biến trạng thái (ví dụ `lastMovePawn` hoặc tương tự) được khai báo trong engine nhưng không được sử dụng ở bất kỳ logic nào.  |


| # | Issue                | Difficulty | File                 | Explanation                                                                        |
| - | -------------------- | ---------- | -------------------- | ---------------------------------------------------------------------------------- |
| 1 | En Passant State Bug | Medium     | ChessGameEngine.java | `enPassantSquare` tồn tại nhưng **không bao giờ được update khi pawn double move** |
| 2 | Dead Field           | Easy       | ChessGameEngine.java | `debugMode` tồn tại nhưng **không bao giờ được bật hoặc dùng thực tế**             |


---

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

| # | Issue                   | Difficulty | File                                                        | Vị trí / Nguyên nhân                                                                                                                                                                                 |
| - | ----------------------- | ---------- | ----------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1 | Missing JavaDoc         | Easy       | `ChessGame/AIPlayer.java`                                   | Public class `AIPlayer` được khai báo nhưng không có JavaDoc mô tả mục đích và cách hoạt động của AI player.                                                                                         |
| 2 | Illegal Move Generation | Medium     | `ChessGame/AIPlayer.java`                                   | Hàm sinh nước đi của AI tạo các move trực tiếp từ board mà không kiểm tra hợp lệ bằng engine (`isLegalMove` hoặc validation tương đương). Điều này có thể sinh ra nước đi vi phạm luật cờ.           |
| 3 | Architecture Violation  | Hard       | `ChessGame/AIPlayer.java`, `ChessGame/ChessGameEngine.java` | `AIPlayer` truy cập và thay đổi trực tiếp trạng thái bàn cờ (`board`, `pieces`, hoặc state tương tự) thay vì gọi API của `ChessGameEngine`, phá vỡ kiến trúc tách biệt giữa AI layer và game engine. |

| # | Issue                   | Difficulty | File          | Explanation                               |
| - | ----------------------- | ---------- | ------------- | ----------------------------------------- |
| 1 | Missing JavaDoc         | Easy       | AIPlayer.java | Public class không có documentation       |
| 2 | Illegal Move Generation | Medium     | AIPlayer.java | Move generation không gọi `isValidMove()` |
| 3 | Architecture Violation  | Hard       | AIPlayer.java | AI sửa trực tiếp `board[][]`              |

---

# PR011 (8 issues)

Changes
- Modify: SmartConstract/contracts/SimpleBank.sol
- Modify: migrations/2_deploy_contracts.js
- Add: SmartConstract/contracts/BankAdmin.sol
- Add: SmartConstract/contracts/BankEvents.sol

Issues

1. [Easy] Magic Number
   Hardcoded funding value in migration script.

2. [Easy] Unused Variable
   `adminCount` declared but never used.

3. [Easy] Redundant Require
   Duplicate validation check.

4. [Medium] Incorrect Withdraw Validation
   Allows zero-value withdrawals.

5. [Medium] Access Control Missing
   Admin functions callable by any user.

6. [Medium] Missing Event Emission
   Withdraw function does not emit event.

7. [Hard] Reentrancy Vulnerability
   `transfer()` executed before balance safety patterns.

8. [Hard] Contract Architecture Violation
   Logic spread across multiple contracts without proper interface.

| # | Issue                           | Difficulty | File                                                                                 | Vị trí / Nguyên nhân                                                                                            |
| - | ------------------------------- | ---------- | ------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------- |
| 1 | Magic Number                    | Easy       | `migrations/2_deploy_contracts.js`                                                   | Giá trị `30000000000000000000` được hardcode khi deploy contract thay vì dùng `30 * ether`.                     |
| 2 | Unused Variable                 | Easy       | `SmartConstract/contracts/BankAdmin.sol`                                             | Biến `adminCount` được khai báo nhưng không bao giờ được sử dụng trong contract.                                |
| 3 | Redundant Require               | Easy       | `SmartConstract/contracts/SimpleBank.sol`                                            | Constructor có `require(msg.value > 0)` mặc dù đã có `require(msg.value == 30 ether)`.                          |
| 4 | Incorrect Withdraw Validation   | Medium     | `SmartConstract/contracts/SimpleBank.sol`                                            | Hàm `withdraw()` cho phép `withdrawAmount == 0` vì chỉ kiểm tra `<= balances[msg.sender]`.                      |
| 5 | Access Control Missing          | Medium     | `SmartConstract/contracts/BankAdmin.sol`                                             | Các hàm `addAdmin`, `removeAdmin`, `clearAdmins` không có kiểm tra quyền (`owner` hoặc `onlyAdmin`).            |
| 6 | Missing Event Emission          | Medium     | `SmartConstract/contracts/SimpleBank.sol`                                            | Hàm `withdraw()` không emit event sau khi rút tiền.                                                             |
| 7 | Reentrancy Vulnerability        | Hard       | `SmartConstract/contracts/SimpleBank.sol`                                            | `msg.sender.transfer()` được gọi trước khi cập nhật `balances[msg.sender]`.                                     |
| 8 | Contract Architecture Violation | Hard       | `SmartConstract/contracts/SimpleBank.sol`, `SmartConstract/contracts/BankEvents.sol` | Logic event logging bị tách sang contract khác (`BankEvents`) nhưng không có interface hoặc abstraction hợp lý. |

---

# PR12 (5 issues)

Changes
- Modify: SmartConstract/contracts/SimpleBank.sol
- Add: SmartConstract/contracts/WithdrawQueue.sol

Issues

1. [Easy] Unused Import.

2. [Medium] Withdrawal Logic Bug
   Queue logic allows double withdrawals.

3. [Medium] Incorrect Balance Calculation.

4. [Hard] Reentrancy Risk Introduced
   External call made before state update.

5. [Hard] Contract Interaction Risk
   WithdrawQueue and SimpleBank share mutable state incorrectly.

| # | Issue                         | Difficulty | File                                         | Vị trí / Nguyên nhân                                                                  |
| - | ----------------------------- | ---------- | -------------------------------------------- | ------------------------------------------------------------------------------------- |
| 1 | Unused Import                 | Easy       | `SmartConstract/contracts/SimpleBank.sol`    | `WithdrawQueue.sol` được import hai lần nhưng chỉ cần một lần.                        |
| 2 | Withdrawal Logic Bug          | Medium     | `SmartConstract/contracts/WithdrawQueue.sol` | `processNext()` không kiểm tra `processed`, queue có thể bị xử lý nhiều lần.          |
| 3 | Incorrect Balance Calculation | Medium     | `SmartConstract/contracts/SimpleBank.sol`    | `deposit()` cộng thêm `+1` vào balance.                                               |
| 4 | Reentrancy Risk Introduced    | Hard       | `SmartConstract/contracts/SimpleBank.sol`    | `withdrawQueue.processNext()` được gọi trước khi cập nhật `balances`.                 |
| 5 | Contract Interaction Risk     | Hard       | `SimpleBank.sol`, `WithdrawQueue.sol`        | Queue contract và bank cùng thay đổi state mà không có interface hoặc access control. |


---

# PR13 (7 issues)

Changes
- Modify: SmartConstract/contracts/SimpleBank.sol
- Modify: SmartConstract/migrations scripts
- Add: SmartConstract/contracts/RewardManager.sol

Issues

1. [Easy] Magic Number.

2. [Easy] Dead Code.

3. [Medium] Reward Logic Bug
   More than 3 clients receive rewards.

4. [Medium] Missing Validation.

5. [Medium] Event Logging Missing.

6. [Hard] Contract Dependency Misconfiguration.

7. [Hard] Upgrade Pattern Misuse.

---

# PR14 (6 issues)

Changes
- Modify: ChessGame/PieceValidation.java
- Add: ChessGame/BoardUtils.java
- Modify: ChessGameEngine.java

Issues

1. [Easy] Code Style Violation.

2. [Easy] Duplicate Utility Method.

3. [Medium] Path Checking Bug
   isPathClear skips intermediate square.

4. [Medium] Incorrect Bishop Movement Validation.

5. [Hard] Utility Class Misuse
   BoardUtils modifies board state.

6. [Hard] Encapsulation Violation
   Engine exposes board to utilities.

---

# PR15 (8 issues)

Changes
- Modify: ChessGameEngine.java
- Modify: SpecialMoves.java
- Add: ChessGame/MoveValidator.java
- Add: ChessGame/GameStateManager.java

Issues

1. [Easy] Duplicate Constant.

2. [Easy] Unused Field.

3. [Medium] Illegal Move Validation
   MoveValidator bypasses PieceValidation.

4. [Medium] Inconsistent Game State Updates.

5. [Medium] Incorrect Checkmate Detection.

6. [Hard] Rule Engine Duplication.

7. [Hard] Tight Coupling Between Components.

8. [Hard] State Management Fragmentation.