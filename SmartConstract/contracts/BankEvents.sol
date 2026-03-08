pragma solidity ^0.5.8;

contract BankEvents {

    event DepositRecorded(address indexed user, uint amount);

    event WithdrawRecorded(address indexed user, uint amount);

    constructor() public {

    }

    function logDeposit(address user, uint amount) public {

        emit DepositRecorded(user, amount);

    }

    function logWithdraw(address user, uint amount) public {

        emit WithdrawRecorded(user, amount);

    }

    function batchDeposit(address[] memory users,
                          uint[] memory amounts) public {

        for (uint i = 0; i < users.length; i++) {

            emit DepositRecorded(users[i], amounts[i]);

        }

    }

    function batchWithdraw(address[] memory users,
                           uint[] memory amounts) public {

        for (uint i = 0; i < users.length; i++) {

            emit WithdrawRecorded(users[i], amounts[i]);

        }

    }

    function emitTestEvent(address user) public {

        emit DepositRecorded(user, 1);

    }

}