package com.tblGroup.toBuyList.models;


import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import jakarta.persistence.*;

@Entity
@Table(name = "credit_offer")
public class CreditOffer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "title_credit_offer", unique = true)
	private TitleCreditOffer titleCreditOffer;
	
	@Column(name = "limitation_credit_amount")
	private double limitationCreditAmount;
	
	@Column(name = "credit_delay")
	private int creditDelay;
	
	@Column(name = "tax_after_delay")
	private float taxAfterDelay;
	
	public CreditOffer() {
	}
	
	public CreditOffer(TitleCreditOffer titleCreditOffer, double limitationCreditAmount, int creditDelay, float taxAfterDelay) {
		this.titleCreditOffer = titleCreditOffer;
		this.limitationCreditAmount = limitationCreditAmount;
		this.creditDelay = creditDelay;
		this.taxAfterDelay = taxAfterDelay;
	}
	
	public int getId() {
		return id;
	}
	
	public TitleCreditOffer getTitleCreditOffer() {
		return titleCreditOffer;
	}
	
	public void setTitleCreditOffer(TitleCreditOffer titleCreditOffer) {
		this.titleCreditOffer = titleCreditOffer;
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
	
	public float getTaxAfterDelay() {
		return taxAfterDelay;
	}
	
	public void setTaxAfterDelay(float taxAfterDelay) {
		this.taxAfterDelay = taxAfterDelay;
	}
}
