package com.penguinstudios.fantomguardian.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigInteger;
import java.util.List;

@Entity(tableName = "contracts")
public class Contract {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "owner_address")
    private String ownerAddress;

    @ColumnInfo(name = "contract_address")
    private String contractAddress;

    @ColumnInfo(name = "num_recipients")
    private int numRecipients;

    @ColumnInfo(name = "days_to_reset")
    private int numDaysToReset;

    @ColumnInfo(name = "date_of_expiration")
    private long dateOfExpiration;

    @ColumnInfo(name = "contract_tx_hash")
    private String contractTxHash;

    @ColumnInfo(name = "decryption_phrase")
    private String decryptionPhrase;

    //Empty public constructor required for Room
    public Contract() {
    }

    private Contract(Builder builder) {
        this.ownerAddress = builder.ownerAddress;
        this.contractAddress = builder.contractAddress;
        this.numRecipients = builder.numRecipients;
        this.numDaysToReset = builder.numDaysToReset;
        this.dateOfExpiration = builder.dateOfExpiration;
        this.contractTxHash = builder.contractTxHash;
        this.decryptionPhrase = builder.decryptionPhrase;
    }

    public static class Builder {
        private String ownerAddress;
        private String contractAddress;
        private int numRecipients;
        private int numDaysToReset;
        private long dateOfExpiration;
        private String contractTxHash;
        private String decryptionPhrase;

        public Builder ownerAddress(String ownerAddress) {
            this.ownerAddress = ownerAddress;
            return Builder.this;
        }

        public Builder contractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
            return Builder.this;
        }

        public Builder numRecipients(List<SendToRecipient> list) {
            this.numRecipients = list.size();
            return Builder.this;
        }

        public Builder resetDuration(ResetDuration resetDuration) {
            this.numDaysToReset = resetDuration.getNumDays();
            return Builder.this;
        }

        public Builder dateOfExpiration(BigInteger dateOfExpiration) {
            this.dateOfExpiration = dateOfExpiration.longValue();
            return Builder.this;
        }

        public Builder contractTxHash(String contractTxHash) {
            this.contractTxHash = contractTxHash;
            return Builder.this;
        }

        public Builder decryptionPhrase(String decryptionPhrase){
            this.decryptionPhrase = decryptionPhrase;
            return Builder.this;
        }

        public Contract build() {
            return new Contract(this);
        }
    }

    //Getters and Setters required for Room

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getNumRecipients() {
        return numRecipients;
    }

    public void setNumRecipients(int numRecipients) {
        this.numRecipients = numRecipients;
    }

    public int getNumDaysToReset() {
        return numDaysToReset;
    }

    public void setNumDaysToReset(int numDaysToReset) {
        this.numDaysToReset = numDaysToReset;
    }

    public long getDateOfExpiration() {
        return dateOfExpiration;
    }

    public void setDateOfExpiration(long dateOfExpiration) {
        this.dateOfExpiration = dateOfExpiration;
    }

    public String getContractTxHash() {
        return contractTxHash;
    }

    public void setContractTxHash(String contractTxHash) {
        this.contractTxHash = contractTxHash;
    }

    public String getDecryptionPhrase() {
        return decryptionPhrase;
    }

    public void setDecryptionPhrase(String decryptionPhrase) {
        this.decryptionPhrase = decryptionPhrase;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", ownerAddress='" + ownerAddress + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", numRecipients=" + numRecipients +
                ", numDaysToReset=" + numDaysToReset +
                ", dateOfExpiration=" + dateOfExpiration +
                ", contractTxHash='" + contractTxHash + '\'' +
                ", decryptionPhrase='" + decryptionPhrase + '\'' +
                '}';
    }
}
