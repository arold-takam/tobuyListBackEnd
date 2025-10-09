package com.tblGroup.toBuyList.controllers;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.dto.LoginRequestDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/client")
public class ClientController {
	private final ClientService clientService;
	
	private final AuthenticationManager authenticationManager;
	
	public ClientController(ClientService clientService, AuthenticationManager authenticationManager) {
		this.clientService = clientService;
		this.authenticationManager = authenticationManager;
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
	
	@PostMapping(path = "/login", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequestDTO.userName(), loginRequestDTO.password()));

			HttpSession session = request.getSession(true);
			session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					new SecurityContextImpl(authentication));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return ResponseEntity.ok("Login successful !");
		}catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
		}catch (Exception e){
//			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error; try again.");
		}
	}
	
	@GetMapping(path = "/get/{clientID}", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("@clientService.authentification(#clientID)")
	public ResponseEntity<Client>getClient(@PathVariable int clientID){
		try {
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
	@PreAuthorize("@clientService.authentification(#clientID)")
	public ResponseEntity<Client>updateClient(@PathVariable int clientID, @RequestBody ClientDTO newClient){
		try {
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
	@PreAuthorize("@clientService.authentification(#clientID)")
	public ResponseEntity<Void>deleteClient(@PathVariable int clientID){
		try {

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
	@PreAuthorize("@clientService.authentification(#clientID)")
	public ResponseEntity<Wallet>getWallet(@PathVariable int clientID){
		try {

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
