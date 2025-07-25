package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClientService {
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	
	public ClientService(ClientRepository clientRepository, WalletRepository walletRepository) {
		this.clientRepository = clientRepository;
        this.walletRepository = walletRepository;
    }
	
	
//	------------------------------------------------------------------------------------------------------------------
//	---------------------------------CLIENT MANAGEMENT----------------------------------------------
	public Client createClient(Client client){
		Wallet wallet = new Wallet();
		Client clientSaved = clientRepository.save(client);
		wallet.setClient(clientSaved);
		wallet.setAmount(500.00);
		wallet.setWalletNumber(autoGenerateAWalletNumber());
		walletRepository.save(wallet);
		return clientSaved;
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
	
	public void deleteClient(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		Wallet wallet = walletRepository.findByClient_Id(id);
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}

		walletRepository.deleteById(wallet.getId());
		clientRepository.deleteById(id);

	}

	private String autoGenerateAWalletNumber(){
		String walletNumber;
		do {
			walletNumber = String.format("%06d", new Random().nextInt(1000000));
		} while (walletRepository.existsByWalletNumber(walletNumber));

			return walletNumber;
		}


}
