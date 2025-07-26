package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.CreditOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditOfferRepository  extends JpaRepository<CreditOffer, Integer> {

}
