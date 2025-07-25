package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.models.Transfer;
import com.tblGroup.toBuyList.models.Wallet;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import com.tblGroup.toBuyList.repositories.TransferRepository;
import com.tblGroup.toBuyList.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

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

    public void makeATransferToAnAccount(int clientId, TransferDTO transferDTO) throws Exception{
        
        Client client = clientRepository.findById(clientId).orElseThrow(()-> new IllegalArgumentException("\"Client not found at the ID: \"+clientId"));
        
        Wallet wallet = client.getWallet();
        MoneyAccount receiverAccount = moneyAccountRepository.findByPhone(transferDTO.phone());

       if(transferDTO.amount() <=0 || transferDTO.amount() > wallet.getAmount()){
             throw new Exception("amount is incorrect");
        }

        if(receiverAccount != null){

            Transfer transfer = new Transfer();
            transfer.setAmount(transferDTO.amount());
            transfer.setDescription(transferDTO.description());
            transfer.setReceiverAccount(receiverAccount);
            transfer.setWalletReceiver(null);
            transfer.setDateTransfer(new Date(System.currentTimeMillis()));
            transfer.setTypeTransfer(transferDTO.typeTransfer());

            receiverAccount.setAmount(receiverAccount.getAmount() + transferDTO.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO.amount());

            walletRepository.save(wallet);
            moneyAccountRepository.save(receiverAccount);
            transferRepository.save(transfer);
        }else{
            throw new IllegalArgumentException("Account not found");
        }


    }

    public void makeATransferToAWallet(int clientId, TransferDTO transfer){

    }

}
