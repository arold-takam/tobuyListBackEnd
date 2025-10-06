package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Integer> {

	Optional<Credit> findByClient(Client client);
	
	List<Credit> findAllByDateCredit(LocalDate dateCredit);
	
	List<Credit> findAllByClient(Client client);
	
	Credit findByClient_Id(int clientID);
}
