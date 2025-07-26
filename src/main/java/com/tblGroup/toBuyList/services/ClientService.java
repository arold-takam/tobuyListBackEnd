package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.dto.ClientResponseDTO;
import com.tblGroup.toBuyList.dto.WalletResponseDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
		wallet.setWalletNumber(autoGenerateAWalletNumber());
		
		walletRepository.save(wallet);
		
		client.setWallet(wallet);
		
		 return clientRepository.save(client);
	}
	
	public ClientDTO getClientById(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}
		
		Client client = optionalClient.get();
		
		return new ClientDTO(
			client.getId(),
			client.getName(),
			client.getMail(),
			client.getPassword()
		);
	}
	
	public List<ClientDTO>getAllClients(){
		List<Client> clientList = clientRepository.findAll();
		
		List<ClientDTO>clientDTOList = new ArrayList<>();
		
		for (Client client: clientList){
			clientDTOList.add(
				new ClientDTO(
					client.getId(),
					client.getName(),
					client.getMail(),
					client.getPassword()
				)
			);
		}
		
		return clientDTOList;
	}
	
	public ClientDTO updateClient(int id, ClientDTO newClient) throws Exception {
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found");
		}
		
		Client existingClient = optionalClient.get();
		
		if (newClient != null){
			existingClient.setName(newClient.name());
			existingClient.setMail(newClient.mail());
			existingClient.setPassword(newClient.password());
			
			clientRepository.save(existingClient);
			
			return new ClientDTO(
				existingClient.getId(),
				existingClient.getName(),
				existingClient.getMail(),
				existingClient.getPassword()
			);
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
	
//	------------------------------------------------------------------
	
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
