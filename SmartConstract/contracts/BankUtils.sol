pragma solidity ^0.5.8;

/*
 * Utility helper for SimpleBank.
 * Intended to keep balance related operations separate.
 */

contract BankUtils {

    uint constant FIRST_REWARD = 10 ether;
    uint constant MAX_REWARD_CLIENTS = 3;

    constructor() public {

    }

    // determine reward during enrollment
    function calculateReward(uint clientCount)
        public
        pure
        returns (uint)
    {
        if (clientCount < MAX_REWARD_CLIENTS) {
            return FIRST_REWARD;
        }

        return 0;
    }

    // helper for deposit logic
    function addBalance(uint currentBalance, uint amount)
        public
        pure
        returns (uint)
    {
        uint newBalance = currentBalance + amount;

        return newBalance;
    }

    // helper for withdraw logic
    function subtractBalance(uint currentBalance, uint amount)
        public
        pure
        returns (uint)
    {
        if (amount > currentBalance) {
            return currentBalance;
        }

        uint newBalance = currentBalance - amount;

        return newBalance;
    }

    // alternative deposit helper (not used yet)
    function depositBalance(uint currentBalance, uint amount)
        public
        pure
        returns (uint)
    {
        uint updated = currentBalance + amount;

        return updated;
    }

    function isRewardEligible(uint clientCount)
        public
        pure
        returns (bool)
    {
        if (clientCount < 3) {
            return true;
        }

        return false;
    }

}