package com.tblGroup.toBuyList.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
public class Client {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "mail", nullable = false, unique = true)
	private String mail;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<MoneyAccount>moneyAccountList = new ArrayList<>();

	@OneToOne()
	@JoinColumn(name = "wallet")
	private Wallet wallet;
	
	private boolean blocked;
	
	public Client() {
	}
	
	public Client(String name, String mail, String password, List<MoneyAccount>moneyAccountList, Wallet wallet) {
		this.name = name;
		this.mail = mail;
		this.password = password;
		this.moneyAccountList = moneyAccountList;
        this.wallet = wallet;
    }

//	------------------------------------------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<MoneyAccount> getMoneyAccountList() {
		return moneyAccountList;
	}
	
	public void setMoneyAccountList(List<MoneyAccount> moneyAccountList) {
		this.moneyAccountList = moneyAccountList;
	}

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
}