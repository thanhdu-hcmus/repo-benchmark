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

## # PR12 (5 issues)

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
