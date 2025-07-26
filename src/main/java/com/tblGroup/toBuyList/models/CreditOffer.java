package com.tblGroup.toBuyList.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_offer")
public class CreditOffer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  int id;
	
	@Column(name = "title", unique = true)
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "amount_credit")
	private double limitationCreditAmount;
	
	@Column(name = "delay")
	private int creditDelay;
	
	@Column(name = "tax")
	private double creditTax;
	
	@OneToMany(mappedBy = "creditOffer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Credit>creditList = new ArrayList<>();
	
	public CreditOffer() {
	}
	
	public CreditOffer(String title, double limitationCreditAmount, int creditDelay, double creditTax, Credit credit, String description) {
		this.title = title;
		this.limitationCreditAmount = limitationCreditAmount;
		this.creditDelay = creditDelay;
		this.creditTax = creditTax;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public double getLimitationCreditAmount() {
		return limitationCreditAmount;
	}
	
	public void setLimitationCreditAmount(double limitationCreditAmount) {
		this.limitationCreditAmount = limitationCreditAmount;
	}
	
	public int getCreditDelay() {
		return creditDelay;
	}
	
	public void setCreditDelay(int creditDelay) {
		this.creditDelay = creditDelay;
	}
	
	public double getCreditTax() {
		return creditTax;
	}
	
	public void setCreditTax(double creditTax) {
		this.creditTax = creditTax;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
