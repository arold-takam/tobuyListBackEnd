package com.tblGroup.toBuyList.models;


import com.tblGroup.toBuyList.models.Enum.MoneyAccountName;
import jakarta.persistence.*;

@Entity
@Table(name = "money_account")
public class MoneyAccount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "name", nullable = false)
	private MoneyAccountName name;
	
	@Column(name = "phone", unique = true, nullable = false, length= 9)
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
	
	public MoneyAccount(MoneyAccountName name, String phone, String password, double amount, Client client) {
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.amount = amount;
		this.client = client;
	}
	
	//	----------------------------------------------------------------------------------------------------------------------------------------------
	
	public int getId() {
		return id;
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

    public MoneyAccountName getName() {
        return name;
    }

    public void setName(MoneyAccountName name) {
        this.name = name;
    }
}