package com.penguinstudios.fantomguardian.data;

import com.penguinstudios.fantomguardian.contract.DeadMansSwitch;
import com.penguinstudios.fantomguardian.data.model.Network;
import com.penguinstudios.fantomguardian.data.model.ResetDuration;
import com.penguinstudios.fantomguardian.data.model.SendToRecipient;
import com.penguinstudios.fantomguardian.util.CustomGasProvider;
import com.penguinstudios.fantomguardian.util.EncryptionUtil;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RemoteRepository {

    private final WalletRepository walletRepository;
    private final Web3j web3j;

    @Inject
    public RemoteRepository(Web3j web3j, WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
        this.web3j = web3j;
    }

    //Used for read only methods
    private DeadMansSwitch getContract(String contractAddress) {
        return DeadMansSwitch.load(contractAddress, web3j,
                walletRepository.getCredentials(), new DefaultGasProvider());
    }

    //Used for modifying contract state transactions
    private FastRawTransactionManager createTxManager() {
        return new FastRawTransactionManager(web3j, walletRepository.getCredentials(),
                Network.MAIN_NET.getChainId());
    }

    //Contract stores user balances as wei
    public DeadMansSwitch createSwitch(
            List<SendToRecipient> sendToRecipients,
            ResetDuration resetDuration,
            String password) throws Exception {

        final List<String> recipientAddresses = new ArrayList<>();
        final List<BigInteger> amountPerRecipient = new ArrayList<>();
        final List<String> recipientComments = new ArrayList<>();
        BigInteger totalAmountToSend = BigInteger.ZERO;

        for (SendToRecipient sendToRecipient : sendToRecipients) {
            recipientAddresses.add(sendToRecipient.getRecipientAddress());
            amountPerRecipient.add(sendToRecipient.getAmountFtmAsBigInt());
            totalAmountToSend = totalAmountToSend.add(sendToRecipient.getAmountFtmAsBigInt());

            String encryptedComment = EncryptionUtil.encryptString(sendToRecipient.getComments(), password);
            recipientComments.add(encryptedComment);
        }

        return DeadMansSwitch.deploy(web3j, createTxManager(), new CustomGasProvider(),
                totalAmountToSend, BigInteger.valueOf(resetDuration.getNumDays()),
                recipientAddresses, amountPerRecipient, recipientComments).send();
    }

    //Gets the wallet balance in wei
    public EthGetBalance getWalletBalance(String walletAddress) throws IOException {
        return web3j.ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST).send();
    }

    public TransactionReceipt resetSwitch(String contractAddress) throws Exception {
        return DeadMansSwitch.load(contractAddress, web3j, createTxManager(),
                new CustomGasProvider()).resetSwitch().send();
    }

    public TransactionReceipt deleteSwitch(String contractAddress) throws Exception {
        return DeadMansSwitch.load(contractAddress, web3j, createTxManager(),
                new CustomGasProvider()).deleteSwitch().send();
    }

    //Tuple3: List of each -- Recipient Wallet Address, Amount each recipient receives, Comments
    //Comments are encrypted on chain
    public Tuple3<List<String>, List<BigInteger>, List<String>> getDistributionDetails(String contractAddress) throws Exception {
        return getContract(contractAddress).getDistributionDetails().send();
    }

    //Tuple3: Expiration Date, Recipient Balance, Recipient Comment
    public Tuple3<BigInteger, BigInteger, String> getWithdrawStatus(String contractAddress) throws Exception {
        return getContract(contractAddress).getWithdrawStatus().send();
    }

    //Tuple3: Contract Creation Date, Date Expiration, Contract Balance
    public Tuple3<BigInteger, BigInteger, BigInteger> getContractStatus(String contractAddress) throws Exception {
        return getContract(contractAddress).getContractStatus().send();
    }

    //Payable function so it accepts a param BigInteger. Since we are not sending any tokens to contract to it, set it as 0
    public TransactionReceipt withdraw(String contractAddress) throws Exception {
        return DeadMansSwitch.load(contractAddress, web3j, createTxManager(),
                new CustomGasProvider()).withdraw(BigInteger.ZERO).send();
    }

    public BigInteger getDateExpiration(String contractAddress) throws Exception {
        return getContract(contractAddress).getExpirationDate().send();
    }

    //Gets the current timestamp of chain from RPC node
    public BigInteger getTimestamp() throws IOException {
        return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                .send().getBlock().getTimestamp();
    }
}
