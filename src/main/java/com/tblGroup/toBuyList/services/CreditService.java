package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.CreditRequest1DTO;
import com.tblGroup.toBuyList.dto.CreditRequest2DTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.CreditRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CreditService {
	private final CreditRepository creditRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	
	private final ClientService clientService;
	private final CreditOfferService creditOfferService;
	
	public CreditService(CreditRepository creditRepository, ClientService clientService, CreditOfferService creditOfferService, MoneyAccountService moneyAccountService, MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository, WalletRepository walletRepository) {
		this.creditRepository = creditRepository;
		this.clientService = clientService;
		this.creditOfferService = creditOfferService;
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
		this.walletRepository = walletRepository;
	}
	
	//	CREDIT MANAGEMENT------------------------------------------------------------------------------------------------------------------------------------------------------
	public void makeCreditToMoneyAccount(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest1DTO creditRequest1DTO) {
		
		Client client = clientService.getClientById(clientSenderID);
		
		if (creditRepository.findByClient(client).isPresent()) {
			throw new IllegalArgumentException("Client already has a credit");
		}
		
		Wallet walletSenderr = client.getWallet();
		
		if (walletSenderr == null) {
			throw new IllegalArgumentException("This client has no wallet yet");
		}
		
		MoneyAccount moneyAccountReceiver = moneyAccountRepository.findByPhone(creditRequest1DTO.receiverAccountPhone());
		
		if (creditOfferTitle == null) {
			throw new IllegalArgumentException("This credit offer not found");
		}
		
		CreditOffer creditOffer = creditOfferService.getCreditOfferByTitle(creditOfferTitle);
		
		Credit credit = new Credit();
		
		if (moneyAccountReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			moneyAccountReceiver.setAmount(moneyAccountReceiver.getAmount() + creditAmount);
			
			moneyAccountRepository.save(moneyAccountReceiver);
		} else {
			throw new IllegalArgumentException("This client has no money account to receive the credit");
		}
		
		
		
		credit.setReceiverAccountID(moneyAccountReceiver.getId());
		credit.setDescription(creditRequest1DTO.description());
		credit.setDateCredit(LocalDate.now());
		credit.setTimeCredit(LocalTime.now());
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		
		creditRepository.save(credit);
		
	}
	
	public void makeCreditToWallet(int clientSenderID, TitleCreditOffer creditOfferTitle, CreditRequest2DTO creditRequest2DTO) {
		
		Client client = clientService.getClientById(clientSenderID);
		
		if (creditRepository.findByClient(client).isPresent()) {
			throw new IllegalArgumentException("Client already has a credit");
		}
		
		Wallet walletSenderr = client.getWallet();
		
		if (walletSenderr == null) {
			throw new IllegalArgumentException("This client has no wallet yet");
		}
		
		Wallet walletReceiver = walletRepository.findByWalletNumber(creditRequest2DTO.walletReceiverNumber());
		
		if (creditOfferTitle == null) {
			throw new IllegalArgumentException("This credit offer not found");
		}
		
		CreditOffer creditOffer = creditOfferService.getCreditOfferByTitle(creditOfferTitle);
		
		Credit credit = new Credit();
		
		if (walletReceiver != null){
			double creditAmount = creditOffer.getLimitationCreditAmount();
			walletReceiver.setAmount(walletReceiver.getAmount() + creditAmount);
			
			walletRepository.save(walletReceiver);
		} else {
			throw new IllegalArgumentException("This client has no wallet to receive the credit");
		}
		
		
		credit.setWalletReceiverID(walletReceiver.getId());
		
		credit.setDescription(creditRequest2DTO.description());
		credit.setDateCredit(LocalDate.now());
		credit.setTimeCredit(LocalTime.now());
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		
		creditRepository.save(credit);
		
	}
	
//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	public Credit getCreditByClientID(int clientID) {
		Client client = clientService.getClientById(clientID);
		
		if (!creditRepository.findByClient(client).isPresent()) {
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
		List<Credit> listCredits = creditRepository.findAll();
		
		return listCredits;
	}
}
