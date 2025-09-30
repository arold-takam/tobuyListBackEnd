package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    List<Transfer> findAllByClient(Client client);

    void deleteByClient_id(int id);
}
