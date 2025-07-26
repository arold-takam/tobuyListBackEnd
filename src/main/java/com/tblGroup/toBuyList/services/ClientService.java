package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Transfer;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.TransferRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClientService {
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final TransferRepository transferRepository;

	public ClientService(ClientRepository clientRepository, WalletRepository walletRepository, TransferRepository transferRepository) {
		this.clientRepository = clientRepository;
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
    }
	
	
//	------------------------------------------------------------------------------------------------------------------
//	---------------------------------CLIENT MANAGEMENT----------------------------------------------
	public Client createClient(ClientDTO client){
		Wallet wallet = new Wallet();
		Client clientSaved = new Client();
		clientSaved.setName(client.name());
		clientSaved.setMail(client.mail());
		clientSaved.setPassword(client.password());
		wallet.setWalletNumber(autoGenerateAWalletNumber());
		walletRepository.save(wallet);
		clientSaved.setWallet(wallet);
		return clientRepository.save(clientSaved);
	}
	
	public Client getClientById(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}
		
		return optionalClient.get();
	}
	
	public List<Client>getAllClients(){
		return clientRepository.findAll();
	}
	
	public Client updateClient(int id, ClientDTO newClient) throws Exception {
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found");
		}
		
		Client existingClient = optionalClient.get();
		
		if (newClient != null){
			existingClient.setName(newClient.name());
			existingClient.setMail(newClient.mail());
			existingClient.setPassword(newClient.password());
			
			return clientRepository.save(existingClient);
		}
		
		throw  new Exception("Invalid client info, try gain.");
	}

	@Transactional
	public void deleteClient(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);

		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}

		transferRepository.deleteByClient_id(id);
		clientRepository.deleteById(id);
		walletRepository.deleteById(optionalClient.get().getWallet().getId());

	}

	private String autoGenerateAWalletNumber(){
		String walletNumber;
		do {
			walletNumber = String.format("%06d", new Random().nextInt(1000000));
		} while (walletRepository.existsByWalletNumber(walletNumber));

		return walletNumber;
	}

	//	----------------------------------------------WALLET MANAGEMENT-------------------------------------------------------------------------------------------------

	public Wallet getWallet(int clientID){
		Optional<Client>optionalClient = clientRepository.findById(clientID);

		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("No client found at the ID: "+clientID);
		}

		Client client = optionalClient.get();

		if ( client.getWallet() == null){
			throw new IllegalArgumentException("This client has already a wallet, with a balance of: "+client.getWallet().getAmount());
		}

		return client.getWallet();
	}

}
