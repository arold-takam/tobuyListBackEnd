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
	
	@Column(name = "mAccountNumber", nullable = false, length = 9)
	private String mAccountNumber;
	
	@Column(name = "date_deposite")
	private LocalDate dateDeposite;
	
	@Column(name = "time_deposite")
	private LocalTime timeDeposite;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	public Deposit() {
	}
	
	public Deposit(double amount, String description, String mAccountNumber, LocalDate dateDeposite, LocalTime timeDeposite, Client client) {
		this.amount = amount;
		this.description = description;
		this.mAccountNumber = mAccountNumber;
		this.dateDeposite = dateDeposite;
		this.timeDeposite = timeDeposite;
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
	
	public LocalDate getDateDeposite() {
		return dateDeposite;
	}
	
	public void setDateDeposite(LocalDate dateDeposite) {
		this.dateDeposite = dateDeposite;
	}
	
	public LocalTime getTimeDeposite() {
		return timeDeposite;
	}
	
	public void setTimeDeposite(LocalTime timeDeposite) {
		this.timeDeposite = timeDeposite;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public String getmAccountNumber() {
		return mAccountNumber;
	}
	
	public void setmAccountNumber(String mAccountNumber) {
		this.mAccountNumber = mAccountNumber;
	}
}
