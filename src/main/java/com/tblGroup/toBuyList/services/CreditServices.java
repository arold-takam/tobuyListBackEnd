package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.dto.CreditOfferResponseDTO;
import com.tblGroup.toBuyList.dto.CreditRequestDTO;
import com.tblGroup.toBuyList.dto.CreditResponseDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class CreditServices {
	private final CreditRepository creditRepository;
	
	private final ClientService clientService;
	private final ClientRepository clientRepository;
	private final CreditOfferServices creditOfferServices;
	
	private final WalletRepository walletRepository;
	
	private final MoneyAccountRepository moneyAccountRepository;
	private final MoneyAccountService moneyAccountService;
	private final CreditOfferRepository creditOfferRepository;
	
	public CreditServices(CreditRepository creditRepository, ClientService clientService, ClientRepository clientRepository, CreditOfferServices creditOfferServices, WalletRepository walletRepository, MoneyAccountRepository moneyAccountRepository, MoneyAccountService moneyAccountService, CreditOfferRepository creditOfferRepository) {
		this.creditRepository = creditRepository;
		this.clientService = clientService;
		this.clientRepository = clientRepository;
		this.creditOfferServices = creditOfferServices;
		this.walletRepository = walletRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.moneyAccountService = moneyAccountService;
		this.creditOfferRepository = creditOfferRepository;
	}
	
	//	CREDIT MANAGEMENT-------------------------------------------------------------------------------------------------------

	public Credit makeCredit(int clientID,  CreditRequestDTO creditRequestDTO){
		Optional<Client> optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}
		
		Client client = optionalClient.get();
		
		Wallet walletSender = client.getWallet();
		
		Wallet walletReceiver = walletRepository.findByWalletNumberContainingIgnoreCase(creditRequestDTO.walletReceiverNumber());
		
		if (walletReceiver == null){
			throw new IllegalArgumentException("No wallet found at this Number: "+creditRequestDTO.walletReceiverNumber());
		}
		
		MoneyAccount mAccountReceiver = moneyAccountRepository.findByPhone(creditRequestDTO.mAccountPhone());
		
		if (mAccountReceiver == null && walletReceiver == null){
			throw new IllegalArgumentException("You have to select until one account which receive the credit !");
		}
		
		Optional<CreditOffer> creditOfferOptional = creditOfferRepository.findById(creditRequestDTO.creditOfferID());
		
		if (creditOfferOptional.isEmpty()){
			throw new IllegalArgumentException("No credit offer found at the ID: "+creditRequestDTO.creditOfferID());
		}
		
		CreditOffer creditOffer = creditOfferOptional.get();
		
		String description = creditRequestDTO.description();
		String title = creditRequestDTO.title();
		
		
		Credit credit = new Credit();
		
		
		if (walletReceiver == null && mAccountReceiver !=null){
			mAccountReceiver.setAmount(mAccountReceiver.getAmount() + creditOffer.getLimitationCreditAmount());
			
			credit.setTitle(title);
			credit.setDescription(description);
			credit.setDateCredit(LocalDate.now());
			credit.setTimeCredit(LocalTime.now());
			credit.setCreditOffer(creditOffer);
			credit.setClient(client);
			credit.setReceiverAccount(mAccountReceiver);
			credit.setWalletReceiver(walletReceiver);
			
			return creditRepository.save(credit);
			
		}
		
		if (walletReceiver != null && mAccountReceiver == null){
			mAccountReceiver.setAmount(mAccountReceiver.getAmount() + creditOffer.getLimitationCreditAmount());
			
			credit.setTitle(title);
			credit.setDescription(description);
			credit.setDateCredit(LocalDate.now());
			credit.setTimeCredit(LocalTime.now());
			credit.setCreditOffer(creditOffer);
			credit.setClient(client);
			credit.setReceiverAccount(mAccountReceiver);
			credit.setWalletReceiver(walletReceiver);
			
			return creditRepository.save(credit);
		}
		
		return credit;
	}
	
	public CreditResponseDTO getCredit(int creditID){
		Optional<Credit> optionalCredit = creditRepository.findById(creditID);
		
		if (optionalCredit.isEmpty()){
			throw new IllegalArgumentException("No credit found at this ID: "+creditID);
		}
		
		Credit credit = optionalCredit.get();
		
		return new CreditResponseDTO(
			credit.getId(),
			credit.getTitle(),
			credit.getDescription(),
			credit.getClient().getId(),
			credit.getReceiverAccount().getId(),
			credit.getWalletReceiver().getId(),
			credit.getCreditOffer().getId(),
			credit.getDateCredit(),
			credit.getTimeCredit()
		);
	}
}
