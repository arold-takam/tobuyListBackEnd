package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class RefundService {
	private final RefundRepository refundRepository;
	private final CreditRepository creditRepository;
	private final WalletRepository walletRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final ClientRepository clientRepository;
	private final HistoryService historyService;
	
	// Removed ClientService dependency here as it's no longer used in the main logic
	
	public RefundService(RefundRepository refundRepository, CreditRepository creditRepository, WalletRepository walletRepository, MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository, HistoryService historyService) {
		this.refundRepository = refundRepository;
		this.creditRepository = creditRepository;
		this.walletRepository = walletRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
		this.historyService = historyService;
    }
	
	// --- REFUND MANAGEMENT ---
	@Transactional
	public void makeRefundByWallet(int creditID, int clientId,  RefundRequestByWalletDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));
		
		
		Client client = credit.getClient();
		
		if(client.getId() != clientId){
			throw new IllegalArgumentException("This client is not the owner of this credit");
		}

		CreditOffer creditOffer = credit.getCreditOffer();
		
		Wallet wallet = client.getWallet();
		
		int difference = getDifferenceAmount(creditOffer, credit);

		if (!credit.isActive()){
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This credit is already refund.");
		}

		if (request.amount() <= 0 || request.amount() > difference){
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}

		if (wallet.getAmount() < request.amount()) {
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
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
		historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "SUCCESS", client);

	}
	
	@Transactional
	public void makeRefundByMoneyAccount(int creditID, int clientId, RefundRequestByMoneyAccountDTO request) {
		Credit credit = creditRepository.findById(creditID)
			.orElseThrow(() -> new IllegalArgumentException("This credit does not exist"));

		Client client = credit.getClient();
		
		if(client.getId() != clientId){
			throw new IllegalArgumentException("This client is not the owner of this credit");
		}
		CreditOffer creditOffer = credit.getCreditOffer();

		int difference =getDifferenceAmount(creditOffer, credit);

		MoneyAccount moneyAccount = moneyAccountRepository.findByPhone(request.moneyAccountNumber());
		if(moneyAccount == null){
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This moneyAccount doesn't exist");
		}
		
		if(!credit.isActive()){
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			System.out.println("failed");
			throw new IllegalArgumentException("This credit is already refund.");
		}

		if (request.amount() <= 0 || request.amount() > difference){
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("This amount is invalid, try again.");
		}

		credit.setAmountRefund(credit.getAmountRefund() + request.amount());
		
		if (moneyAccount.getAmount() < request.amount()) {
			historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "FAILED", client);
			throw new IllegalArgumentException("Insufficient moneyAccount balance for this refund.");
		}
		moneyAccount.setAmount(moneyAccount.getAmount() - request.amount());
		moneyAccountRepository.save(moneyAccount);

		Refund refund = new Refund();
		refund.setDescription(request.description());
		refund.setCredit(credit);
		refund.setMoneyAccountNumber(request.moneyAccountNumber());
		refund.setDateRefund(LocalDate.now());
		refund.setTimeRefund(LocalTime.now());
		refund.setAmount(request.amount());

		if ((int)request.amount() == difference){
			credit.setActive(false);
		}

		creditRepository.save(credit);
		
		refundRepository.save(refund);
		historyService.setHistory("REFUND","Refunding the " + creditOffer.getTitleCreditOffer() + " credit", "SUCCESS", client);

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
	
//--------------UTILITY METHODS--------------------------------------------------------------------------------------------------------
	private int getDifferenceAmount(CreditOffer creditOffer, Credit credit){
		double amountCredited = creditOffer.getLimitationCreditAmount();
		double amountToRefund = amountCredited * (1 + creditOffer.getTaxAfterDelay());
		double amountRefund = credit.getAmountRefund();
		
		return  (int)(amountToRefund - amountRefund);
	}

}