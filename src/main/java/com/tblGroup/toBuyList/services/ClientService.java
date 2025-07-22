package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.Client;
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
			throw new IllegalArgumentException("Client not found wtih ID: "+id);
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
	
	public boolean deleteClient(int id){
		Optional<Client>optionalClient =  clientRepository.findById(id);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+id+" not found.");
		}
		
		clientRepository.deleteById(id);
		
		return true;
	}

}
