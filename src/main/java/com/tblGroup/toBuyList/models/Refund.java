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
	
	@OneToOne
	@JoinColumn(name = "money_account_id", nullable = false)
	private MoneyAccount moneyAccount;
	
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
	
	public Refund(String description, Credit credit, MoneyAccount moneyAccount, LocalDate dateRefund, LocalTime timeRefund, double amount, boolean ended) {
		this.description = description;
		this.credit = credit;
		this.moneyAccount = moneyAccount;
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
	
	public MoneyAccount getMoneyAccount() {
		return moneyAccount;
	}
	
	public void setMoneyAccount(MoneyAccount moneyAccount) {
		this.moneyAccount = moneyAccount;
	}
	
	public LocalDate getDateRefund() {
		return dateRefund;
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
