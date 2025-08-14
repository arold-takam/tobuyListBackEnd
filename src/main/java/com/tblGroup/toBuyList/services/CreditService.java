package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.CreditRequest1DTO;
import com.tblGroup.toBuyList.dto.CreditRequest2DTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class CreditService {
	private final CreditRepository creditRepository;
    private final MoneyAccountRepository moneyAccountRepository;
    private final WalletRepository walletRepository;
	
	private final ClientService clientService;
	private final CreditOfferService creditOfferService;
	private final HistoryRepository historyRepository;
	private final RefundService refundService;
	
	public CreditService(CreditRepository creditRepository, ClientService clientService, CreditOfferService creditOfferService, MoneyAccountRepository moneyAccountRepository, WalletRepository walletRepository, HistoryRepository historyRepository, RefundService refundService) {
		this.creditRepository = creditRepository;
		this.clientService = clientService;
		this.creditOfferService = creditOfferService;
        this.moneyAccountRepository = moneyAccountRepository;
        this.walletRepository = walletRepository;
        this.historyRepository = historyRepository;
		this.refundService = refundService;
	}
	
	//	CREDIT MANAGEMENT------------------------------------------------------------------------------------------------------------------------------------------------------
	public void makeCreditToMoneyAccount(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest1DTO creditRequest1DTO) {
		
		Client client = clientService.getClientById(clientSenderID);
		
		if (creditRepository.findByClient(client).isPresent()) {
			setHistory("Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			
			throw new IllegalArgumentException("Client already has a credit");
		}
		
		MoneyAccount moneyAccountReceiver = moneyAccountRepository.findByPhone(creditRequest1DTO.receiverAccountPhone());
		
		if (creditOfferTitle == null) {
			throw new IllegalArgumentException("This credit offer not found");
		}
		
		CreditOffer creditOffer = creditOfferService.getCreditOfferByTitle(creditOfferTitle);
		

		
		if (moneyAccountReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			moneyAccountReceiver.setAmount(moneyAccountReceiver.getAmount() + creditAmount);
			setHistory("Subscription to the "+creditOfferTitle+" credit","SUCCESS",client);
			moneyAccountRepository.save(moneyAccountReceiver);
		} else {
			setHistory("Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			throw new IllegalArgumentException("This client has no money account to receive the credit");
		}


		Credit credit = new Credit();
		credit.setReceiverAccountID(moneyAccountReceiver.getId());
		credit.setDescription(creditRequest1DTO.description());
		credit.setDateCredit(LocalDate.now());
		credit.setTimeCredit(LocalTime.now());
		credit.setAmountRefund(0);
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		
		creditRepository.save(credit);
		
	}
	
	public void makeCreditToWallet(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest2DTO creditRequest2DTO) {
		
		Client client = clientService.getClientById(clientSenderID);
		
		if (creditRepository.findByClient(client).isPresent()) {
			setHistory("Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			
			throw new IllegalArgumentException("Client already has a credit");
		}

		Wallet walletSender = client.getWallet();
		
		if (walletSender == null) {
			throw new IllegalArgumentException("This client has no wallet yet");
		}
		
		Wallet walletReceiver = walletRepository.findByWalletNumber(creditRequest2DTO.walletReceiverNumber());
		
		if (creditOfferTitle == null) {
			throw new IllegalArgumentException("This credit offer not found");
		}
		
		CreditOffer creditOffer = creditOfferService.getCreditOfferByTitle(creditOfferTitle);
		

		if (walletReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			walletReceiver.setAmount(walletReceiver.getAmount() + creditAmount);
			setHistory("Subscription to the "+creditOfferTitle+" credit","SUCCESS",client);
			walletRepository.save(walletReceiver);
		} else {
			setHistory("Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			throw new IllegalArgumentException("This client has no wallet to receive the credit");
		}

		Credit credit = new Credit();
		
		credit.setWalletReceiverID(walletReceiver.getId());
		credit.setDescription(creditRequest2DTO.description());
		credit.setDateCredit(LocalDate.now());
		credit.setTimeCredit(LocalTime.now());
		credit.setAmountRefund(0);
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		
		creditRepository.save(credit);
		
	}
	
//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	public Credit getCreditByClientID(int clientID) {
		Client client = clientService.getClientById(clientID);
		
		if (creditRepository.findByClient(client).isEmpty()) {
			throw new IllegalArgumentException("This client has no credit yet");
		}
		
		return creditRepository.findByClient(client).get();
	}
	
	public List<Credit> getCreditByDateCredit(LocalDate dateCredit) {
		if (creditRepository.findAllByDateCredit(dateCredit).isEmpty()) {
			throw new IllegalArgumentException("No credit  found for this date");
		}
		
		return creditRepository.findAllByDateCredit(dateCredit);
	}
	
	public List<Credit> getAllCredits() {

        return creditRepository.findAll();
	}

	private void setHistory(String description, String status, Client client){
		History history = new History("CREDIT", description, new Date(System.currentTimeMillis()), status, client);

		historyRepository.save(history);
	}
}
