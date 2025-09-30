package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DepositService {
	private final DepositRepository depositRepository;
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final HistoryService historyService;
	private final CreditRepository creditRepository;
	private final RefundRepository refundRepository;
	
	
	public DepositService(ClientRepository clientRepository, WalletRepository walletRepository, DepositRepository depositRepository, MoneyAccountRepository moneyAccountRepository, HistoryService historyService, CreditRepository creditRepository, RefundRepository refundRepository) {
		this.clientRepository = clientRepository;
		this.walletRepository = walletRepository;
		this.depositRepository = depositRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.historyService = historyService;
		this.creditRepository = creditRepository;
		this.refundRepository = refundRepository;
	}
	
	
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Transactional
	public void makeDeposit(int clientID, DepositDTO depositDTO) {
		// 1. Validation de base
		Client client = clientRepository.findById(clientID)
			.orElseThrow(() -> new IllegalArgumentException("No client found at the ID: " + clientID));
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(depositDTO.phoneMAccount());
		if (moneyAccount == null || moneyAccount.getAmount() < depositDTO.amount() || depositDTO.amount() <= 0.0) {
			historyService.setHistory("DEPOSIT","Deposit of " + depositDTO.amount(), "FAILED", client);
			throw new IllegalArgumentException("Invalid deposit or insufficient funds.");
		}
		
		// Débit du compte monétaire, une seule fois
		moneyAccount.setAmount(moneyAccount.getAmount() - depositDTO.amount());
		moneyAccountRepository.save(moneyAccount);
		
		// Déterminer le montant qui ira au portefeuille après le potentiel remboursement
		double amountForWallet = depositDTO.amount();
		
		// 2. Logique de remboursement automatique (DOIT ÊTRE FAITE AVANT LE CRÉDIT AU WALLET)
		Optional<Credit> lateCreditOpt = creditRepository.findAllByClient(client).stream()
                .filter(c -> c.isActive() && c.getDateCredit().plusDays(c.getCreditOffer().getCreditDelay()).isBefore(LocalDate.now())).max(Comparator.comparing(Credit::getDateCredit));
		
		if (lateCreditOpt.isPresent()) {
			Credit credit = lateCreditOpt.get();
			CreditOffer offer = credit.getCreditOffer();
			
			double totalToRefund = offer.getLimitationCreditAmount() * (1 + offer.getTaxAfterDelay());
			double alreadyRefunded = credit.getAmountRefund();
			double remaining = totalToRefund - alreadyRefunded;
			
			// Le montant à prélever est le MIN de ce qui reste et de ce qui est déposé
			double amountToTake = Math.min(depositDTO.amount(), remaining);
			
			if (amountToTake > 0) {
				// Mise à jour du crédit
				credit.setAmountRefund(alreadyRefunded + amountToTake);
				boolean isFullyRefunded = credit.getAmountRefund() >= totalToRefund;
				credit.setActive(!isFullyRefunded);
				creditRepository.save(credit);
				
				// Enregistrement du refund automatique
				Refund refund = new Refund();
				refund.setDescription("Auto refund triggered by deposit");
				refund.setCredit(credit);
				refund.setMoneyAccountNumber(depositDTO.phoneMAccount());
				refund.setDateRefund(LocalDate.now());
				refund.setTimeRefund(LocalTime.now());
				refund.setAmount(amountToTake);
				refundRepository.save(refund);
				
				historyService.setHistory("REFUND","Auto refund of " + amountToTake + " triggered by deposit", "SUCCESS", client);
				
				// Mettre à jour le montant final qui ira au portefeuille
				amountForWallet -= amountToTake;
			}
		}
		
		// 3. Crédit du wallet avec le montant ajusté
		Wallet wallet = client.getWallet();
		wallet.setAmount(wallet.getAmount() + amountForWallet);
		walletRepository.save(wallet);
		
		// 4. Enregistrement du dépôt
		Deposit deposit = new Deposit();
		deposit.setAmount(depositDTO.amount());
		deposit.setDescription(depositDTO.description());
		deposit.setmAccountNumber(depositDTO.phoneMAccount());
		deposit.setClient(client);
		deposit.setDateDeposit(LocalDate.now());
		deposit.setTimeDeposit(LocalTime.now());
		depositRepository.save(deposit);
		
		historyService.setHistory("DEPOSIT","Deposit of " + depositDTO.amount(), "SUCCESS", client);
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

}