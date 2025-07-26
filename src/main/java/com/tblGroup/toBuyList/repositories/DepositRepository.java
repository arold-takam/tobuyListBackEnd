package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {
	
	Deposit findByClient_IdAndId(int clientID, int depositID);
	
	List<Deposit> findAllByClientId(int clientID);
	
}
