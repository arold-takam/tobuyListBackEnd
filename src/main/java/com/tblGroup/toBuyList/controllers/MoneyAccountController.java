package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.services.MoneyAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/moneyAccount")
public class MoneyAccountController {
	private MoneyAccountService moneyAccountService;
	
	public MoneyAccountController(MoneyAccountService moneyAccountService) {
		this.moneyAccountService = moneyAccountService;
	}
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------MONEYaCCOUNTmANAGEMENT------------------------------------------------
	@PostMapping(path = "/create/{clientID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount> createAccount(@PathVariable int clientID, @RequestBody MoneyAccount moneyAccount){
		try {
			MoneyAccount moneyAccountCreated = moneyAccountService.createAccount(clientID, moneyAccount);
			
			return new ResponseEntity<>(moneyAccountCreated, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/read/{clientID}/{mAccountID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>getAccount(@PathVariable int clientID, @PathVariable int mAccountID){
		try {
			MoneyAccount moneyAccount = moneyAccountService.getAccountByID(clientID, mAccountID);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/read/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MoneyAccount>>getAllAccount(@PathVariable int clientID){
		try {
			List<MoneyAccount>moneyAccountList = moneyAccountService.getAllAccounts(clientID);
			
			return new ResponseEntity<>(moneyAccountList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping(path = "/update/{clientID}/{mAccountID}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>updateAccount(@PathVariable int clientID, @PathVariable int mAccountID, @RequestBody MoneyAccount newMoneyAccount){
		try {
			MoneyAccount moneyAccount = moneyAccountService.updateAccount(clientID, mAccountID, newMoneyAccount);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping(path = "/delete/{clientID}/{mAccountID}")
	public ResponseEntity<Void>deleteAccount(@PathVariable int clientID, @PathVariable int mAccountID){
		boolean deleted = moneyAccountService.deleteAccount(clientID, mAccountID);
		
		if (deleted){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	
	//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	@PutMapping(path = "/deposit/{clientID}/{mAccountID}",
		produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>makeDeposit(@PathVariable int clientID, @RequestParam double amount, @PathVariable int mAccountID){
		try {
			MoneyAccount moneyAccount = moneyAccountService.makeDeposit(clientID, amount, mAccountID);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping(path = "/retrieve/{clientID}/{mAccountID}",
		produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>makeRetrieve(@PathVariable int clientID, @RequestParam double amount, @PathVariable int mAccountID){
		try {
			MoneyAccount moneyAccount = moneyAccountService.makeRetrieve(clientID, amount, mAccountID);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
