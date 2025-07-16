package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoneyAccountService {
	private MoneyAccountRepository moneyAccountRepository;
	private ClientRepository clientRepository;

	public MoneyAccountService(MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository) {
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
	}
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------MONEYaCCOUNTmANAGEMENT------------------------------------------------
	public MoneyAccount createAccount(int clientID, MoneyAccount moneyAccount){
		Optional<Client>optionalClient= clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with this id not found");
		}
		
		if (moneyAccount == null){
			throw new IllegalArgumentException("This account is invalid, try again.");
		}
		
		Client clientConcerned = optionalClient.get();
		
		moneyAccount.setClient(clientConcerned);
		
		return moneyAccountRepository.save(moneyAccount);
	}
	
	public MoneyAccount getAccountByID(int clientID, int mAccountID){
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("Money account with ID: " + mAccountID + " not found for client ID: " + clientID + ".");
			}
			
			Hibernate.initialize(moneyAccount.getClient());
			
			return moneyAccount;
		}
		
	public List<MoneyAccount>getAllAccounts(int clientID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			Client clientSaved = optionalClient.get();
			
			List<MoneyAccount>moneyAccountList = moneyAccountRepository.findAllByClientId(clientID);
			
			for (MoneyAccount ma: moneyAccountList){
				Hibernate.initialize(ma.getClient());
			}
			
			return moneyAccountList;
		}
		
	public MoneyAccount updateAccount(int clientID, int mAccountID,  MoneyAccount newMoneyAccount){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccountFound = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccountFound != null){
				moneyAccountFound.setName(newMoneyAccount.getName());
				moneyAccountFound.setAmount(newMoneyAccount.getAmount());
				moneyAccountFound.setPassword(newMoneyAccount.getPassword());
				moneyAccountFound.setPhone(newMoneyAccount.getPhone());
				
				
				clientRepository.save(optionalClient.get());
				
				return moneyAccountFound;
			}
			
			throw new IllegalArgumentException("No money account found at this ID.");
		}
		
	public Boolean deleteAccount(int clientID, int mAccountID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("This client has no account at this ID, You could create him.");
			}
			
			moneyAccountRepository.delete(moneyAccount);
			
			return true;
		}
	
	
//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	
	public MoneyAccount makeDeposit(int clientID, double amount, int mAccountID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		if (amount <= 0){
			throw new IllegalArgumentException("This amount is invalid, please try again  with amount up to 0 ");
		}
		
		if (moneyAccount != null){
			moneyAccount.setAmount(moneyAccount.getAmount() + amount);
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
	
	public MoneyAccount makeRetrieve(int clientID, double amount, int mAccountID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		if (moneyAccount != null){
			if (amount <= 0 || amount > moneyAccount.getAmount()){
				throw new IllegalArgumentException("This amount is invalid, please try again  with amount up to 0 & down to: "+moneyAccount.getAmount());
			}
			
			moneyAccount.setAmount(moneyAccount.getAmount() - amount);
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
}
