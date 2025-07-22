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


    public Wallet(double amount) {
        this.amount = amount;
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
}
