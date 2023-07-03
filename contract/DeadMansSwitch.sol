// SPDX-License-Identifier: MIT
pragma solidity >=0.8.0;

contract DeadMansSwitch {
    uint256 private constant ONE_HOUR_SECONDS = 3600;
    uint256 private constant ONE_DAY_SECONDS = 86400;
    uint256 private constant NINETY_DAYS_SECONDS = 7776000;
    uint256 private constant HALF_YEAR_SECONDS = 15552000;
    uint256 private constant ONE_YEAR_SECONDS = 31536000;
    uint16 private constant ONE_HOUR = 0;
    uint16 private constant ONE_DAY = 1;
    uint16 private constant NINETY_DAYS = 90;
    uint16 private constant HALF_YEAR = 182;
    uint16 private constant ONE_YEAR = 365;
    address private owner;
    uint256 private contractCreationDate;
    uint256 private numDaysToReset;
    uint256 private dateExpiration;
    address[] private recipients;
    mapping(address => uint256) private recipientBalances;
    mapping(address => string) private recipientComments;

    constructor(
        uint256 _numDaysToReset,
        address[] memory _recipients,
        uint256[] memory _amountPerRecipient,
        string[] memory _comments
    ) payable {
        require(
            _recipients.length == _amountPerRecipient.length,
            "Number of recipients and amountPerRecipient must match"
        );
        require(
            _recipients.length == _comments.length,
            "Number of recipients and comments must match"
        );

        owner = msg.sender;
        recipients = _recipients;
        uint256 _currentTimestamp = block.timestamp;

        contractCreationDate = _currentTimestamp;
        numDaysToReset = _numDaysToReset;

        if (numDaysToReset == ONE_HOUR) {
            dateExpiration = _currentTimestamp + ONE_HOUR_SECONDS;
        } else if (numDaysToReset == ONE_DAY) {
            dateExpiration = _currentTimestamp + ONE_DAY_SECONDS;
        } else if (numDaysToReset == NINETY_DAYS) {
            dateExpiration = _currentTimestamp + NINETY_DAYS_SECONDS;
        } else if (numDaysToReset == HALF_YEAR) {
            dateExpiration = _currentTimestamp + HALF_YEAR_SECONDS;
        } else if (numDaysToReset == ONE_YEAR) {
            dateExpiration = _currentTimestamp + ONE_YEAR_SECONDS;
        } else {
            revert("Invalid Number of days to reset");
        }

        for (uint256 i = 0; i < _recipients.length; i++) {
            recipientBalances[_recipients[i]] = _amountPerRecipient[i];
        }

        for (uint256 i = 0; i < _recipients.length; i++) {
            recipientComments[_recipients[i]] = _comments[i];
        }
    }

    function withdraw() external payable {
        address _caller = msg.sender;
        uint256 _userBalance = recipientBalances[_caller];

        if (isExpired() && _userBalance > 0) {
            //First set user balance to 0 to prevent reentrancy attack
            recipientBalances[_caller] = 0;
            address payable _recipient = payable(_caller);
            _recipient.transfer(_userBalance);
        }
    }

    function resetSwitch() external {
        address _caller = msg.sender;
        if (owner == _caller) {
            uint256 _timestamp = block.timestamp;

            if (numDaysToReset == ONE_HOUR) {
                dateExpiration = _timestamp + ONE_HOUR_SECONDS;
            } else if (numDaysToReset == ONE_DAY) {
                dateExpiration = _timestamp + ONE_DAY_SECONDS;
            } else if (numDaysToReset == NINETY_DAYS) {
                dateExpiration = _timestamp + NINETY_DAYS_SECONDS;
            } else if (numDaysToReset == HALF_YEAR) {
                dateExpiration = _timestamp + HALF_YEAR_SECONDS;
            } else if (numDaysToReset == ONE_YEAR) {
                dateExpiration = _timestamp + ONE_YEAR_SECONDS;
            } else {
                revert("Invalid state");
            }
        }
    }

    function deleteSwitch() external {
        address _caller = msg.sender;
        if (owner == _caller) {
            address payable recipient = payable(_caller);
            uint256 contractBalance = address(this).balance;
            recipient.transfer(contractBalance);
        }
    }

    function isExpired() internal view returns (bool) {
        return block.timestamp > dateExpiration;
    }

    function getExpirationDate() external view returns (uint256) {
        return dateExpiration;
    }

    function getContractStatus()
        external
        view
        returns (
            uint256,
            uint256,
            uint256
        )
    {
        uint256 _contractBalance = address(this).balance;

        return (contractCreationDate, dateExpiration, _contractBalance);
    }

    function getWithdrawStatus()
        external
        view
        returns (
            uint256,
            uint256,
            string memory
        )
    {
        address _caller = msg.sender;

        return (
            dateExpiration,
            recipientBalances[_caller],
            recipientComments[_caller]
        );
    }

    function getDistributionDetails()
        external
        view
        returns (
            address[] memory,
            uint256[] memory,
            string[] memory
        )
    {
        uint256 count = recipients.length;
        uint256[] memory _amountPerRecipient = new uint256[](count);
        string[] memory _comments = new string[](count);

        for (uint256 i = 0; i < recipients.length; i++) {
            _amountPerRecipient[i] = recipientBalances[recipients[i]];
            _comments[i] = recipientComments[recipients[i]];
        }

        return (recipients, _amountPerRecipient, _comments);
    }
}
