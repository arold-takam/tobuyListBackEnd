package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.DepositeDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Deposit;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.DepositeRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class DepositSercives {
	private final DepositeRepository depositeRepository;
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final MoneyAccountService moneyAccountService;
	
	
	public DepositSercives(ClientRepository clientRepository, WalletRepository walletRepository, MoneyAccountService moneyAccountService, DepositeRepository depositeRepository, MoneyAccountRepository moneyAccountRepository) {
		this.clientRepository = clientRepository;
		this.walletRepository = walletRepository;
		this.moneyAccountService = moneyAccountService;
		this.depositeRepository = depositeRepository;
		this.moneyAccountRepository = moneyAccountRepository;
	}
	
//	--------------------------------------------------------------------DEPOSIT MANAGEMENT-----------------------------------------------------------------
	@Transactional
	public Deposit makeDeposit(int clientID, String phoneMAccount, DepositeDTO depositeDTO){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		Wallet clientWallet = client.getWallet();
		if (clientWallet == null){
			throw new IllegalArgumentException("This client has no wallet yet");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(phoneMAccount);
		
		if (moneyAccount == null) {
			throw new IllegalArgumentException("Money Account with phone number: " + phoneMAccount + " not found.");
		}
		
		if (moneyAccount.getClient().getId() != clientID) {
			throw new IllegalArgumentException("Money Account with phone: " + phoneMAccount + " does not belong to client with ID: " + clientID);
		}
		
		if (depositeDTO.amount() <= 0.0){
			throw new IllegalArgumentException("This amount is invalid, please try again.");
		}
		if (moneyAccount.getAmount() < depositeDTO.amount()){
			throw new IllegalArgumentException("Your account "+moneyAccount.getName()+" has no sufficient amount for this deposit, please reload it. it balance is: "+moneyAccount.getAmount());
		}
		
		Deposit deposit = new Deposit();
		
		deposit.setAmount(depositeDTO.amount());
		deposit.setDescription(depositeDTO.description());
		
		moneyAccount.setAmount(moneyAccount.getAmount() - depositeDTO.amount());
		deposit.setMoneyAccount(moneyAccount);
		
		clientWallet.setAmount(clientWallet.getAmount() + depositeDTO.amount());
		deposit.setClient(client);
		
		deposit.setDateDeposite(LocalDate.now());
		deposit.setTimeDeposite(LocalTime.now());
		
		moneyAccountRepository.save(moneyAccount);
		walletRepository.save(clientWallet);
		
		return depositeRepository.save(deposit);
	}
	
	public Deposit getDeposit(int clientID, int depositID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		Deposit deposit = depositeRepository.findByClient_IdAndId(clientID, depositID);
		
		if (deposit == null){
			throw new IllegalArgumentException(client.getName()+" has made no deposit at this ID: "+depositID);
		}
		
		return deposit;
	}
	
	public List<Deposit> getAllDeposit(int clientID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		List<Deposit>listDeposit = depositeRepository.findAllByClientId(clientID);
		if (listDeposit.isEmpty()){
			throw new IllegalArgumentException(client.getName()+"   has made no deposit yet.");
		}
		
		return listDeposit;
	}
	
}
