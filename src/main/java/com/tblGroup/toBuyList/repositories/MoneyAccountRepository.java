package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.MoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MoneyAccountRepository extends JpaRepository<MoneyAccount, Integer> {

	MoneyAccount findByClient_IdAndId(int clientID, int mAccountID);
	
	List<MoneyAccount>findAllByClientId(int clientID);

    MoneyAccount findByPhone(String phone);
}
