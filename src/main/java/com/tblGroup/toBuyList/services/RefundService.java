package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.RefundRequestDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TypeRefundAccountPay;
import com.tblGroup.toBuyList.repositories.CreditRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.RefundRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
public class RefundService {
	private final RefundRepository refundRepository;
	private final CreditRepository creditRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final WalletRepository walletRepository;
	
	public RefundService(RefundRepository refundRepository, CreditRepository creditRepository,
	                     MoneyAccountService moneyAccountService, MoneyAccountRepository moneyAccountRepository,
	                     WalletRepository walletRepository) {
		this.refundRepository = refundRepository;
		this.creditRepository = creditRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.walletRepository = walletRepository;
	}
	
//  -----------------------------------------------------------------------------------------------------------------------------------------------
	
	public void makeRefund(RefundRequestDTO refundRequestDTO, TypeRefundAccountPay typeRefundAccountPay) {
		Credit credit = creditRepository.findById(refundRequestDTO.creditID())
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		CreditOffer offer = credit.getCreditOffer();
		LocalDate dateCredit = credit.getDateCredit();
		LocalDate dateRefund = LocalDate.now();
		
		long realDelay = ChronoUnit.DAYS.between(dateCredit, dateRefund);
		int creditDelay = offer.getCreditDelay();
		
		if (realDelay <= creditDelay) {
			refundPromptly(credit, refundRequestDTO, typeRefundAccountPay);
		} else {
			refundWithTax(credit, refundRequestDTO, typeRefundAccountPay, realDelay - creditDelay);
		}
	}

//  -----------------------------------------------------------------------------------------------------------------------------------------------
	
	private void refundPromptly(Credit credit, RefundRequestDTO dto, TypeRefundAccountPay typePay) {
		double amountToRefund = dto.amount();
		double totalAfterRefund = credit.getAmountRefund() + amountToRefund;
		double creditLimit = credit.getCreditOffer().getLimitationCreditAmount();
		
		if (totalAfterRefund > creditLimit) {
			throw new IllegalArgumentException("Refund exceeds the credit limit.");
		}
		
		// Déduction du compte
		if (typePay == TypeRefundAccountPay.Wallet) {
			Wallet wallet = credit.getClient().getWallet();
			if (wallet.getAmount() < amountToRefund) {
				throw new IllegalArgumentException("Insufficient wallet balance.");
			}
			wallet.setAmount(wallet.getAmount() - amountToRefund);
			walletRepository.save(wallet);
		} else {
			MoneyAccount account = moneyAccountRepository.findById(dto.moneyAccountID())
				.orElseThrow(() -> new IllegalArgumentException("This money account does not exist"));
			if (account.getAmount() < amountToRefund) {
				throw new IllegalArgumentException("Insufficient money account balance.");
			}
			account.setAmount(account.getAmount() - amountToRefund);
			moneyAccountRepository.save(account);
		}
		
		// Mise à jour du crédit
		credit.setAmountRefund(totalAfterRefund);
		creditRepository.save(credit);
		
		// Création du remboursement
		Refund refund = new Refund();
		refund.setDescription(dto.description());
		refund.setCredit(credit);
		refund.setMoneyAccount(typePay == TypeRefundAccountPay.Wallet ? null :
			moneyAccountRepository.findById(dto.moneyAccountID()).orElse(null));
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
			System.out.println("✅ Crédit entièrement remboursé.");
			// Tu peux ici déclencher une notification ou archiver le crédit
		} else {
			System.out.println("Refund successfully processed.");
		}
	}
	
	private void refundWithTax(Credit credit, RefundRequestDTO dto, TypeRefundAccountPay typePay, long delayDays) {
		double baseAmount = dto.amount();
		float penaltyRate = credit.getCreditOffer().getTaxAfterDelay(); // ex: 0.05 pour 5%
		double penalty = baseAmount * penaltyRate * delayDays;
		double totalToRefund = baseAmount + penalty;
		
		System.out.println("⚠️ Remboursement en retard. Pénalité appliquée : " + penalty);
		
		// Blocage du client
		credit.getClient().setBlocked(true);
		
		// Prélèvement automatique si Wallet
		if (typePay == TypeRefundAccountPay.Wallet) {
			Wallet wallet = credit.getClient().getWallet();
			double autoDeduct = totalToRefund * 0.80;
			
			if (wallet.getAmount() < autoDeduct) {
				throw new IllegalArgumentException("Solde insuffisant pour prélèvement automatique.");
			}
			
			wallet.setAmount(wallet.getAmount() - autoDeduct);
			walletRepository.save(wallet);
			
			System.out.println("💰 Prélèvement automatique effectué : " + autoDeduct);
			dto = new RefundRequestDTO(dto.description(), dto.creditID(), dto.moneyAccountID(), autoDeduct);
		} else {
			dto = new RefundRequestDTO(dto.description(), dto.creditID(), dto.moneyAccountID(), totalToRefund);
		}
		
		refundPromptly(credit, dto, typePay);
	}
}
