package com.tblGroup.toBuyList.models;

import jakarta.persistence.*;

@Entity
@Table(name="wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(name="amount")
    private double amount;

    @Column(name="wallet_number" ,unique=true, length = 6)
    private String walletNumber;


    public Wallet(double amount, String walletNumber) {
        this.amount = amount;
        this.walletNumber = walletNumber;
    }

    public Wallet() {

    }


    public int getId() {
        return Id;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }


}
