package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.AmountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountResponseDTO;
import com.tblGroup.toBuyList.dto.PasswordDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Enum.MoneyAccountName;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MoneyAccountService {
	private final MoneyAccountRepository moneyAccountRepository;
	private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    public MoneyAccountService(MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------MoneyAccountManagement------------------------------------------------
	public MoneyAccount createAccount(int clientID, MoneyAccountName moneyAccountName,  MoneyAccountDTO moneyAccount){
		Client client = getClientByID(clientID);

		if(moneyAccountRepository.existsByPhone(moneyAccount.phone())){
			throw new IllegalArgumentException("this number phone already exists");
		}

		if(moneyAccount.phone().length() != 9){
			throw new IllegalArgumentException("Invalid number phone");
		}
		
		MoneyAccount moneyAccountAdded = new MoneyAccount();
		moneyAccountAdded.setName(moneyAccountName);
		moneyAccountAdded.setPhone(moneyAccount.phone());
		moneyAccountAdded.setPassword(passwordEncoder.encode(moneyAccount.password()));
		moneyAccountAdded.setClient(client);


		return moneyAccountRepository.save(moneyAccountAdded);
	}
	
	public MoneyAccountResponseDTO getAccountByID(int clientID, int mAccountID, String password){
			MoneyAccount moneyAccount = getMoneyAccountByClient(clientID, mAccountID, password);

			Hibernate.initialize(moneyAccount.getClient());

                return new MoneyAccountResponseDTO(mAccountID, moneyAccount.getName(), moneyAccount.getPhone(), moneyAccount.getPassword(), moneyAccount.getAmount());
	}
		
	public List<MoneyAccountResponseDTO>getAllAccounts(int clientID){
		 getClientByID(clientID);
	
		List<MoneyAccount>listMoneyAccount = moneyAccountRepository.findAllByClientId(clientID);
		
		List<MoneyAccountResponseDTO>listMoneyAccountResponseDTO = new ArrayList<>();
		
		for (MoneyAccount moneyAccount : listMoneyAccount){
			 listMoneyAccountResponseDTO.add(new MoneyAccountResponseDTO(moneyAccount.getId(), moneyAccount.getName(), moneyAccount.getPhone(), moneyAccount.getPassword(), moneyAccount.getAmount()));
		}


                return listMoneyAccountResponseDTO;
	}
		
	public MoneyAccount updateAccount(int clientID, int mAccountID,  PasswordDTO passwordDTO){
		getClientByID(clientID);
		
		MoneyAccount moneyAccountFound = getMoneyAccountByClient(clientID, mAccountID, passwordDTO.oldPassword());
	
		moneyAccountFound.setPassword(passwordDTO.newPassword());
		
		moneyAccountRepository.save(moneyAccountFound);
		
		return moneyAccountFound;
	}
		
	public Boolean deleteAccount(int clientID, int mAccountID, String password){
		getClientByID(clientID);
		
		MoneyAccount moneyAccount = getMoneyAccountByClient(clientID, mAccountID, password);
		
		moneyAccountRepository.delete(moneyAccount);
		
		return true;
	}
	
	
//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	
	public MoneyAccount makeDeposit(int clientID, AmountDTO amountDTO, int mAccountID, String password) throws Exception {
		 getClientByID(clientID);

		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);

		if (moneyAccount == null){
			throw new IllegalArgumentException("Money account with ID: " + mAccountID + " not found for client ID: " + clientID + ".");
		}

		
		if (amountDTO.amount() <= 0){
			throw new Exception("This amount is invalid, please try again  with amount up to 0 ");
		}
		moneyAccount.setAmount(moneyAccount.getAmount() + amountDTO.amount());
		
		moneyAccountRepository.save(moneyAccount);
		
		return moneyAccount;
	}
	
	public MoneyAccount makeRetrieve(int clientID, AmountDTO amountDTO, int mAccountID, String password) throws Exception {
	 getClientByID(clientID);
		
		MoneyAccount moneyAccount = getMoneyAccountByClient(clientID, mAccountID, password);
		
		if (amountDTO.amount() <= 0 || amountDTO.amount() > moneyAccount.getAmount()){
			throw new Exception("This amount is invalid, please try again  with amount up to 0 & down to: "+moneyAccount.getAmount());
		}
		
		moneyAccount.setAmount(moneyAccount.getAmount() - amountDTO.amount());
		
		moneyAccountRepository.save(moneyAccount);
		
		return moneyAccount;
	}
	
//--------------------------UTILITY METHODS-------------------------------------------------------------------------------------------------------
	private Client getClientByID(int clientID){
		Optional<Client>optionalClient= clientRepository.findById(clientID);
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with this id not found");
		}
		
		return optionalClient.get();
	}
	
	private MoneyAccount getMoneyAccountByClient(int clientID, int mAccountID, String password){
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		if (moneyAccount == null){
			throw new IllegalArgumentException("Money account with ID: " + mAccountID + " not found for client ID: " + clientID + ".");
		}
		if(!moneyAccount.getPassword().equals(password)){
			throw new IllegalArgumentException("Wrong password");
		}
		
		return moneyAccount;
	}


}
