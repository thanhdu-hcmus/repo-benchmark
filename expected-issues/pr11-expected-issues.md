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

## PR011 — Detailed Explanation
Changes

Files modified or added:

SmartConstract/contracts/SimpleBank.sol
SmartConstract/contracts/BankAdmin.sol
SmartConstract/contracts/BankEvents.sol
migrations/2_deploy_contracts.js

This PR introduces administration and event contracts, but the implementation introduces 8 issues related to code quality, security vulnerabilities, and architecture.

### 1. Magic Number (Easy)
File

migrations/2_deploy_contracts.js

Problem

The deployment script uses a hardcoded funding value:

value: 30000000000000000000
Why This Is a Problem

This value represents:

30 ether

but writing it as a raw number makes the code:

harder to read

error-prone

difficult to maintain

Example confusion:

Is this 3 ETH? 30 ETH? 0.3 ETH?
Correct Fix

Use Web3 utilities:

value: web3.utils.toWei("30", "ether")

or

value: 30 * 10**18

This improves clarity and maintainability.

2. Unused Variable (Easy)
File

SmartConstract/contracts/BankAdmin.sol

Problem

The contract declares a variable:

uint public adminCount;

but never updates or uses it.

Why This Is a Problem

Unused variables:

increase contract complexity

waste storage

mislead developers about intended functionality

Developers may assume adminCount tracks administrators, but it does nothing.

Correct Fix

Either remove it:

// delete adminCount

or implement proper usage:

adminCount++;
adminCount--;

when admins are added or removed.

3. Redundant Require (Easy)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The constructor contains duplicate validation:

require(msg.value > 0);
require(msg.value == 30 ether);
Why This Is a Problem

The second condition already guarantees the first.

If:

msg.value == 30 ether

then automatically:

msg.value > 0

Thus the first require is unnecessary.

Correct Fix

Keep only the strict validation:

require(msg.value == 30 ether, "Initial funding must be 30 ETH");
4. Incorrect Withdraw Validation (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

Withdraw validation only checks:

require(withdrawAmount <= balances[msg.sender]);

This allows:

withdrawAmount = 0
Why This Is a Problem

Zero withdrawals:

waste gas

create unnecessary transactions

may cause unexpected contract behavior

Correct Fix

Add a lower bound check:

require(withdrawAmount > 0, "Withdraw amount must be greater than zero");
require(withdrawAmount <= balances[msg.sender], "Insufficient balance");
5. Access Control Missing (Medium)
File

SmartConstract/contracts/BankAdmin.sol

Problem

Administrative functions can be called by any user.

Example:

function addAdmin(address newAdmin) public

No access modifier is used.

Why This Is Dangerous

Any user could:

add themselves as admin
remove other admins
clear admin list

This completely breaks the security model of the contract.

Correct Fix

Use an access modifier.

Example:

modifier onlyOwner() {
    require(msg.sender == owner, "Not authorized");
    _;
}

Then protect functions:

function addAdmin(address newAdmin) public onlyOwner
6. Missing Event Emission (Medium)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The withdraw() function updates balances but does not emit an event.

Example:

balances[msg.sender] -= withdrawAmount;

No event follows.

Why This Is a Problem

Events are important for:

transaction transparency

blockchain monitoring

frontend updates

Without an event, external systems cannot track withdrawals.

Correct Fix

Define an event:

event Withdraw(address indexed user, uint amount);

Emit it:

emit Withdraw(msg.sender, withdrawAmount);
7. Reentrancy Vulnerability (Hard)
File

SmartConstract/contracts/SimpleBank.sol

Problem

The contract transfers funds before updating the user's balance.

Example vulnerable code:

msg.sender.transfer(withdrawAmount);
balances[msg.sender] -= withdrawAmount;
Why This Is Dangerous

An attacker contract could call withdraw() repeatedly before the balance updates.

Example attack flow:

1. Attacker calls withdraw()
2. Contract sends ETH
3. Attacker fallback function calls withdraw() again
4. Balance not updated yet
5. Multiple withdrawals occur

This results in fund draining.

Correct Fix

Follow the Checks-Effects-Interactions pattern.

Correct order:

require(withdrawAmount <= balances[msg.sender]);

balances[msg.sender] -= withdrawAmount;

msg.sender.transfer(withdrawAmount);

Or use ReentrancyGuard.

8. Contract Architecture Violation (Hard)
Files
SmartConstract/contracts/SimpleBank.sol
SmartConstract/contracts/BankEvents.sol
Problem

Event logic is moved into a separate contract (BankEvents) but there is no interface or abstraction connecting it to SimpleBank.

Example issue:

SimpleBank manages logic
BankEvents defines events
But they are loosely connected
Why This Is a Problem

This creates fragmented architecture.

Problems include:

unclear contract responsibilities

duplicated logic risk

difficult maintenance

Example confusion:

Should events be emitted by SimpleBank or BankEvents?
Correct Architecture

Use a clear interface or inheritance.

Example:

contract BankEvents {
    event Deposit(address indexed user, uint amount);
    event Withdraw(address indexed user, uint amount);
}

contract SimpleBank is BankEvents {
    ...
}

This ensures:

Event definitions centralized
Logic remains inside main contract
