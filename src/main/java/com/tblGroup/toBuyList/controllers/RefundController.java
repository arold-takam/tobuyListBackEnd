package com.tblGroup.toBuyList.controllers;


import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.Refund;
import com.tblGroup.toBuyList.services.RefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/refund")
public class RefundController {
	private final RefundService refundService;
	
	public RefundController(RefundService refundService) {
		this.refundService = refundService;
	}
	
	
//  REFUND MANAGEMENT-----------------------------------------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/makeRefundByWallet/{creditID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> makeRefundByWallet(@PathVariable int creditID, @RequestBody RefundRequestByWalletDTO requestByWalletDTO){
		try {
			refundService.makeRefundByWallet(creditID, requestByWalletDTO);
			
			return new ResponseEntity <>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(path = "/makeRefundByMoneyAccount/{creditID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> makeRefundByMoneyAccount(@PathVariable int creditID, @RequestBody RefundRequestByMoneyAccountDTO refundRequestByMoneyAccountDTO){
		try {
			refundService.makeRefundByMoneyAccount(creditID, refundRequestByMoneyAccountDTO);
			
			return new ResponseEntity <>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
//	GETTING MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@GetMapping(path = "/get/{refundID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Refund> getRefundByID(@PathVariable int refundID){
		try {
			Refund refund = refundService.getRefundByID(refundID);
			
			return new ResponseEntity<>(refund, HttpStatus.OK);
		}catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/all/byClient/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Refund>> getAllRefundsByClientID(@PathVariable int clientID){
		
		return new ResponseEntity<>(refundService.getAllRefundsByClientID(clientID), HttpStatus.OK);
		
	}
	
	
	@GetMapping(path = "/get/all/byDate", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Refund>> getAllRefundsByDate(@RequestParam LocalDate dateRefund){
		
		return new ResponseEntity<>(refundService.getAllRefundsByDate(dateRefund), HttpStatus.OK);
		
	}
	
	
	@GetMapping(path = "/get/all", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Refund>> getAllRefundsByDate(){
		
		return new ResponseEntity<>(refundService.getAllRefunds(), HttpStatus.OK);
		
	}
	
}
