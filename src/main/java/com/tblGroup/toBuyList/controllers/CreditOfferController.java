package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.CreditOfferRequestDTO;
import com.tblGroup.toBuyList.dto.CreditOfferResponseDTO;
import com.tblGroup.toBuyList.models.CreditOffer;
import com.tblGroup.toBuyList.services.CreditOfferServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/creditOffer")
public class CreditOfferController {
	private CreditOfferServices creditOfferServices;
	
	public CreditOfferController(CreditOfferServices creditOfferServices) {
		this.creditOfferServices = creditOfferServices;
	}

//	CREDIT OFFER MANAGEMENT--------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<CreditOffer> createOffer(@RequestBody CreditOfferRequestDTO creditOfferRequestDTO){
		try {
			CreditOffer creditOffer = creditOfferServices.createOffer(creditOfferRequestDTO);
			
			return new ResponseEntity<>(creditOffer, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/{offerID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CreditOfferResponseDTO>getOffer(@PathVariable int offerID){
		try {
			CreditOfferResponseDTO creditOfferResponseDTO = creditOfferServices.getOffer(offerID);
			
			return new ResponseEntity<>(creditOfferResponseDTO, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CreditOfferResponseDTO>>getAllOffers(){
		try {
			List<CreditOfferResponseDTO> creditOfferResponseDTOList = creditOfferServices.getAllOffers();
			
			return new ResponseEntity<>(creditOfferResponseDTOList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping(path = "/update/{offerID}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CreditOffer>updateOffer(@PathVariable int offerID, @RequestBody CreditOfferRequestDTO creditOfferRequestDTO){
		try {
			CreditOffer creditOffer = creditOfferServices.UpdateOffer(offerID, creditOfferRequestDTO);
			
			return new ResponseEntity<>(creditOffer, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/{offerID}")
	public ResponseEntity<Void>deleteOffer(@PathVariable int offerID){
		try {
			boolean delete = creditOfferServices.deleteOffer(offerID);
			
			if (delete){
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
