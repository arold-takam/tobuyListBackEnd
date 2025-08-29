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
import java.util.Date;
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
	private final HistoryRepository historyRepository;
	
	// Removed ClientService dependency here as it's no longer used in the main logic
	
	public RefundService(RefundRepository refundRepository, CreditRepository creditRepository, WalletRepository walletRepository, MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository, HistoryRepository historyRepository) {
		this.refundRepository = refundRepository;
		this.creditRepository = creditRepository;
		this.walletRepository = walletRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
        this.historyRepository = historyRepository;
    }
	
	// --- REFUND MANAGEMENT ---
	@Transactional
	public void makeRefundByWallet(int creditID, RefundRequestByWalletDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));


		CreditOffer creditOffer = credit.getCreditOffer();
		Client client = credit.getClient();
		Wallet wallet = client.getWallet();
		double amountCredited = creditOffer.getLimitationCreditAmount();
		double amountToRefund = amountCredited * (1 + creditOffer.getTaxAfterDelay());
		double amountRefund = credit.getAmountRefund();
		int difference = (int)(amountToRefund - amountRefund);

		if (!credit.isActive()){
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This credit is already refund.");
		}

		if (request.amount() <= 0 || request.amount() > difference){
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}

		if (wallet.getAmount() < request.amount()) {
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("Insufficient wallet balance for this refund.");
		}

		if ((int)request.amount() == difference){
			credit.setActive(false);
		}

		credit.setAmountRefund(credit.getAmountRefund() + request.amount());
		creditRepository.save(credit);

		wallet.setAmount(wallet.getAmount() - request.amount());
		walletRepository.save(wallet);

		Refund refund = new Refund();
		refund.setDescription(request.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(" ");
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(request.amount());

		refundRepository.save(refund);
		setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "SUCCESS", client);

	}
	
	@Transactional
	public void makeRefundByMoneyAccount(int creditID, RefundRequestByMoneyAccountDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));


		CreditOffer creditOffer = credit.getCreditOffer();
        Client client = credit.getClient();

		double amountCredited = creditOffer.getLimitationCreditAmount();
		double amountToRefund = amountCredited * (1 + creditOffer.getTaxAfterDelay());

		double amountRefund = credit.getAmountRefund();
		int difference = (int)(amountToRefund - amountRefund);

		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(request.moneyAccountNumber());
		if(moneyAccount == null){
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This moneyAccount doesn't exist");
		}
		if(!credit.isActive()){
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This credit is already refund.");
		}
		


		if (request.amount() <= 0 || request.amount() > difference){
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}

		if (moneyAccount.getAmount() < request.amount()) {
			setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
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

		if ((int)request.amount() == difference){
			credit.setActive(false);
		}

		creditRepository.save(credit);
		
		refundRepository.save(refund);
		setHistory("Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "SUCCESS", client);

	}


	private void setHistory(String description, String status, Client client) {
		History history = new History("DEPOSIT", description, new Date(System.currentTimeMillis()), status, client);
		historyRepository.save(history);
	}

	@Scheduled(fixedRate = 86400) // S'exécute toutes les 24h
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