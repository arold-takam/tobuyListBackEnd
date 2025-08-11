package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DepositService {
	private final DepositRepository depositRepository;
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final HistoryRepository historyRepository;

	
	
	public DepositService(ClientRepository clientRepository, WalletRepository walletRepository, DepositRepository depositRepository, MoneyAccountRepository moneyAccountRepository, HistoryRepository historyRepository) {
		this.clientRepository = clientRepository;
		this.walletRepository = walletRepository;
		this.depositRepository = depositRepository;
		this.moneyAccountRepository = moneyAccountRepository;
        this.historyRepository = historyRepository;
    }
	
//	--------------------------------------------------------------------DEPOSIT MANAGEMENT-----------------------------------------------------------------
	@Transactional
	public void makeDeposit(int clientID, DepositDTO depositDTO) throws Exception {
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		Wallet clientWallet = client.getWallet();
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(depositDTO.phoneMAccount());
		
		if (moneyAccount == null) {
			setHistory("Deposit of "+depositDTO.amount(), "FAILED", client);
			throw new IllegalArgumentException("Money Account with phone number: " + depositDTO.phoneMAccount() + " not found.");
		}
		
		if (depositDTO.amount() <= 0.0){
			setHistory("Deposit of "+depositDTO.amount(), "FAILED", client);
			throw new Exception("This amount is invalid, please try again.");
		}
		if (moneyAccount.getAmount() < depositDTO.amount()){
			setHistory("Deposit of "+depositDTO.amount(), "FAILED", client);
			throw new Exception("Your account "+moneyAccount.getName()+" has no sufficient amount for this deposit, please reload it. it balance is: "+moneyAccount.getAmount());
		}
		
		Deposit deposit = new Deposit();
		
		deposit.setAmount(depositDTO.amount());
		deposit.setDescription(depositDTO.description());
		
		moneyAccount.setAmount(moneyAccount.getAmount() - depositDTO.amount());
		deposit.setmAccountNumber(depositDTO.phoneMAccount());
		
		clientWallet.setAmount(clientWallet.getAmount() + depositDTO.amount());
		deposit.setClient(client);
		
		deposit.setDateDeposit(LocalDate.now());
		deposit.setTimeDeposit(LocalTime.now());
		
		moneyAccountRepository.save(moneyAccount);
		walletRepository.save(clientWallet);
		
		depositRepository.save(deposit);

		setHistory("Deposit of "+depositDTO.amount(), "SUCCESS", client);
	}
	
	public Deposit getDeposit(int clientID, int depositID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		Deposit deposit = depositRepository.findByClient_IdAndId(clientID, depositID);
		
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
		
		List<Deposit>listDeposit = depositRepository.findAllByClientId(clientID);
		if (listDeposit.isEmpty()){
			throw new IllegalArgumentException(client.getName()+" has made no deposit yet.");
		}
		
		return listDeposit;
	}

	private void setHistory(String description, String status, Client client){
		History history = new History("DEPOSIT", description, new Date(System.currentTimeMillis()), status, client);

		historyRepository.save(history);
	}
	
}
