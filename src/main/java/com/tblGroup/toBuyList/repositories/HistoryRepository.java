package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {

    List<History> findAllByClient(Client client);
    void deleteAllByClient(Client client);

}
