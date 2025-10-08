package com.tblGroup.toBuyList.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "deposit")
public class Deposit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "description")
	private String description;

	@Column(name = "m_Account_Number", nullable = false, length = 9)
	private String mAccountNumber;
	
	@Column(name = "date_deposit")
	private LocalDate dateDeposit;
	
	@Column(name = "time_deposit")
	private LocalTime timeDeposit;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	public Deposit() {
	}
	
	public Deposit(double amount, String description, String mAccountNumber, LocalDate dateDeposit, LocalTime timeDeposit, Client client) {
		this.amount = amount;
		this.description = description;
		this.mAccountNumber = mAccountNumber;
		this.dateDeposit = dateDeposit;
		this.timeDeposit = timeDeposit;
		this.client = client;
	}
	
	public int getId() {
		return id;
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
	
	public LocalDate getDateDeposit() {
		return dateDeposit;
	}
	
	public void setDateDeposit(LocalDate dateDeposite) {
		this.dateDeposit = dateDeposite;
	}
	
	public LocalTime getTimeDeposit() {
		return timeDeposit;
	}
	
	public void setTimeDeposit(LocalTime timeDeposite) {
		this.timeDeposit = timeDeposite;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public String getMAccountNumber() {
		return mAccountNumber;
	}
	
	public void setmAccountNumber(String mAccountNumber) {
		this.mAccountNumber = mAccountNumber;
	}
}
