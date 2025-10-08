package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.CreditOffer;
import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CreditOfferRepository extends JpaRepository<CreditOffer, Integer> {
	
	CreditOffer findByTitleCreditOffer(TitleCreditOffer titleCreditOffer);
	
	List<CreditOffer> findAllByTitleCreditOffer(TitleCreditOffer titleCreditOffer);
}
