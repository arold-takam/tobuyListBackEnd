package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		
		if (!credit.isActive()){
			throw new IllegalArgumentException("This credit is already refund.");
		}
		
		Wallet wallet = credit.getClient().getWallet();
		
		if (wallet == null){
			throw new IllegalArgumentException("This wallet doesn't exist");
		}
		
		Client client = credit.getClient();
		
		if (client == null){
			throw new IllegalArgumentException("This client doesn't exist");
		}
		
		CreditOffer creditOffer = credit.getCreditOffer();
		if (creditOffer == null){
			throw new IllegalArgumentException("This credit offer doesnt exist in the system");
		}
		
		double amountCredited = creditOffer.getLimitationCreditAmount();
		double amountToRefund = amountCredited * (1 + creditOffer.getTaxAfterDelay());
		
		double amountRefund = credit.getAmountRefund();
		double difference = amountToRefund - amountRefund;
		if (difference == 0){
			credit.setActive(false);
			creditRepository.save(credit);
			
			throw new IllegalArgumentException("This credit is already closed, you are clean.");
		}
		
		if (request.amount() <= 0 || request.amount() > difference){
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}
		
		if (wallet.getAmount() < request.amount()) {
			throw new IllegalArgumentException("Insufficient wallet balance for this refund.");
		}
		wallet.setAmount(wallet.getAmount() - request.amount());
		walletRepository.save(wallet);
		
		
		credit.setAmountRefund(credit.getAmountRefund() + request.amount());
		
		Refund refund = new Refund();
		refund.setDescription(request.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(" ");
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(request.amount());
		
		if (request.amount() == difference){
			credit.setActive(false);
			
			refund.setEnded(true);
		}else {
			refund.setEnded(false);
		}
		creditRepository.save(credit);
		
		refundRepository.save(refund);
		
	}
	
	@Transactional
	public void makeRefundByMoneyAccount(int creditID, RefundRequestByMoneyAccountDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		if (!credit.isActive()){
			throw new IllegalArgumentException("This credit is already refund.");
		}
		
		Client client = credit.getClient();
		
		if (client == null){
			throw new IllegalArgumentException("This client doesn't exist");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(request.moneyAccountNumber());
		if (moneyAccount == null){
			throw new IllegalArgumentException("This moneyAccount doesn't exist");
		}
		
		CreditOffer creditOffer = credit.getCreditOffer();
		if (creditOffer == null){
			throw new IllegalArgumentException("This credit offer doesnt exist in the system");
		}
		
		double amountCredited = creditOffer.getLimitationCreditAmount();
		double amountToRefund = amountCredited * (1 + creditOffer.getTaxAfterDelay());
		
		double amountRefund = credit.getAmountRefund();
		double difference = amountToRefund - amountRefund;
		if (difference == 0){
			credit.setActive(false);
			creditRepository.save(credit);
			
			throw new IllegalArgumentException("This credit is already closed, you are clean.");
		}
		
		if (request.amount() <= 0 || request.amount() > difference){
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}
		
		if (moneyAccount.getAmount() < request.amount()) {
			throw new IllegalArgumentException("Insufficient moneyAccount balance for this refund.");
		}
		moneyAccount.setAmount(moneyAccount.getAmount() - request.amount());
		moneyAccountRepository.save(moneyAccount);
		
		
		credit.setAmountRefund(credit.getAmountRefund() + request.amount());
		
		Refund refund = new Refund();
		refund.setDescription(request.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(" ");
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(request.amount());
		
		if (request.amount() == difference){
			credit.setActive(false);
			
			refund.setEnded(true);
		}else {
			refund.setEnded(false);
		}
		creditRepository.save(credit);
		
		refundRepository.save(refund);
		
	}
	
	@Scheduled(fixedRate = 86400) // S'exécute toutes les 24 heures
	@Transactional
	public void autoRefundLoading() {
		// 1. Trouver le crédit le plus récent qui est actif et en retard
		Optional<Credit> latestLateCredit = creditRepository.findAll().stream()
			.filter(c -> c.isActive() && c.getDateCredit().plusDays(c.getCreditOffer().getCreditDelay()).isBefore(LocalDate.now()))
			.sorted(Comparator.comparing(Credit::getDateCredit).reversed())
			.findFirst();
		
		if (latestLateCredit.isEmpty()) {
			// Aucun crédit en retard, rien à faire
			return;
		}
		
		Credit credit = latestLateCredit.get();
		
		Wallet wallet = credit.getClient().getWallet();
		
		// 2. Calculer le montant restant à rembourser
		double totalDue = credit.getCreditOffer().getLimitationCreditAmount() * (1 + credit.getCreditOffer().getTaxAfterDelay());
		double remainingDue = totalDue - credit.getAmountRefund();
		
		// 3. Déterminer le montant à prélever
		double amountToTake = Math.min(wallet.getAmount(), remainingDue);
		
		// Si rien ne peut être prélevé, ne rien faire
		if (amountToTake <= 0) {
			return;
		}
		
		// 4. Exécuter le prélèvement
		wallet.setAmount(wallet.getAmount() - amountToTake);
		walletRepository.save(wallet);
		
		// 5. Mettre à jour le crédit
		credit.setAmountRefund(credit.getAmountRefund() + amountToTake);
		boolean isFullyRefunded = credit.getAmountRefund() >= totalDue;
		credit.setActive(!isFullyRefunded);
		creditRepository.save(credit);
		
		// 6. Enregistrer le remboursement automatique
		Refund refund = new Refund();
		refund.setDescription("Prélèvement automatique en retard");
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(" ");
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(amountToTake);
		refund.setEnded(isFullyRefunded);
		refundRepository.save(refund);
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