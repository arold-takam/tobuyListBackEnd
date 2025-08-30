package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import com.tblGroup.toBuyList.repositories.*;
import com.tblGroup.toBuyList.repositories.HistoryRepository;
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
           setHistory(""+typeTransfer, "Transfer of "+transferDTO.amount()+ " to "+transferDTO.phone(), "FAILED", client);
             throw new Exception("amount is incorrect");
        }

        if(receiverAccount != null){

            Transfer transfer = new Transfer(
                    transferDTO.amount(),
                    transferDTO.description(),
                    null,
                    transferDTO.phone(),
                    new Date(System.currentTimeMillis()),
                    typeTransfer,
                    client
            );

            receiverAccount.setAmount(receiverAccount.getAmount() + transferDTO.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO.amount());


            setHistory(""+typeTransfer, "Transfer of "+transferDTO.amount()+ " to "+transferDTO.phone(), "SUCCESS", client);

            walletRepository.save(wallet);
            moneyAccountRepository.save(receiverAccount);
            transferRepository.save(transfer);

        }else{
            throw new IllegalArgumentException("Account not found");
        }

    }

    public void makeATransferToAWallet(int clientId, TransferDTO2 transferDTO2, TypeTransfer typeTransfer) throws Exception {

        Client client = clientRepository.findById(clientId).orElseThrow(()-> new IllegalArgumentException("client not found"));

        Wallet wallet = client.getWallet();

        Wallet walletReceiver = walletRepository.findByWalletNumber(transferDTO2.walletNumber());

        if(walletReceiver == null){
            setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
            throw new IllegalArgumentException("This wallet account not found");
        }

        if(!walletReceiver.getWalletNumber().equals(wallet.getWalletNumber())){
            if(transferDTO2.amount() < 0){
                setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
                throw new Exception("Invalid amount");
            }
            if(transferDTO2.amount() > wallet.getAmount()){
                setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
                throw new Exception("amount is insufficient");
            }

            walletReceiver.setAmount(walletReceiver.getAmount() + transferDTO2.amount());
            wallet.setAmount(wallet.getAmount() - transferDTO2.amount());
            walletRepository.save(walletReceiver);
            walletRepository.save(wallet);

            Transfer transfer = new Transfer(
                    transferDTO2.amount(),
                    transferDTO2.description(),
                    null,
                    transferDTO2.walletNumber(),
                    new Date(System.currentTimeMillis()),
                    typeTransfer,
                    client
            );

            transferRepository.save(transfer);


            setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "SUCCESS", client);


        }else{
            setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
            throw new Exception("This transaction is forbidden ");
        }

    }

    private void setHistory(String action, String description, String status, Client client){
        History history = new History(action, description, new Date(System.currentTimeMillis()), status, client);

        historyRepository.save(history);
    }

}
