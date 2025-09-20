package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.CreditRequest1DTO;
import com.tblGroup.toBuyList.dto.CreditRequest2DTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class CreditService {
	private final CreditRepository creditRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final WalletRepository walletRepository;

	private final CreditOfferService creditOfferService;
	private final HistoryService historyService;
	private final ClientRepository clientRepository;
	
	public CreditService(CreditRepository creditRepository, CreditOfferService creditOfferService, MoneyAccountRepository moneyAccountRepository, WalletRepository walletRepository, HistoryService historyService, ClientRepository clientRepository) {
		this.creditRepository = creditRepository;
		this.creditOfferService = creditOfferService;
	        this.moneyAccountRepository = moneyAccountRepository;
	        this.walletRepository = walletRepository;
	        this.historyService = historyService;
		this.clientRepository = clientRepository;
	}
	
	//	CREDIT MANAGEMENT------------------------------------------------------------------------------------------------------------------------------------------------------
	public void makeCreditToMoneyAccount(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest1DTO creditRequest1DTO, String password) {
		CreditOffer creditOffer = getCreditOfferFromService(creditOfferTitle);
		
		Client client = getAndValidateClientByID(clientSenderID);
		if(!client.getPassword().equals(password)){
			throw new IllegalArgumentException("Wrong password");
		}
		
		validateLastCredit_client(client, creditOfferTitle);
		
		MoneyAccount moneyAccountReceiver = moneyAccountRepository.findByPhone(creditRequest1DTO.receiverAccountPhone());
		
		if (moneyAccountReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			moneyAccountReceiver.setAmount(moneyAccountReceiver.getAmount() + creditAmount);
			historyService.setHistory("CREDIT","Subscription to the "+creditOfferTitle+" credit","SUCCESS",client);
			moneyAccountRepository.save(moneyAccountReceiver);
		} else {
			historyService.setHistory("CREDIT","Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			throw new IllegalArgumentException("This client has no money account to receive the credit");
		}
		
		buildCredit(client, creditOffer, creditRequest1DTO.description());
	}
	
	public void makeCreditToWallet(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest2DTO creditRequest2DTO, String password) {
		Client client = getAndValidateClientByID(clientSenderID);
		if(!client.getPassword().equals(password)){
			throw new IllegalArgumentException("Wrong password");
		}
		
		CreditOffer creditOffer = getCreditOfferFromService(creditOfferTitle);

		validateLastCredit_client(client, creditOfferTitle);

		Wallet walletReceiver = walletRepository.findByWalletNumber(creditRequest2DTO.walletReceiverNumber());
		
		if (walletReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			walletReceiver.setAmount(walletReceiver.getAmount() + creditAmount);
			historyService.setHistory("CREDIT","Subscription to the "+creditOfferTitle+" credit","SUCCESS",client);
			walletRepository.save(walletReceiver);
		} else {
			historyService.setHistory("CREDIT","Subscription to the "+creditOfferTitle+" credit","FAILED",client);
			throw new IllegalArgumentException("This client has no wallet to receive the credit");
		}

		buildCredit(client, creditOffer, creditRequest2DTO.description());
	}
	
//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	public Credit getCreditByClientID(int clientID) {
		Client client  = getAndValidateClientByID(clientID);
		
		return creditRepository.findByClient_Id(client.getId());
	}
	
	public List<Credit> getCreditByDateCredit(LocalDate dateCredit) {
		return creditRepository.findAllByDateCredit(dateCredit);
	}
	
	public List<Credit> getAllCredits() {
                return creditRepository.findAll();
	}
	
//------------------------UTILITY METHODS---------------------------------------------------------------------------------------------
	private Client getAndValidateClientByID(int clientID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("This client doesnt exist.");
		}
		
		return optionalClient.get();
	}
	
	private void validateLastCredit_client(Client client, TitleCreditOffer creditOfferTitle){
		List<Credit>creditList = creditRepository.findAllByClient(client);
		
		if (!creditList.isEmpty()) {
			Credit lastCredit = creditList.getLast();
			
			if (lastCredit.isActive()) {
				historyService.setHistory("CREDIT","Subscription to the " + creditOfferTitle + " credit", "FAILED", client);
				throw new IllegalArgumentException("Client already has an active credit");
			}
		}
	}
	
	private CreditOffer getCreditOfferFromService(TitleCreditOffer creditOfferTitle){
		if (creditOfferTitle == null) {
			throw new IllegalArgumentException("This credit offer not found");
		}
		
		return creditOfferService.getCreditOfferByTitle(creditOfferTitle);
	}
	
	private void buildCredit(Client client, CreditOffer offer, String description) {
		Credit credit = new Credit();
		
		credit.setDescription(description);
		credit.setDateCredit(LocalDate.now());
		credit.setTimeCredit(LocalTime.now());
		credit.setAmountRefund(0);
		credit.setActive(true);
		credit.setClient(client);
		credit.setCreditOffer(offer);

		creditRepository.save(credit);
	}
	
}
