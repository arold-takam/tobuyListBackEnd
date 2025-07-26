package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.CreditOfferRequestDTO;
import com.tblGroup.toBuyList.dto.CreditOfferResponseDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Credit;
import com.tblGroup.toBuyList.models.CreditOffer;
import com.tblGroup.toBuyList.repositories.CreditOfferRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditOfferServices {
	private final CreditOfferRepository creditOfferRepository;
	
	public CreditOfferServices(CreditOfferRepository creditOfferRepository) {
		this.creditOfferRepository = creditOfferRepository;
	}
	
//	CREDIT OFFER MANAGEMENT------------------------------------------------------------------------------------------------------------------------
	public CreditOffer createOffer(CreditOfferRequestDTO creditOfferRequestDTO){
		return testOfferValidity(creditOfferRequestDTO);
	}
	
	public CreditOfferResponseDTO getOffer(int creditOfferID){
		Optional<CreditOffer>creditOfferOptional = creditOfferRepository.findById(creditOfferID);
		
		if (creditOfferOptional.isEmpty()){
			throw new IllegalArgumentException("No credit offer found at the ID: "+creditOfferID);
		}
		
		CreditOffer creditOffer = creditOfferOptional.get();
		
		return new CreditOfferResponseDTO(
			creditOfferID,
			creditOffer.getTitle(),
			creditOffer.getDescription(),
			creditOffer.getLimitationCreditAmount(),
			creditOffer.getCreditDelay(),
			creditOffer.getCreditTax()
		);
	}
	
	public List<CreditOfferResponseDTO> getAllOffers(){
		List<CreditOffer>creditOfferList = creditOfferRepository.findAll();
		
		if (creditOfferList.isEmpty()){
			throw new IllegalArgumentException("No task found yet, you can add them easily.");
		}
		
		List<CreditOfferResponseDTO>creditOfferResponseDTOList = new ArrayList<>();
		
		for (CreditOffer creditOffer: creditOfferList){
			creditOfferResponseDTOList.add(
				new CreditOfferResponseDTO(
					creditOffer.getId(),
					creditOffer.getTitle(),
					creditOffer.getDescription(),
					creditOffer.getLimitationCreditAmount(),
					creditOffer.getCreditDelay(),
					creditOffer.getCreditTax()
				)
			);
		}
		
		return creditOfferResponseDTOList;
	}
	
	public CreditOffer UpdateOffer(int offerId, CreditOfferRequestDTO newOffer){
		Optional<CreditOffer>creditOfferOptional = creditOfferRepository.findById(offerId);
		
		if (creditOfferOptional.isEmpty()){
			throw new IllegalArgumentException("No offer found at the ID: "+offerId);
		}
		
		CreditOffer creditOffer = creditOfferOptional.get();
		
		creditOffer.setTitle(newOffer.title());
		creditOffer.setDescription(newOffer.description());
		creditOffer.setLimitationCreditAmount(newOffer.limitationAmount());
		creditOffer.setCreditDelay(newOffer.creditDelay());
		creditOffer.setCreditTax(newOffer.creditTax());
		
		return creditOfferRepository.save(creditOffer);
	}
	
	public boolean deleteOffer(int offerID){
		Optional<CreditOffer>creditOfferOptional = creditOfferRepository.findById(offerID);
		
		if (creditOfferOptional.isEmpty()){
			throw new IllegalArgumentException("No offer found at this ID: "+offerID);
		}
		
		creditOfferRepository.deleteById(offerID);
		
		return true;
		
	}
	
//	------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private CreditOffer testOfferValidity(CreditOfferRequestDTO creditOfferRequestDTO) {
		if (creditOfferRequestDTO == null){
			throw new IllegalArgumentException("this offer is invalid, try again.");
		}
		
		CreditOffer creditOffer = new CreditOffer();
		
		creditOffer.setTitle(creditOfferRequestDTO.title());
		creditOffer.setDescription(creditOfferRequestDTO.description());
		creditOffer.setLimitationCreditAmount(creditOffer.getLimitationCreditAmount());
		creditOffer.setCreditDelay(creditOfferRequestDTO.creditDelay());
		creditOffer.setCreditTax(creditOfferRequestDTO.creditTax());
		
		return creditOfferRepository.save(creditOffer);
	}
}
