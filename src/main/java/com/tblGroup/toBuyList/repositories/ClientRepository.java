package com.tblGroup.toBuyList.repositories;


import com.tblGroup.toBuyList.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    boolean existsByMail(String mail);

    Optional<Client> findByUsername(String username);
}
