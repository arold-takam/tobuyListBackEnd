package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    private final ClientRepository clientRepository;
    private final MoneyAccountRepository moneyAccountRepository;
    private final WalletRepository walletRepository;
    private final HistoryRepository historyRepository;

    public TransferService(TransferRepository transferRepository, ClientRepository clientRepository, MoneyAccountRepository moneyAccountRepository, WalletRepository walletRepository, HistoryRepository historyRepository) {
        this.transferRepository = transferRepository;
        this.clientRepository = clientRepository;
        this.moneyAccountRepository = moneyAccountRepository;
        this.walletRepository = walletRepository;
        this.historyRepository = historyRepository;
    }

    public void makeATransferToAnAccount(int clientId, TransferDTO transferDTO, TypeTransfer typeTransfer) throws Exception{

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
            transfer.setTypeTransfer(typeTransfer);
            transfer.setClient(client);

            receiverAccount.setAmount(receiverAccount.getAmount() + transferDTO.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO.amount());

            History history= new History();
            history.setAction(""+typeTransfer);
            history.setDescription("Transfer of "+transferDTO.amount()+ " to "+transferDTO.phone());
            history.setDateAction(new Date(System.currentTimeMillis()));
            history.setClient(client);

            walletRepository.save(wallet);
            moneyAccountRepository.save(receiverAccount);
            transferRepository.save(transfer);
            historyRepository.save(history);

        }else{
            throw new IllegalArgumentException("Account not found");
        }


    }

    public void makeATransferToAWallet(int clientId, TransferDTO2 transferDTO2, TypeTransfer typeTransfer) throws Exception {

        Client client = clientRepository.findById(clientId).orElseThrow(()-> new IllegalArgumentException("client not found"));

        Wallet wallet = client.getWallet();

        Wallet walletReceiver = walletRepository.findByWalletNumber(transferDTO2.walletNumber());

        if(walletReceiver == null){
            throw new IllegalArgumentException("This wallet account not found");
        }

        if(!walletReceiver.getWalletNumber().equals(wallet.getWalletNumber())){
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
            transfer.setTypeTransfer(typeTransfer);
            transfer.setClient(client);
            transferRepository.save(transfer);

            History history= new History();
            history.setAction(""+typeTransfer);
            history.setDescription("Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber());
            history.setDateAction(new Date(System.currentTimeMillis()));
            history.setClient(client);
            historyRepository.save(history);


        }else{
            throw new Exception("This transaction is forbidden ");
        }

    }

}
