package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.DepositeDTO;
import com.tblGroup.toBuyList.models.Deposit;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.services.DepositSercives;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/deposit")
public class DepositController {
	private final DepositSercives depositSercives;
	
	public DepositController(DepositSercives depositSercives) {
		this.depositSercives = depositSercives;
	}
	
	//	----------------------------------------------WALLET MANAGEMENT-------------------------------------------------------------------------------------------------
	@PostMapping(path = "/create/wallet/{clientID}")
	public ResponseEntity<Wallet>create(@PathVariable int clientID){
		try {
			Wallet wallet = depositSercives.createWallet(clientID);
			
			return new ResponseEntity<>(wallet, HttpStatus.CREATED);
		} catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/wallet/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Wallet>getWallet(@PathVariable int clientID){
		try {
			Wallet wallet = depositSercives.getWallet(clientID);
			
			return new ResponseEntity<>(wallet, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/wallet/{clientID}")
	public ResponseEntity<Void>delete(@PathVariable int clientID){
		try {
			boolean delete = depositSercives.deleteWallet(clientID);
			
			if (delete){
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


//	--------------------------------------------------------------------DEPOSIT MANAGEMENT-----------------------------------------------------------------
	@PostMapping(path = "/create/{clientID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Deposit>create(@PathVariable int clientID, @RequestParam String phoneMAccount, @RequestBody DepositeDTO depositeDTO){
		try {
			Deposit deposit = depositSercives.makeDeposit(clientID, phoneMAccount, depositeDTO);
			
			return new ResponseEntity<>(deposit, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Deposit>get(@PathVariable int clientID, @RequestParam int depositID){
		try {
			Deposit deposit = depositSercives.getDeposit(clientID, depositID);
			
			return new ResponseEntity<>(deposit, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/all/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Deposit>>get(@PathVariable int clientID){
		try {
			List<Deposit> depositList = depositSercives.getAllDeposit(clientID);
			
			return new ResponseEntity<>(depositList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
