package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
	private ClientRepository clientRepository;
	
	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}
	
	
//	------------------------------------------------------------------------------------------------------------------
//	---------------------------------CLIENT MANAGEMENT----------------------------------------------
	public Client createClient(Client client){
		return clientRepository.save(client);
	}
	
	public Client getClientById(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client not found wtih ID: "+id);
		}
		
		return optionalClient.get();
	}
	
	public List<Client>getAllClients(){
		return clientRepository.findAll();
	}
	
	public Client updateClient(int id, Client newClient){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client not found wtih ID: "+id);
		}
		
		Client existingClient = optionalClient.get();
		
		if (newClient != null){
			existingClient.setName(newClient.getName());
			existingClient.setMail(newClient.getMail());
			existingClient.setPassword(newClient.getPassword());
			
			return clientRepository.save(existingClient);
		}
		
		throw new IllegalArgumentException("Invalid client info, try gain.");
	}
	
	public boolean deleteClient(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client not found wtih ID: "+id);
		}
		
		clientRepository.deleteById(id);
		
		return true;
	}

}
