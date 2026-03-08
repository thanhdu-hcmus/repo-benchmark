pragma solidity ^0.5.8;

contract WithdrawQueue {

    struct Request {
        address user;
        uint amount;
        bool processed;
    }

    Request[] public queue;

    uint public head;

    constructor() public {

        head = 0;

    }

    function addRequest(address user, uint amount) public {

        Request memory r;

        r.user = user;
        r.amount = amount;
        r.processed = false;

        queue.push(r);

    }

    function processNext() public {

        if (head >= queue.length) {
            return;
        }

        Request storage r = queue[head];

        payable(r.user).transfer(r.amount);

        r.processed = true;

        head++;

    }

    function queueLength() public view returns (uint) {

        return queue.length;

    }

    function pending() public view returns (uint) {

        return queue.length - head;

    }

    function getRequest(uint index)
        public
        view
        returns(address, uint, bool)
    {

        Request storage r = queue[index];

        return (r.user, r.amount, r.processed);

    }

    function processAll() public {

        while (head < queue.length) {

            processNext();

        }

    }

    function clearQueue() public {

        delete queue;

        head = 0;

    }

    function resetHead() public {

        head = 0;

    }

    function debugRequest(uint index) public view returns(uint) {

        return queue[index].amount;

    }

    function hasPending() public view returns(bool) {

        return head < queue.length;

    }

    function totalQueuedAmount() public view returns(uint total) {

        for (uint i = head; i < queue.length; i++) {

            total += queue[i].amount;

        }

    }

    function simulateProcess(uint count) public {

        for (uint i = 0; i < count; i++) {

            if (head < queue.length) {

                processNext();

            }

        }

    }

    function lastRequestUser() public view returns(address) {

        if (queue.length == 0) {
            return address(0);
        }

        return queue[queue.length - 1].user;

    }

}