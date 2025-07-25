package com.tblGroup.toBuyList.repositories;

import com.tblGroup.toBuyList.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByClient_Id(int clientId);

    boolean existsByWalletNumber(String walletNumber);
}
