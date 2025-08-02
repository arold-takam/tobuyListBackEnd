package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.AmountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountResponseDTO;
import com.tblGroup.toBuyList.dto.PasswordDTO;
import com.tblGroup.toBuyList.models.Enum.MoneyAccountName;
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
	private final MoneyAccountService moneyAccountService;
	
	public MoneyAccountController(MoneyAccountService moneyAccountService) {
		this.moneyAccountService = moneyAccountService;
	}
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------moneyAccountManagement------------------------------------------------
	@PostMapping(path = "/create/{clientID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount> createAccount(@PathVariable int clientID, @RequestParam MoneyAccountName moneyAccountName,  @RequestBody MoneyAccountDTO moneyAccount){
		try {
			MoneyAccount moneyAccountCreated = moneyAccountService.createAccount(clientID, moneyAccountName, moneyAccount);
			
			return new ResponseEntity<>(moneyAccountCreated, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/read/{mAccountID}/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccountResponseDTO>getAccount(@PathVariable int mAccountID, @PathVariable int clientID){
		try {
			MoneyAccountResponseDTO moneyAccountResponseDTO = moneyAccountService.getAccountByID(clientID, mAccountID);
			
			return new ResponseEntity<>(moneyAccountResponseDTO, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/read/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MoneyAccountResponseDTO>>getAllAccount(@PathVariable int clientID){
			List<MoneyAccountResponseDTO> listMoneyAccountResponseDTO = moneyAccountService.getAllAccounts(clientID);
			
			return new ResponseEntity<>(listMoneyAccountResponseDTO, HttpStatus.OK);
	}
	
	@PutMapping(path = "/update/{mAccountID}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>updateAccount(@RequestParam int clientID, @PathVariable int mAccountID, @RequestBody PasswordDTO password){
		try {
			MoneyAccount moneyAccount = moneyAccountService.updateAccount(clientID, mAccountID, password);
			
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
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	
	//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	@PutMapping(path = "/deposit/{mAccountID}",
		produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>makeDeposit(@RequestParam int clientID, @RequestBody AmountDTO amountDTO, @PathVariable int mAccountID){
		try {
			MoneyAccount moneyAccount = moneyAccountService.makeDeposit(clientID, amountDTO, mAccountID);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping(path = "/retrieve/{mAccountID}",
		produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<MoneyAccount>makeRetrieve(@RequestParam int clientID, @RequestBody AmountDTO amountDTO, @PathVariable int mAccountID){
		try {
			MoneyAccount moneyAccount = moneyAccountService.makeRetrieve(clientID, amountDTO, mAccountID);
			
			return new ResponseEntity<>(moneyAccount, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
