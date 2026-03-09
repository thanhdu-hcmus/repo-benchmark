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
   
   
## PR07 — Detailed Explanation
Changes

Files affected:

SmartConstract/contracts/SimpleBank.sol
SmartConstract/contracts/BankUtils.sol
SmartConstract/test/simpleBank.test.js

This PR introduces 7 issues related to Solidity coding standards, validation logic, arithmetic safety, and contract architecture.

1. Naming Convention Violation (Easy)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The function name:

function getbalance() public view returns(uint)

does not follow Solidity naming conventions.

Why This Is a Problem

Solidity uses camelCase for functions.

Incorrect:

getbalance()

Correct:

getBalance()

Violating naming conventions causes:

inconsistent code style

reduced readability

harder collaboration in large projects

Correct Fix
function getBalance() public view returns(uint)
2. Missing Event (Easy)
File

SmartConstract/contracts/BankUtils.sol

Problem

The helper function depositBalance() updates balances but does not emit an event.

Example problematic logic:

balances[user] += amount;
Why This Is a Problem

In Solidity, events are important for:

transaction tracking

blockchain transparency

off-chain applications listening for updates

Without events:

front-end applications cannot detect deposits

blockchain activity becomes harder to audit

Correct Fix

Add an event:

event Deposit(address indexed user, uint amount);

Then emit it:

balances[user] += amount;
emit Deposit(user, amount);
3. Missing Client Check (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The enroll() function does not check whether a user is already enrolled.

Example:

function enroll() public {
    enrolled[msg.sender] = true;
}
Why This Is a Problem

Users can call:

enroll()
enroll()
enroll()

multiple times.

This may lead to:

duplicated logic execution

reward logic errors

inconsistent state

Correct Fix

Add a validation check:

require(!enrolled[msg.sender], "Already enrolled");
4. Incorrect Reward Logic (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The reward assignment overwrites previous balances.

Example problematic code:

balances[msg.sender] = reward;
Why This Is a Problem

If a user calls enroll() again:

Old balance = 50
Reward = 10

Expected:

60

Actual result:

10

The balance is overwritten instead of incremented.

Correct Fix

Use addition:

balances[msg.sender] += reward;
5. Improper Error Handling (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The withdraw() function silently fails when the withdrawal amount exceeds the balance.

Example problematic code:

if(amount > balances[msg.sender]) {
    return;
}
Why This Is a Problem

In smart contracts:

silent failures hide problems

users receive no feedback

funds may appear stuck

Correct Fix

Use require() to revert the transaction:

require(amount <= balances[msg.sender], "Insufficient balance");

This ensures:

transaction fails clearly

state remains consistent

6. Integer Overflow Risk (Hard)
File

SmartConstract/contracts/BankUtils.sol

Problem

Arithmetic operations are performed without overflow protection.

Example:

uint newBalance = currentBalance + amount;
Why This Is Dangerous

In older Solidity versions (<0.8):

uint256 max = 2^256 - 1

If overflow occurs:

max + 1 → 0

Example:

currentBalance = 2^256 - 1
amount = 1

Result:

0

This causes unexpected balance resets.

Correct Fix

Use SafeMath (for Solidity <0.8):

using SafeMath for uint256;

uint newBalance = currentBalance.add(amount);

Or rely on built-in overflow checks in Solidity ≥0.8.

7. Poor Contract Modularization (Hard)
File

SmartConstract/contracts/BankUtils.sol

Problem

BankUtils.sol duplicates logic already implemented in SimpleBank.sol.

Example duplication:

balance updates
reward logic
deposit logic
Why This Is a Problem

Duplicated logic causes:

maintenance difficulty

inconsistent behavior

potential security bugs

Example scenario:

SimpleBank fixes reward bug
BankUtils still contains old buggy logic

This results in different behaviors for the same operation.

Correct Fix

Refactor shared logic into a single location.

Possible solutions:

Move shared logic into BankUtils library

Import and reuse functions in SimpleBank

Avoid duplicating state updates across contracts

Example structure:

SimpleBank.sol
   └─ uses BankUtils.sol (library)