package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface RefundRepository extends JpaRepository<Refund, Integer> {
	
	List<Refund>findAllByCredit_Client(Client client);
	
	List<Refund>findAllByDateRefund(LocalDate localDate);
}
