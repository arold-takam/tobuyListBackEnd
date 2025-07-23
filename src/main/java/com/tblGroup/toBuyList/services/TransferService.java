package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.models.Transfer;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.TransferRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    private final ClientRepository clientRepository;
    private final MoneyAccountRepository moneyAccountRepository;
    private final WalletRepository walletRepository;

    public TransferService(TransferRepository transferRepository, ClientRepository clientRepository, MoneyAccountRepository moneyAccountRepository, WalletRepository walletRepository) {
        this.transferRepository = transferRepository;
        this.clientRepository = clientRepository;
        this.moneyAccountRepository = moneyAccountRepository;
        this.walletRepository = walletRepository;
    }

    public Transfer makeATransfer(int clientId, int MoneyAccountId ) throws Exception{
        MoneyAccount moneyAccount = moneyAccountRepository.findById(MoneyAccountId).orElseThrow(()-> new Exception("Account not found"));
        return null;
    }


}
