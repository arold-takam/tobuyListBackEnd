package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.dto.ClientResponseDTO;
import com.tblGroup.toBuyList.dto.WalletResponseDTO;
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
	public ClientResponseDTO createClient(ClientDTO clientDTO){
		Client client = new Client();
		
		client.setName(clientDTO.name());
		client.setMail(clientDTO.mail());
		client.setPassword(clientDTO.password());
		
		Wallet wallet = new Wallet();
		wallet.setAmount(0.0);
		wallet.setWalletNumber(autoGenerateAWalletNumber());
		
		client.setWallet(wallet);
		wallet.setClient(client);
		
		Client clientSaved = clientRepository.save(client);
		
		WalletResponseDTO walletResponseDTO = new WalletResponseDTO(
			clientSaved.getWallet().getId(),
			clientSaved.getWallet().getAmount(),
			clientSaved.getWallet().getWalletNumber()
		);
		
		return new ClientResponseDTO(
			clientSaved.getId(),
			clientSaved.getName(),
			clientSaved.getMail(),
			clientSaved.getPassword(),
			walletResponseDTO
		);
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
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}
		
		Wallet wallet = optionalClient.get().getWallet();

		walletRepository.deleteById(wallet.getId());
		clientRepository.deleteById(id);

	}
	
	private String autoGenerateAWalletNumber(){
		String walletNumber;
		do {
			walletNumber = String.format("%06d", new Random().nextInt(1000000));
			System.out.println("Generated Wallet Number: '" + walletNumber + "' (Length: " + walletNumber.length() + ")");
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
