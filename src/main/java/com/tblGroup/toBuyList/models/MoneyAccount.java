package com.tblGroup.toBuyList.models;


import jakarta.persistence.*;

@Entity
@Table(name = "money_account")
public class MoneyAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name",  nullable = true)
	private String name;
	
	@Column(name = "phone", unique = true, nullable = false)
	private String phone;
	
	@Column(name = "password",  nullable = false)
	private String password;
	
	@Column(name = "amount")
	private double amount;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "client_id", nullable = false)
	private Client client;
	
	public MoneyAccount() {
	}
	
	public MoneyAccount(String name, String phone, String password, Client client, double amount) {
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.client = client;
		this.amount = amount;
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
