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
			refundWithTaxByWallet(credit, requestByWalletDTO, realDelay - creditDelay);
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
			refundWithTaxByMoneyAccount(credit, refundRequestByMoneyAccountDTO, realDelay - creditDelay);
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


//  -----------------------------------------------------------------------------------------------------------------------------------------------
	
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
	
	private void refundWithTaxByWallet(Credit credit, RefundRequestByWalletDTO dto,long delayDays) {
		double baseAmount = dto.amount();
		float penaltyRate = credit.getCreditOffer().getTaxAfterDelay(); // ex: 0.05 pour 5%
		double penalty = baseAmount * penaltyRate * delayDays;
		double totalToRefund = baseAmount + penalty;
		
		System.out.println("⚠️ Remboursement en retard. Pénalité appliquée : " + penalty);
		
		
		Wallet wallet = credit.getClient().getWallet();
		double autoDeduct = totalToRefund * 0.80;
		
		// Déduction du compte
		if (wallet != null) {
			if (wallet.getAmount() < autoDeduct) {
				throw new IllegalArgumentException("Solde insuffisant pour prélèvement automatique.");
			}
			
			wallet.setAmount(wallet.getAmount() - autoDeduct);
			walletRepository.save(wallet);
			
			System.out.println("💰 Prélèvement automatique effectué : " + autoDeduct);
			dto = new RefundRequestByWalletDTO(dto.description(),  autoDeduct);
		} else {
			dto = new RefundRequestByWalletDTO(dto.description(),  totalToRefund);
		}
		
		refundPromptlyByWallet(credit, dto);
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
	
	private void refundWithTaxByMoneyAccount(Credit credit, RefundRequestByMoneyAccountDTO refundRequestByMoneyAccountDTO, long delayDays) {
		double baseAmount = refundRequestByMoneyAccountDTO.amount();
		float penaltyRate = credit.getCreditOffer().getTaxAfterDelay(); // ex: 0.05 pour 5%
		double penalty = baseAmount * penaltyRate * delayDays;
		double totalToRefund = baseAmount + penalty;
		
		System.out.println("⚠️ Remboursement en retard. Pénalité appliquée : " + penalty);
		
		
		MoneyAccount account = moneyAccountRepository.findByPhone(refundRequestByMoneyAccountDTO.moneyAccountNumber());
		
		if (account == null) {
			throw new  IllegalArgumentException("This money account does not exist");
		}
		
		// Déduction du compte
		if (account != null) {
			
			if (account.getAmount() < totalToRefund) {
				throw new IllegalArgumentException("Insufficient account balance.");
			}
			
			account.setAmount(account.getAmount() - totalToRefund);
			moneyAccountRepository.save(account);
			
			System.out.println("💰 Prélèvement automatique effectué : " + totalToRefund);
			
			refundRequestByMoneyAccountDTO = new RefundRequestByMoneyAccountDTO(refundRequestByMoneyAccountDTO.description(), refundRequestByMoneyAccountDTO.moneyAccountNumber(), penalty);
		} else {
			refundRequestByMoneyAccountDTO = new RefundRequestByMoneyAccountDTO(
				refundRequestByMoneyAccountDTO.description(),
				refundRequestByMoneyAccountDTO.moneyAccountNumber(),
				totalToRefund
			);
			
		}
		
		refundPromptlyByMoneyAccount(credit, refundRequestByMoneyAccountDTO);
	}
	
}
