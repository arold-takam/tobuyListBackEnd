package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.CreditRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.RefundRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
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
	private final ClientService clientService;
	
	public RefundService(RefundRepository refundRepository, CreditRepository creditRepository, WalletRepository walletRepository, MoneyAccountRepository moneyAccountRepository, ClientService clientService) {
		this.refundRepository = refundRepository;
		this.creditRepository = creditRepository;
		this.walletRepository = walletRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientService = clientService;
	}


//  REFUND MANAGEMENT-----------------------------------------------------------------------------------------------------------------------------------------------

	public void makeRefundByWallet(int creditID, RefundRequestByWalletDTO requestByWalletDTO) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		CreditOffer offer = credit.getCreditOffer();
		LocalDate dateCredit = credit.getDateCredit();
		LocalDate dateRefund = LocalDate.now();
		
		long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
		int creditDelay = offer.getCreditDelay();
		
		if (realDelay <= creditDelay) {
			refundPromptlyByWallet(credit, requestByWalletDTO);
		} else {
			throw new IllegalArgumentException("This refund is not possible, you have a penaty for your credit. A tax will be applied.");
		}
	}
	
	public void makeRefundByMoneyAccount(int creditID, RefundRequestByMoneyAccountDTO refundRequestByMoneyAccountDTO) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		CreditOffer offer = credit.getCreditOffer();
		LocalDate dateCredit = credit.getDateCredit();
		LocalDate dateRefund = LocalDate.now();
		
		long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
		int creditDelay = offer.getCreditDelay();
		
		if (realDelay <= creditDelay) {
			refundPromptlyByMoneyAccount(credit, refundRequestByMoneyAccountDTO);
		} else {
			throw new IllegalArgumentException("This refund is not possible, you have a penaty for your credit. A tax will be applied.");
		}
	}


//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public Refund getRefundByID(int refundID) {
		
		return refundRepository.findById(refundID).orElseThrow(()-> new IllegalArgumentException("This refund does not exist"));
	}
	
	public List<Refund> getAllRefundsByClientID(int clientID) {
		Client client = clientService.getClientById(clientID);
		
		return refundRepository.findAllByCredit_Client(client);
	}
	
	public List<Refund>getAllRefundsByDate(LocalDate dateRefund) {
		
		return refundRepository.findAllByDateRefund(dateRefund);
	}
	
	public List<Refund> getAllRefunds() {
		
		return refundRepository.findAll();
	}


//REFUND LOGIC-----------------------------------------------------------------------------------------------------------------------------------
	
	private void refundPromptlyByWallet(Credit credit, RefundRequestByWalletDTO dto) {
		double amountToRefund = dto.amount();
		double totalAfterRefund = credit.getAmountRefund() + amountToRefund;
		double creditLimit = credit.getCreditOffer().getLimitationCreditAmount();
		
		if (totalAfterRefund > creditLimit) {
			throw new IllegalArgumentException("Refund exceeds the credit limit.");
		}

		Wallet wallet = credit.getClient().getWallet();
		
		// Déduction du compte
		if (wallet != null) {
			if (wallet.getAmount() < amountToRefund) {
				throw new IllegalArgumentException("Insufficient wallet balance.");
			}
			
			LocalDate dateCredit = credit.getDateCredit();
			LocalDate dateRefund = LocalDate.now();
			int creditDelay = credit.getCreditOffer().getCreditDelay();
			
			long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
			
			if ((creditDelay - realDelay) <= 0){
				double penalty = amountToRefund  *  credit.getCreditOffer().getTaxAfterDelay() * realDelay;
				
				wallet.setAmount(wallet.getAmount() - penalty);
				walletRepository.save(wallet);
				
				credit.setAmountRefund(credit.getAmountRefund() + penalty);
				creditRepository.save(credit);
				
				// Création du remboursement
				Refund refund = new Refund();
				
				refund.setDescription(dto.description());
				refund.setCredit(credit);
				refund.setMoneyAccountNumber(" ");
				refund.setDateRefund(LocalDate.now());
				refund.setTimeRefund(LocalTime.now());
				refund.setAmount(penalty);
				refund.setEnded(totalAfterRefund + penalty == creditLimit);
				
				refundRepository.save(refund);
				
				// Affichage du statut
				double remainingAmount = creditLimit - totalAfterRefund + penalty;
				long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), credit.getDateCredit().plusDays(credit.getCreditOffer().getCreditDelay()));
				
				System.out.println("📊 Montant restant à rembourser : " + remainingAmount + " FCFA");
				System.out.println("⏳ Délai restant : " + remainingDays + " jours");
				
				return;
			}
			
			wallet.setAmount(wallet.getAmount() - amountToRefund);
			walletRepository.save(wallet);
		} else {
			throw new IllegalArgumentException("Wallet not found for this client.");
		}
		
		// Mise à jour du crédit
		credit.setAmountRefund(totalAfterRefund);
		creditRepository.save(credit);
		
		// Création du remboursement
		Refund refund = new Refund();
		
		refund.setDescription(dto.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(" ");
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(amountToRefund);
		refund.setEnded(totalAfterRefund == creditLimit);
		
		refundRepository.save(refund);
		
		// Affichage du statut
		double remainingAmount = creditLimit - totalAfterRefund;
		long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), credit.getDateCredit().plusDays(credit.getCreditOffer().getCreditDelay()));
		
		System.out.println("📊 Montant restant à rembourser : " + remainingAmount + " FCFA");
		System.out.println("⏳ Délai restant : " + remainingDays + " jours");
		
		if (refund.closesCredit()) {
			credit.setActive(false);
			creditRepository.save(credit);
			
			
			System.out.println("✅ Crédit entièrement remboursé.");
			// Tu peux ici déclencher une notification ou archiver le crédit
		} else {
			System.out.println("Refund successfully processed.");
		}
	}
	
	
	private void refundPromptlyByMoneyAccount(Credit credit, RefundRequestByMoneyAccountDTO refundRequestByMoneyAccountDTO) {
		double amountToRefund = refundRequestByMoneyAccountDTO.amount();
		double totalAfterRefund = credit.getAmountRefund() + amountToRefund;
		double creditLimit = credit.getCreditOffer().getLimitationCreditAmount();
		
		if (totalAfterRefund > creditLimit) {
			throw new IllegalArgumentException("Refund exceeds the credit limit.");
		}
		
		MoneyAccount account = moneyAccountRepository.findByPhone(refundRequestByMoneyAccountDTO.moneyAccountNumber());
		
		if (account == null) {
			throw new  IllegalArgumentException("This money account does not exist");
		}
		
		// Déduction du compte
		if (account.getAmount() < amountToRefund) {
			throw new IllegalArgumentException("Insufficient account balance.");
		}
		
		
		
		LocalDate dateCredit = credit.getDateCredit();
		LocalDate dateRefund = LocalDate.now();
		int creditDelay = credit.getCreditOffer().getCreditDelay();
		
		long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
		
		if ((creditDelay - realDelay) <= 0){
			double penalty = amountToRefund  *  credit.getCreditOffer().getTaxAfterDelay() * realDelay;
			
			account.setAmount(account.getAmount() - penalty);
			moneyAccountRepository.save(account);
			
			credit.setAmountRefund(credit.getAmountRefund() + penalty);
			creditRepository.save(credit);
			
			// Création du remboursement
			Refund refund = new Refund();
			
			refund.setDescription(refundRequestByMoneyAccountDTO.description());
			refund.setCredit(credit);
			refund.setMoneyAccountNumber(" ");
			refund.setDateRefund(LocalDate.now());
			refund.setTimeRefund(LocalTime.now());
			refund.setAmount(penalty);
			refund.setEnded(totalAfterRefund + penalty == creditLimit);
			
			refundRepository.save(refund);
			
			// Affichage du statut
			double remainingAmount = creditLimit - totalAfterRefund + penalty;
			long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), credit.getDateCredit().plusDays(credit.getCreditOffer().getCreditDelay()));
			
			System.out.println("📊 Montant restant à rembourser : " + remainingAmount + " FCFA");
			System.out.println("⏳ Délai restant : " + remainingDays + " jours");
			
			return;
		}
		
		
		
		account.setAmount(account.getAmount() - amountToRefund);
		moneyAccountRepository.save(account);
		
		// Mise à jour du crédit
		credit.setAmountRefund(totalAfterRefund);
		creditRepository.save(credit);
		
		// Création du remboursement
		Refund refund = new Refund();
		refund.setDescription(refundRequestByMoneyAccountDTO.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(account.getPhone());
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(amountToRefund);
		refund.setEnded(totalAfterRefund == creditLimit);
		
		refundRepository.save(refund);
		
		// Affichage du statut
		double remainingAmount = creditLimit - totalAfterRefund;
		long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), credit.getDateCredit().plusDays(credit.getCreditOffer().getCreditDelay()));
		
		System.out.println("📊 Montant restant à rembourser : " + remainingAmount + " FCFA");
		System.out.println("⏳ Délai restant : " + remainingDays + " jours");
		
		if (refund.closesCredit()) {
			credit.setActive(false);
			creditRepository.save(credit);
			
			System.out.println("✅ Crédit entièrement remboursé.");
			// Tu peux ici déclencher une notification ou archiver le crédit
		} else {
			System.out.println("Refund successfully processed.");
		}
	}
	
}
