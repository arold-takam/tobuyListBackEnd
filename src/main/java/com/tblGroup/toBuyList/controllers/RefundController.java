package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.Refund;
import com.tblGroup.toBuyList.services.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/refund", produces = APPLICATION_JSON_VALUE)
public class RefundController {
	private static final Logger log = LoggerFactory.getLogger(RefundController.class);
	
	private final RefundService refundService;
	
	public RefundController(RefundService refundService) {
		this.refundService = refundService;
    }
	
	// --- REFUND CREATION ----------------------------------------------------------------
	
	@PostMapping(path = "/wallet/{creditID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> makeRefundByWallet(@PathVariable int creditID,  @RequestParam int clientId, @RequestBody RefundRequestByWalletDTO request) {
		return handle(() -> {
			refundService.makeRefundByWallet(creditID, clientId,  request);
			return new ResponseEntity<>(CREATED);
		});
	}
	
	@PostMapping(path = "/moneyAccount/{creditID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> makeRefundByMoneyAccount(@PathVariable int creditID, @RequestParam  int clientId, @RequestBody RefundRequestByMoneyAccountDTO request) {
		return handle(() -> {
			refundService.makeRefundByMoneyAccount(creditID, clientId, request);
			return new ResponseEntity<>(CREATED);
		});
	}
	
	// --- REFUND RETRIEVAL ----------------------------------------------------------------
	
	@GetMapping(path = "/{refundID}")
	public ResponseEntity<Refund> getRefundByID(@PathVariable int refundID) {
		return handle(() -> new ResponseEntity<>(refundService.getRefundByID(refundID), OK));
	}
	
	@GetMapping(path = "/client/{clientID}")
	@PreAuthorize("@clientService.authentification(#clientID)")
	public ResponseEntity<List<Refund>> getAllRefundsByClientID(@PathVariable int clientID) {

		return ResponseEntity.ok(refundService.getAllRefundsByClientID(clientID));
	}
	
	@GetMapping(path = "/date")
	public ResponseEntity<List<Refund>> getAllRefundsByDate(@RequestParam LocalDate dateRefund) {
		return ResponseEntity.ok(refundService.getAllRefundsByDate(dateRefund));
	}
	
	@GetMapping
	public ResponseEntity<List<Refund>> getAllRefunds() {
		return ResponseEntity.ok(refundService.getAllRefunds());
	}
	
	// --- ERROR HANDLING UTILITY ----------------------------------------------------------
	
	private <T> ResponseEntity<T> handle(SupplierWithException<ResponseEntity<T>> supplier) {
		try {
			return supplier.get();
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(NOT_FOUND);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(BAD_REQUEST);
		}
	}
	
	@FunctionalInterface
	private interface SupplierWithException<T> {
		T get() throws Exception;
	}
}
