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

## PR12 — Detailed Explanation
Changes

Files affected:

SmartConstract/contracts/SimpleBank.sol
SmartConstract/contracts/WithdrawQueue.sol

This PR introduces a withdrawal queue mechanism intended to process withdrawals asynchronously. However, the implementation introduces five issues, including logic errors, security risks, and contract interaction problems.

1. Unused Import (Easy)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The contract imports WithdrawQueue.sol twice, but only one import is needed.

Example:

import "./WithdrawQueue.sol";
import "./WithdrawQueue.sol";
Why This Is a Problem

Duplicate imports:

clutter the codebase

reduce readability

may confuse developers about dependencies

Although Solidity typically ignores duplicate imports during compilation, keeping them is poor code hygiene.

Correct Fix

Remove the duplicate import:

import "./WithdrawQueue.sol";
2. Withdrawal Logic Bug (Medium)
File

SmartConstract/contracts/WithdrawQueue.sol

Problem

The queue processing function does not check whether a request has already been processed.

Example problematic logic:

function processNext() public {
    Withdrawal memory request = queue[nextIndex];

    // process withdrawal
}

There is no verification of processed status.

Why This Is a Problem

A withdrawal request may be processed multiple times.

Example scenario:

Queue:
[0] Alice withdraw 10 ETH

Execution flow:

processNext() → Alice receives 10 ETH
processNext() again → Alice receives 10 ETH again

This results in double withdrawals.

Correct Fix

Add a processed flag:

struct Withdrawal {
    address user;
    uint amount;
    bool processed;
}

Then check before processing:

require(!request.processed, "Already processed");

And update it:

request.processed = true;
3. Incorrect Balance Calculation (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The deposit() function incorrectly increments balances by adding an extra value.

Example problematic code:

balances[msg.sender] += msg.value + 1;
Why This Is a Problem

This introduces incorrect accounting.

Example:

User deposits: 5 ETH

Expected balance:

5 ETH

Actual result:

5 ETH + 1

This breaks the financial integrity of the contract.

Potential consequences:

inconsistent balances

incorrect withdrawals

accounting mismatches

Correct Fix

Update balance correctly:

balances[msg.sender] += msg.value;
4. Reentrancy Risk Introduced (Hard)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The contract calls the queue processor before updating balances.

Example vulnerable pattern:

withdrawQueue.processNext();
balances[msg.sender] -= amount;
Why This Is Dangerous

If processNext() triggers an external call, an attacker could exploit reentrancy.

Example attack flow:

1. User initiates withdraw
2. processNext() sends ETH
3. Attacker fallback executes
4. withdraw() called again
5. Balance not yet updated

Result:

Multiple withdrawals executed
Correct Fix

Follow the Checks → Effects → Interactions pattern.

Correct order:

require(balances[msg.sender] >= amount);

balances[msg.sender] -= amount;

withdrawQueue.processNext();

Or protect with ReentrancyGuard.

5. Contract Interaction Risk (Hard)
Files
SmartConstract/contracts/SimpleBank.sol
SmartConstract/contracts/WithdrawQueue.sol
Problem

Both contracts mutate shared financial state without proper coordination.

Example:

SimpleBank → updates balances
WithdrawQueue → processes withdrawals

But there is:

no interface abstraction

no strict access control

no single source of truth

Why This Is Dangerous

Multiple contracts manipulating shared state can cause:

inconsistent balances

unauthorized withdrawals

logic conflicts

Example scenario:

SimpleBank updates balance
WithdrawQueue processes withdrawal simultaneously
State becomes inconsistent

This violates safe contract interaction principles.

Correct Architecture

Define a clear interaction interface.

Example:

interface IBank {
    function decreaseBalance(address user, uint amount) external;
}

Then allow only authorized calls:

modifier onlyBank {
    require(msg.sender == bankAddress, "Not authorized");
    _;
}

Architecture becomes:

SimpleBank
   ↓ manages balances
WithdrawQueue
   ↓ executes withdrawal requests

This ensures controlled contract communication.