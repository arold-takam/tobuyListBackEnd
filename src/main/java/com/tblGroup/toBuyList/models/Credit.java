package com.tblGroup.toBuyList.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "credit")
public class Credit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "date_credit")
	private LocalDate dateCredit;
	
	@Column(name = "time_credit")
	private LocalTime timeCredit;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "credit_offer_id", referencedColumnName = "id")
	private CreditOffer creditOffer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Client client;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "receiver_account_id", referencedColumnName = "id")
	private MoneyAccount receiverAccount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "wallet_receiver_id",  referencedColumnName = "id")
	private Wallet walletReceiver;
	
	public Credit() {
	}
	
	public Credit(String title, String description, LocalDate dateCredit, LocalTime timeCredit, CreditOffer creditOffer, Client client, MoneyAccount receiverAccount, Wallet walletReceiver) {
		this.title = title;
		this.description = description;
		this.dateCredit = dateCredit;
		this.timeCredit = timeCredit;
		this.creditOffer = creditOffer;
		this.client = client;
		this.receiverAccount = receiverAccount;
		this.walletReceiver = walletReceiver;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
	public CreditOffer getCreditOffer() {
		return creditOffer;
	}
	
	public void setCreditOffer(CreditOffer creditOffer) {
		this.creditOffer = creditOffer;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public MoneyAccount getReceiverAccount() {
		return receiverAccount;
	}
	
	public void setReceiverAccount(MoneyAccount receiverAccount) {
		this.receiverAccount = receiverAccount;
	}
	
	public int getId() {
		return id;
	}
	
	public Wallet getWalletReceiver() {
		return walletReceiver;
	}
	
	public void setWalletReceiver(Wallet walletReceiver) {
		this.walletReceiver = walletReceiver;
	}
}
