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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "client_id" , nullable = false)
    private Client client;


    public Wallet(double amount, String walletNumber, Client client) {
        this.amount = amount;
        this.walletNumber = walletNumber;
        this.client = client;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getWalletNumber() {
        return walletNumber;
    }

    public void setWalletNumber(String walletNumber) {
        this.walletNumber = walletNumber;
    }
}
