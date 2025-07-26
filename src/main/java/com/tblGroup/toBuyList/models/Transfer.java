package com.tblGroup.toBuyList.models;

import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="transfer")
public class Transfer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="amount" , nullable = false)
    private double amount;

    @Column(name="description")
    private String description;


    @Column(name="receiver_account")
    private String receiverAccountNumber;


    @Column(name = "wallet_number")
    private String walletNumber;

    @Column(name="date_transfer")
    private Date dateTransfer;

    @ManyToOne()
    @JoinColumn(name="client_id")
    private Client client;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="type_transfer")
    private TypeTransfer typeTransfer;

    public Transfer(double amount, String description, String receiverAccountNumber, String walletNumber, Date dateTransfer, Client client, TypeTransfer typeTransfer) {
        this.amount = amount;
        this.description = description;
        this.receiverAccountNumber = receiverAccountNumber;
        this.walletNumber = walletNumber;
        this.dateTransfer = dateTransfer;
        this.client = client;
        this.typeTransfer = typeTransfer;
    }

    public Transfer() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
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
