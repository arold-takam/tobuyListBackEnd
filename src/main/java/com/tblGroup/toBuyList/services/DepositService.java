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
	private final CreditRepository creditRepository;
	private final RefundRepository refundRepository;
	
	
	public DepositService(ClientRepository clientRepository, WalletRepository walletRepository, DepositRepository depositRepository, MoneyAccountRepository moneyAccountRepository, HistoryRepository historyRepository, CreditRepository creditRepository, RefundRepository refundRepository) {
		this.clientRepository = clientRepository;
		this.walletRepository = walletRepository;
		this.depositRepository = depositRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.historyRepository = historyRepository;
		this.creditRepository = creditRepository;
		this.refundRepository = refundRepository;
	}
	
	@Transactional
	public void makeDeposit(int clientID, DepositDTO depositDTO) {
		Client client = clientRepository.findById(clientID)
			.orElseThrow(() -> new IllegalArgumentException("No client found at the ID: " + clientID));
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(depositDTO.phoneMAccount());
		if (moneyAccount == null) {
			setHistory("Deposit of " + depositDTO.amount(), "FAILED", client);
			throw new IllegalArgumentException("Money Account with phone number: " + depositDTO.phoneMAccount() + " not found.");
		}
		
		if (depositDTO.amount() <= 0.0) {
			setHistory("Deposit of " + depositDTO.amount(), "FAILED", client);
			throw new IllegalArgumentException("This amount is invalid, please try again.");
		}
		
		if (moneyAccount.getAmount() < depositDTO.amount()) {
			setHistory("Deposit of " + depositDTO.amount(), "FAILED", client);
			throw new IllegalArgumentException("Your account " + moneyAccount.getName() + " has no sufficient amount for this deposit, please reload it. it balance is: " + moneyAccount.getAmount());
		}
		
		// --- Logique unifiée pour le dépôt et la pénalité ---
		
		double amountForWallet = depositDTO.amount(); // Montant initial à créditer au portefeuille

		// --- Exécution de la transaction de dépôt (logique unique) ---
		
		// Déduire le montant total du compte monétaire
		moneyAccount.setAmount(moneyAccount.getAmount() - depositDTO.amount());
		moneyAccountRepository.save(moneyAccount);
		
		// Créditer le montant restant au portefeuille
		client.getWallet().setAmount(client.getWallet().getAmount() + amountForWallet);
		walletRepository.save(client.getWallet());
		
		// Créer et sauvegarder l'objet Deposit
		Deposit deposit = new Deposit();
		deposit.setAmount(depositDTO.amount());
		deposit.setDescription(depositDTO.description());
		deposit.setmAccountNumber(depositDTO.phoneMAccount());
		deposit.setClient(client);
		deposit.setDateDeposit(LocalDate.now());
		deposit.setTimeDeposit(LocalTime.now());
		
		depositRepository.save(deposit);
		
		setHistory("Deposit of " + depositDTO.amount(), "SUCCESS", client);
	}
	
	// --- Méthodes de recherche inchangées ---
	public Deposit getDeposit(int clientID, int depositID) {
		Client client = clientRepository.findById(clientID)
			.orElseThrow(() -> new IllegalArgumentException("No client found at the ID: " + clientID));
		
		Deposit deposit = depositRepository.findByClient_IdAndId(clientID, depositID);
		if (deposit == null) {
			throw new IllegalArgumentException(client.getName() + " has made no deposit at this ID: " + depositID);
		}
		return deposit;
	}
	
	public List<Deposit> getAllDeposit(int clientID) {
		Client client = clientRepository.findById(clientID)
			.orElseThrow(() -> new IllegalArgumentException("No client found at the ID: " + clientID));
		
		List<Deposit> listDeposit = depositRepository.findAllByClientId(clientID);
		if (listDeposit.isEmpty()) {
			throw new IllegalArgumentException(client.getName() + " has made no deposit yet.");
		}
		return listDeposit;
	}
	
	private void setHistory(String description, String status, Client client) {
		History history = new History("DEPOSIT", description, new Date(System.currentTimeMillis()), status, client);
		historyRepository.save(history);
	}
}