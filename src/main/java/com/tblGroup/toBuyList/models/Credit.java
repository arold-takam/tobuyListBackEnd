package com.tblGroup.toBuyList.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "credit")
public class Credit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "date_credit", nullable = false)
	LocalDate dateCredit;
	
	@Column(name = "time_credit", nullable = false)
	LocalTime timeCredit;
	
	@Column(name = "receiver_account")
	private int receiverAccountID;
	
	@Column(name = "wallet_receiver")
	private int walletReceiverID;
	
	@Column(name = "amountRefund")
	private double amountRefund;
	
	@OneToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	@OneToOne
	@JoinColumn(name = "credit_offer", nullable = false)
	private CreditOffer creditOffer;
	
	public Credit() {
	
	}
	
	public Credit(String description, LocalDate dateCredit, LocalTime timeCredit, int receiverAccountID, int walletReceiverID, double amountToRefund, Client client, CreditOffer creditOffer) {
		this.description = description;
		this.dateCredit = dateCredit;
		this.timeCredit = timeCredit;
		this.receiverAccountID = receiverAccountID;
		this.walletReceiverID = walletReceiverID;
		this.amountRefund = amountRefund;
		this.client = client;
		this.creditOffer = creditOffer;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDate getDateCredit() {
		return dateCredit;
	}
	
	public void setDateCredit(LocalDate dateCredit) {
		this.dateCredit = dateCredit;
	}
	
	public LocalTime getTimeCredit() {
		return timeCredit;
	}
	
	public void setTimeCredit(LocalTime timeCredit) {
		this.timeCredit = timeCredit;
	}
	
	public int getReceiverAccountID() {
		return receiverAccountID;
	}
	
	public void setReceiverAccountID(int receiverAccountID) {
		this.receiverAccountID = receiverAccountID;
	}
	
	public int getWalletReceiverID() {
		return walletReceiverID;
	}
	
	public void setWalletReceiverID(int walletReceiverID) {
		this.walletReceiverID = walletReceiverID;
	}
	
	public double getAmountRefund() {
		return amountRefund;
	}
	
	public void setAmountRefund(double amountRefund) {
		this.amountRefund = amountRefund;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public CreditOffer getCreditOffer() {
		return creditOffer;
	}
	
	public void setCreditOffer(CreditOffer creditOffer) {
		this.creditOffer = creditOffer;
	}
}
