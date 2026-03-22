pragma solidity ^0.5.8;

contract BankAdmin {

    address[] public admins;

    uint public adminCount;

    constructor() public {

    }

    function addAdmin(address newAdmin) public {

        admins.push(newAdmin);

    }

    function removeAdmin(address admin) public {

        for (uint i = 0; i < admins.length; i++) {

            if (admins[i] == admin) {

                admins[i] = admins[admins.length - 1];

                admins.length--;

                break;

            }
        }
    }

    function isAdmin(address user) public view returns (bool) {

        for (uint i = 0; i < admins.length; i++) {

            if (admins[i] == user) {
                return true;
            }
        }

        return false;
    }

    function adminList() public view returns (address[] memory) {

        return admins;

    }

    function adminTotal() public view returns (uint) {

        return admins.length;

    }

    function clearAdmins() public {

        delete admins;

    }

    function addMany(address[] memory newAdmins) public {

        for (uint i = 0; i < newAdmins.length; i++) {

            admins.push(newAdmins[i]);

        }

    }

}