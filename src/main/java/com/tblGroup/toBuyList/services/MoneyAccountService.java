package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.AmountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountResponseDTO;
import com.tblGroup.toBuyList.dto.PasswordDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MoneyAccountService {
	private final MoneyAccountRepository moneyAccountRepository;
	private final ClientRepository clientRepository;

	public MoneyAccountService(MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository) {
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
	}
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------MoneyAccountManagement------------------------------------------------
	public MoneyAccount createAccount(int clientID, MoneyAccountDTO moneyAccount){
		Optional<Client>optionalClient= clientRepository.findById(clientID);


		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with this id not found");
		}

		Client client = optionalClient.get();
		MoneyAccount moneyAccountAdded = new MoneyAccount();
		moneyAccountAdded.setName(moneyAccount.name());
		moneyAccountAdded.setPhone(moneyAccount.phone());
		moneyAccountAdded.setPassword(moneyAccount.password());
		moneyAccountAdded.setClient(client);


		return moneyAccountRepository.save(moneyAccountAdded);
	}
	
	public MoneyAccountResponseDTO getAccountByID(int clientID, int mAccountID){
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("Money account with ID: " + mAccountID + " not found for client ID: " + clientID + ".");
			}
			
			Hibernate.initialize(moneyAccount.getClient());
			
			return new MoneyAccountResponseDTO(
				moneyAccount.getId(),
				moneyAccount.getName(),
				moneyAccount.getPhone(),
				moneyAccount.getPassword(),
				moneyAccount.getAmount(),
				clientID
			);
		}
		
	public List<MoneyAccountResponseDTO>getAllAccounts(int clientID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			List<MoneyAccount>moneyAccountList = moneyAccountRepository.findAllByClientId(clientID);
			
			List<MoneyAccountResponseDTO>moneyAccountResponseDTOList = new ArrayList<>();
			
			for (MoneyAccount moneyAccount: moneyAccountList){
				moneyAccountResponseDTOList.add(
					new MoneyAccountResponseDTO(
						moneyAccount.getId(),
						moneyAccount.getName(),
						moneyAccount.getPhone(),
						moneyAccount.getPassword(),
						moneyAccount.getAmount(),
						clientID
					)
				);
			}
			
                         return moneyAccountResponseDTOList;
		}
		
	public MoneyAccount updateAccount(int clientID, int mAccountID,  PasswordDTO passwordDTO){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccountFound = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccountFound != null){
				moneyAccountFound.setPassword(passwordDTO.password());
				
				moneyAccountRepository.save(moneyAccountFound);
				
				return moneyAccountFound;
			}
			
			throw new IllegalArgumentException("No money account found at this ID.");
		}
		
	public Boolean deleteAccount(int clientID, int mAccountID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("This client has no account at this ID, You could create him.");
			}
			
			moneyAccountRepository.delete(moneyAccount);
			
			return true;
	}
	
	
//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	
	public MoneyAccount makeDeposit(int clientID, AmountDTO amountDTO, int mAccountID) throws Exception {
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		
		
		if (moneyAccount != null) {
			if (amountDTO.amount() <= 0){
				throw new Exception("This amount is invalid, please try again  with amount up to 0 ");
			}
			moneyAccount.setAmount(moneyAccount.getAmount() + amountDTO.amount());
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
	
	public MoneyAccount makeRetrieve(int clientID, AmountDTO amountDTO, int mAccountID) throws Exception {
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		if (moneyAccount != null){
			if (amountDTO.amount() <= 0 || amountDTO.amount() > moneyAccount.getAmount()){
				throw new Exception("This amount is invalid, please try again  with amount up to 0 & down to: "+moneyAccount.getAmount());
			}
			
			moneyAccount.setAmount(moneyAccount.getAmount() - amountDTO.amount());
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
}
