package com.tblGroup.toBuyList.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "refund")
public class Refund {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "credit_id", nullable = false)
	private Credit credit;
	
	@Column(name = "money_account_number")
	private String moneyAccountNumber;
	
	@Column(name = "date_refund")
	private LocalDate dateRefund;
	
	@Column(name = "time_refund")
	private LocalTime timeRefund;
	
	@Column(name = "amount")
	private double amount;
	
	@Column(name = "status")
	private boolean ended;
	
	public Refund() {
	}
	
	public Refund(String description, Credit credit, String moneyAccountNumber, LocalDate dateRefund, LocalTime timeRefund, double amount, boolean ended) {
		this.description = description;
		this.credit = credit;
		this.moneyAccountNumber = moneyAccountNumber;
		this.dateRefund = dateRefund;
		this.timeRefund = timeRefund;
		this.amount = amount;
		this.ended = ended;
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
	
	public Credit getCredit() {
		return credit;
	}
	
	public void setCredit(Credit credit) {
		this.credit = credit;
	}
	
	public LocalDate getDateRefund() {
		return dateRefund;
	}
	
	public String getMoneyAccountNumber() {
		return moneyAccountNumber;
	}
	
	public void setMoneyAccountNumber(String moneyAccountNumber) {
		this.moneyAccountNumber = moneyAccountNumber;
	}
	
	public void setDateRefund(LocalDate dateRefund) {
		this.dateRefund = dateRefund;
	}
	
	public LocalTime getTimeRefund() {
		return timeRefund;
	}
	
	public void setTimeRefund(LocalTime timeRefund) {
		this.timeRefund = timeRefund;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
//	-------------------------------------------------------------------------------------------------------------------------------
	
	public boolean closesCredit() {
		return ended;
	}
}
