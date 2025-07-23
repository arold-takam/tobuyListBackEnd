package com.tblGroup.toBuyList.models;

import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="transfer")
public class Transfer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int Id;

    @Column(name="amount")
    private double amount;

    @Column(name="description")
    private String description;

    @OneToOne
    @JoinColumn(name="money_account_id")
    private MoneyAccount receiverAccount;

    @OneToOne
    @JoinColumn(name = "wallet_receiver_id")
    private Wallet walletReceiver;

    @Column(name="date")
    private Date dateTransfer;

    @Column(name="type_Transfer")
    private TypeTransfer typeTransfer;

    public Transfer(double amount, String description, MoneyAccount receiverAccount, Wallet walletReceiver, Date dateTransfer, TypeTransfer typeTransfer) {
        this.amount = amount;
        this.description = description;
        this.receiverAccount = receiverAccount;
        this.walletReceiver = walletReceiver;
        this.dateTransfer = dateTransfer;
        this.typeTransfer = typeTransfer;
    }

    public Transfer() {

    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MoneyAccount getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(MoneyAccount receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public Wallet getWalletReceiver() {
        return walletReceiver;
    }

    public void setWalletReceiver(Wallet walletReceiver) {
        this.walletReceiver = walletReceiver;
    }

    public Date getDateTransfer() {
        return dateTransfer;
    }

    public void setDateTransfer(Date dateTransfer) {
        this.dateTransfer = dateTransfer;
    }

    public TypeTransfer getTypeTransfer() {
        return typeTransfer;
    }

    public void setTypeTransfer(TypeTransfer typeTransfer) {
        this.typeTransfer = typeTransfer;
    }
}
