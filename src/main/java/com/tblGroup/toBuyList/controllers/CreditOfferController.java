package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.CreditOfferRequestDTO;
import com.tblGroup.toBuyList.models.CreditOffer;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.services.CreditOfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/creditOffer")
public class CreditOfferController {
	private final CreditOfferService creditOfferService;
	
	public CreditOfferController(CreditOfferService creditOfferService) {
		this.creditOfferService = creditOfferService;
	}
	
//	CREDIT OFFER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------

	@PostMapping(path = "/createCreditOffer", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> createCreditOffer(@RequestParam TitleCreditOffer titleCreditOffer, @RequestBody CreditOfferRequestDTO creditOfferRequestDTO) {
		try {
			creditOfferService.createCreditOffer(titleCreditOffer,creditOfferRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/getCreditOfferByTitle", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CreditOffer> getCreditOfferByTitle(@RequestParam TitleCreditOffer titleCreditOffer) {
		try {
			CreditOffer creditOffer = creditOfferService.getCreditOfferByTitle(titleCreditOffer);
			
			return new ResponseEntity<>(creditOffer, HttpStatus.OK);
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/getAllCreditOffer", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CreditOffer>> getAllCreditOffer() {
		try {
			List<CreditOffer> creditOfferList = creditOfferService.getAllCreditOffers();
			
			return new ResponseEntity<>(creditOfferList, HttpStatus.OK);
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping(path = "/update/CreditOffer{creditOfferID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updateCreditOffer(@PathVariable int creditOfferID, @RequestParam TitleCreditOffer titleCreditOffer, @RequestBody CreditOfferRequestDTO creditOfferRequestDTO) {
		try {
			creditOfferService.updateCreditOffer(creditOfferID, titleCreditOffer, creditOfferRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/deleteCreditOffer")
	public ResponseEntity<Void> deleteCreditOffer(@RequestParam TitleCreditOffer titleCreditOffer) {
		try {
			boolean deleted = creditOfferService.deleteCreditOffer(titleCreditOffer);
			
			if (deleted) {
				return new ResponseEntity<>(HttpStatus.OK);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
}
