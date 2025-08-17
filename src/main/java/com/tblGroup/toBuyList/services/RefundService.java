package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RefundService {
	private final RefundRepository refundRepository;
	private final CreditRepository creditRepository;
	private final WalletRepository walletRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final ClientRepository clientRepository;
	
	// Removed ClientService dependency here as it's no longer used in the main logic
	
	public RefundService(RefundRepository refundRepository, CreditRepository creditRepository, WalletRepository walletRepository, MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository) {
		this.refundRepository = refundRepository;
		this.creditRepository = creditRepository;
		this.walletRepository = walletRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
	}
	
	// --- REFUND MANAGEMENT ---
	@Transactional
	public void makeRefundByWallet(int creditID, RefundRequestByWalletDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		Wallet wallet = credit.getClient().getWallet();
		
		// La logique de remboursement unifiée est appelée ici
		processRefund(credit, request.amount(), request.description(), wallet, null);
	}
	
	@Transactional
	public void makeRefundByMoneyAccount(int creditID, RefundRequestByMoneyAccountDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(request.moneyAccountNumber());
		if (moneyAccount == null) {
			throw new IllegalArgumentException("This money account does not exist");
		}
		
		// La logique de remboursement unifiée est appelée ici
		processRefund(credit, request.amount(), request.description(), null, moneyAccount);
	}
	
	// --- PRIVATE HELPER METHODS ---
	private void processRefund(Credit credit, double initialAmount, String description, Wallet wallet, MoneyAccount moneyAccount) {
		// Calcul de la pénalité si le remboursement est en retard
		double totalAmountToDebit = initialAmount;
		LocalDate dateCredit = credit.getDateCredit();
		LocalDate dateRefund = LocalDate.now();
		long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
		int creditDelay = credit.getCreditOffer().getCreditDelay();
		
		// Vérifie si le crédit est en retard
		if (realDelay > creditDelay) {
			double penalty = initialAmount * credit.getCreditOffer().getTaxAfterDelay() * realDelay;
			totalAmountToDebit += penalty;
			description += " (avec pénalité)";
		}
		
		// Vérification du solde et déduction du compte
		if (wallet != null) {
			if (wallet.getAmount() < totalAmountToDebit) {
				throw new IllegalArgumentException("Insufficient wallet balance.");
			}
			wallet.setAmount(wallet.getAmount() - totalAmountToDebit);
			walletRepository.save(wallet);
		} else if (moneyAccount != null) {
			if (moneyAccount.getAmount() < totalAmountToDebit) {
				throw new IllegalArgumentException("Insufficient money account balance.");
			}
			moneyAccount.setAmount(moneyAccount.getAmount() - totalAmountToDebit);
			moneyAccountRepository.save(moneyAccount);
		} else {
			throw new IllegalArgumentException("No valid account provided for refund.");
		}
		
		// Mise à jour du crédit
		double creditLimit = credit.getCreditOffer().getLimitationCreditAmount();
		double totalAfterRefund = credit.getAmountRefund() + totalAmountToDebit;
		
		if (totalAfterRefund > creditLimit) {
			throw new IllegalArgumentException("Refund exceeds the credit limit.");
		}
		
		credit.setAmountRefund(totalAfterRefund);
		credit.setActive(totalAfterRefund < creditLimit);
		creditRepository.save(credit);
		
		// Création et sauvegarde du remboursement
		Refund refund = new Refund();
		refund.setDescription(description);
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(moneyAccount != null ? moneyAccount.getPhone() : null);
		refund.setDateRefund(dateRefund);
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(totalAmountToDebit);
		refund.setEnded(totalAfterRefund >= creditLimit);
		
		refundRepository.save(refund);
		
		System.out.println("📊 Montant restant à rembourser : " + (creditLimit - totalAfterRefund) + " FCFA");
		if (refund.isEnded()) {
			System.out.println("✅ Crédit entièrement remboursé.");
		} else {
			System.out.println("Refund successfully processed.");
		}
	}
	
	// --- GETTING MANAGEMENT (unchanged) ---
	public Refund getRefundByID(int refundID) {
		return refundRepository.findById(refundID).orElseThrow(() -> new IllegalArgumentException("This refund does not exist"));
	}
	
	public List<Refund> getAllRefundsByClientID(int clientID) {
		Client client = clientRepository.findById(clientID).orElseThrow(() -> new IllegalArgumentException("This client does not exist"));
		
		return refundRepository.findAllByCredit_Client(client);
	}
	
	public List<Refund> getAllRefundsByDate(LocalDate dateRefund) {
		return refundRepository.findAllByDateRefund(dateRefund);
	}
	
	public List<Refund> getAllRefunds() {
		return refundRepository.findAll();
	}
}