package com.tblGroup.toBuyList.controllers;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/client")
public class ClientController {
	private final ClientService clientService;
	
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}
	
//	------------------------------------------------------------------------------------------
// -------------------------------------------------------CLIENT MANAGEMENT-----------------------------------------------------------------
	@Operation(security = @SecurityRequirement(name = "noauth"))
	@PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>createClient(@RequestBody ClientDTO client){
		try {
			Client savedClient = clientService.createClient(client);
			
			return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>getClient(@PathVariable int clientID){
		try {
			clientService.authentification(clientID);
			Client foundClient = clientService.getClientById(clientID);
			return new ResponseEntity<>(foundClient, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/get", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllClient(){
		List<Client>clientList = clientService.getAllClients();
		
		return new ResponseEntity<>(clientList, HttpStatus.OK);
	}
	
	@PutMapping(path = "/update/{clientID}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>updateClient(@PathVariable int clientID, @RequestBody ClientDTO newClient){
		try {
			clientService.authentification(clientID);
			Client updatedClient = clientService.updateClient(clientID, newClient);
			return  new ResponseEntity<>(updatedClient, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/{clientID}")
	public ResponseEntity<Void>deleteClient(@PathVariable int clientID){
		try {
			clientService.authentification(clientID);
			clientService.deleteClient(clientID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			System.out.println(e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
	//	----------------------------------------------WALLET MANAGEMENT-------------------------------------------------------------------------------------------------

	@GetMapping(path = "/get/wallet/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Wallet>getWallet(@PathVariable int clientID){
		try {
			clientService.authentification(clientID);

			Wallet wallet = clientService.getWallet(clientID);

			return new ResponseEntity<>(wallet, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
