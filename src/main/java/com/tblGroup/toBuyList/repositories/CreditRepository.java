package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CreditRepository extends JpaRepository<Credit, Integer> {

}
