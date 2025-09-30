package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.CreditOfferRequestDTO;
import com.tblGroup.toBuyList.models.CreditOffer;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import com.tblGroup.toBuyList.repositories.CreditOfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CreditOfferService {
	private final CreditOfferRepository creditOfferRepository;
	
	public CreditOfferService(CreditOfferRepository creditOfferRepository) {
		this.creditOfferRepository = creditOfferRepository;
	}
	
	//	CREDIT OFFER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------
	public void createCreditOffer(TitleCreditOffer titleCreditOffer, CreditOfferRequestDTO creditOfferRequestDTO) {
		if (creditOfferRequestDTO == null) {
			throw new IllegalArgumentException("Credit offer request cannot be null.");
		}
		
		CreditOffer creditOffer = new CreditOffer();
		
		creditOffer.setTitleCreditOffer(titleCreditOffer);
		creditOffer.setLimitationCreditAmount(creditOfferRequestDTO.limitationCreditAmount());
		creditOffer.setCreditDelay(creditOfferRequestDTO.creditDelay());
		creditOffer.setTaxAfterDelay(creditOfferRequestDTO.taxAfterDelay());
		
		creditOfferRepository.save(creditOffer);
	}
	
	public CreditOffer getCreditOfferByTitle(TitleCreditOffer titleCreditOffer) {
		CreditOffer creditOffer = creditOfferRepository.findByTitleCreditOffer(titleCreditOffer);
		
		if (creditOffer == null) {
			throw new IllegalArgumentException("Credit offer with title " + titleCreditOffer + " not found.");
		}
		
		return creditOffer;
	}
	
	public List<CreditOffer> getAllCreditOffers() {
                return creditOfferRepository.findAll();
	}
	
	public void updateCreditOffer(int creditOfferID, TitleCreditOffer newTitleCreditOffer, CreditOfferRequestDTO creditOfferRequestDTO) {
		Optional<CreditOffer> creditOfferOptional = creditOfferRepository.findById(creditOfferID);
		
		if (creditOfferOptional.isEmpty()) {
			throw new IllegalArgumentException("Credit offer with id " + creditOfferID + " not found.");
		}
		
		CreditOffer creditOffer = creditOfferOptional.get();
		
		creditOffer.setTitleCreditOffer(newTitleCreditOffer);
		creditOffer.setLimitationCreditAmount(creditOfferRequestDTO.limitationCreditAmount());
		creditOffer.setCreditDelay(creditOfferRequestDTO.creditDelay());
		creditOffer.setTaxAfterDelay(creditOfferRequestDTO.taxAfterDelay());
		
		creditOfferRepository.save(creditOffer);
	}

	@Transactional
	public boolean deleteCreditOffer(TitleCreditOffer titleCreditOffer) {
		CreditOffer creditOffer = creditOfferRepository.findByTitleCreditOffer(titleCreditOffer);
		
		if (creditOffer == null) {
			throw new IllegalArgumentException("Credit offer with title " + titleCreditOffer + " not found.");
		}
		
		creditOfferRepository.delete(creditOffer);
		
		return true;
	}
	
}
