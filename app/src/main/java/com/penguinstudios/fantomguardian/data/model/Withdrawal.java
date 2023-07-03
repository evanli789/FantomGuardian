package com.penguinstudios.fantomguardian.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigInteger;

@Entity(tableName = "withdrawals")
public class Withdrawal {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "owner_address")
    private String ownerAddress;

    @ColumnInfo(name = "contract_address")
    private String contractAddress;

    @ColumnInfo(name = "date_withdrawn")
    private long dateWithdrawn;

    @ColumnInfo(name = "amount")
    private long amount;

    @ColumnInfo(name = "comments")
    private String encryptedComments;

    @ColumnInfo(name = "decryption_phrase")
    private String decryptionPhrase;

    //Empty public constructor required for Room
    public Withdrawal() {
    }

    private Withdrawal(Builder builder) {
        this.ownerAddress = builder.ownerAddress;
        this.contractAddress = builder.contractAddress;
        this.dateWithdrawn = builder.dateWithdrawn;
        this.amount = builder.amount;
        this.encryptedComments = builder.encryptedComments;
        this.decryptionPhrase = builder.decryptionPhrase;
    }

    public static class Builder {
        private String ownerAddress;
        private String contractAddress;
        private long dateWithdrawn;
        private long amount;
        private String encryptedComments;
        private String decryptionPhrase;

        public Builder ownerAddress(String ownerAddress){
            this.ownerAddress = ownerAddress;
            return Builder.this;
        }

        public Builder contractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
            return Builder.this;
        }

        public Builder dateWithdrawn(BigInteger dateWithdrawn) {
            this.dateWithdrawn = dateWithdrawn.longValue();
            return Builder.this;
        }

        public Builder amount(BigInteger amount) {
            this.amount = amount.longValue();
            return Builder.this;
        }

        public Builder encryptedComments(String encryptedComments) {
            this.encryptedComments = encryptedComments;
            return Builder.this;
        }

        public Builder decryptionPhrase(String decryptionPhrase) {
            this.decryptionPhrase = decryptionPhrase;
            return Builder.this;
        }

        public Withdrawal build(){
            return new Withdrawal(this);
        }
    }

    public int getId() {
        return id;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getDateWithdrawn() {
        return dateWithdrawn;
    }

    public void setDateWithdrawn(long dateWithdrawn) {
        this.dateWithdrawn = dateWithdrawn;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getEncryptedComments() {
        return encryptedComments;
    }

    public void setEncryptedComments(String encryptedComments) {
        this.encryptedComments = encryptedComments;
    }

    public String getDecryptionPhrase() {
        return decryptionPhrase;
    }

    public void setDecryptionPhrase(String decryptionPhrase) {
        this.decryptionPhrase = decryptionPhrase;
    }
}
