package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
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

        Client client = clientRepository.findById(clientId).orElseThrow(()-> new Exception("client not found"));

        MoneyAccount receiverAccount = moneyAccountRepository.findByPhone(transferDTO.phone());

        Wallet wallet = client.getWallet();

       if(transferDTO.amount() <=0 || transferDTO.amount() > wallet.getAmount()){
             throw new Exception("amount is incorrect");
        }

        if(receiverAccount != null){

            Transfer transfer = new Transfer();
            transfer.setAmount(transferDTO.amount());
            transfer.setDescription(transferDTO.description());
            transfer.setReceiverAccountNumber(transferDTO.phone());
            transfer.setWalletNumber(null);
            transfer.setDateTransfer(new Date(System.currentTimeMillis()));
            transfer.setTypeTransfer(transferDTO.typeTransfer());
            transfer.setClient(client);

            receiverAccount.setAmount(receiverAccount.getAmount() + transferDTO.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO.amount());

            walletRepository.save(wallet);
            moneyAccountRepository.save(receiverAccount);
            transferRepository.save(transfer);

        }else{
            throw new IllegalArgumentException("Account not found");
        }


    }

    public void makeATransferToAWallet(int clientId, TransferDTO2 transferDTO2) throws Exception {

        Client client = clientRepository.findById(clientId).orElseThrow(()-> new IllegalArgumentException("client not found"));

        Wallet wallet = client.getWallet();

        Wallet walletReceiver = walletRepository.findByWalletNumber(transferDTO2.walletNumber());

        if(walletReceiver == null){
            throw new IllegalArgumentException("This wallet account not found");
        }

        if(walletReceiver != wallet){
            if(transferDTO2.amount() < 0){
                throw new Exception("Invalid amount");
            }
            if(transferDTO2.amount() > wallet.getAmount()){
                throw new Exception("amount is insufficient");
            }

            walletReceiver.setAmount(walletReceiver.getAmount() + transferDTO2.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO2.amount());
            walletRepository.save(walletReceiver);
            walletRepository.save(wallet);

            Transfer transfer = new Transfer();
            transfer.setAmount(transferDTO2.amount());
            transfer.setDescription(transferDTO2.description());
            transfer.setReceiverAccountNumber(null);
            transfer.setWalletNumber(transferDTO2.walletNumber());
            transfer.setDateTransfer(new Date(System.currentTimeMillis()));
            transfer.setTypeTransfer(transferDTO2.typeTransfer());
            transfer.setClient(client);
            transferRepository.save(transfer);

        }else{
            throw new Exception("This transaction is forbidden ");
        }

    }

}
