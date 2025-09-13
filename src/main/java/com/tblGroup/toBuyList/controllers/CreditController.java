package com.tblGroup.toBuyList.controllers;


import com.tblGroup.toBuyList.dto.CreditRequest1DTO;
import com.tblGroup.toBuyList.dto.CreditRequest2DTO;
import com.tblGroup.toBuyList.models.Credit;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.services.ClientService;
import com.tblGroup.toBuyList.services.CreditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/credit")
public class CreditController {
	private final CreditService creditService;
	private final ClientService clientService;
	
	public CreditController(CreditService creditService, ClientService clientService) {
		this.creditService = creditService;
        this.clientService = clientService;
    }
	
	
	//	CREDIT MANAGEMENT------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "create/toMoneyAccount/{clientSenderID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> makeCreditToMoneyAccount(@PathVariable int clientSenderID, @RequestParam TitleCreditOffer creditOfferTitle, @RequestBody CreditRequest1DTO creditRequest1DTO){
		try {
			creditService.makeCreditToMoneyAccount(clientSenderID, creditOfferTitle, creditRequest1DTO);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping(path = "create/toWallet/{clientSenderID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> makeCreditToWallet(@PathVariable int clientSenderID, @RequestParam TitleCreditOffer creditOfferTitle, @RequestBody CreditRequest2DTO creditRequest2DTO){
		try {
			clientService.authentification(clientSenderID);
			creditService.makeCreditToWallet(clientSenderID, creditOfferTitle, creditRequest2DTO);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	
//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@GetMapping(path = "/get/client/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Credit> getCreditByClientID(@PathVariable int clientID){
		try {
			clientService.authentification(clientID);

			Credit credit = creditService.getCreditByClientID(clientID);
			
			return new ResponseEntity<>(credit, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/date/{date}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Credit>> getCreditByClientID(@PathVariable LocalDate date){
		try {
			List<Credit> creditList = creditService.getCreditByDateCredit(date);
			
			return new ResponseEntity<>(creditList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Credit>> getAllCredit(){
		List<Credit>creditList = creditService.getAllCredits();
		
		return new ResponseEntity<>(creditList, HttpStatus.OK);
	}
}
