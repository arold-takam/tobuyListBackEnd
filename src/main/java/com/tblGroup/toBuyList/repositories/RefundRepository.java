package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefundRepository extends JpaRepository<Refund, Integer> {

}
