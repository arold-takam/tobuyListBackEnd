package com.tblGroup.toBuyList.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tblGroup.toBuyList.models.Enum.Role;
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

	@Column(name = "username", nullable = false, unique = true)
	private String username;
	
	@Column(name = "mail", nullable = false, unique = true)
	private String mail;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role roleName;
	
	@OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<MoneyAccount>moneyAccountList = new ArrayList<>();
	
	
	@OneToOne()
	@JoinColumn(name = "wallet")
	private Wallet wallet;
	
	
	public Client() {
	}
	
	public Client(String name, String username, String mail, String password, Role roleName, List<MoneyAccount>moneyAccountList, Wallet wallet) {
		this.name = name;
        this.username = username;
        this.mail = mail;
		this.password = password;
		this.roleName = roleName;
		this.moneyAccountList = moneyAccountList;
		this.wallet = wallet;
    }

//	------------------------------------------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
	
	public Role getRoleName() {
		return roleName;
	}
	
	public void setRoleName(Role roleName) {
		this.roleName = roleName;
	}
}