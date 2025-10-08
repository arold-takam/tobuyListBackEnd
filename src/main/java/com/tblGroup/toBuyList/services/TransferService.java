package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class TransferService {
    private final TransferRepository transferRepository;
    private final ClientService clientService;
    private final MoneyAccountRepository moneyAccountRepository;
    private final WalletRepository walletRepository;
    private final HistoryService historyService;
    private final PasswordEncoder passwordEncoder;
    
    public TransferService(TransferRepository transferRepository, ClientService clientService, MoneyAccountRepository moneyAccountRepository, WalletRepository walletRepository, HistoryService historyService, PasswordEncoder passwordEncoder) {
        this.transferRepository = transferRepository;
        this.clientService = clientService;
        this.moneyAccountRepository = moneyAccountRepository;
        this.walletRepository = walletRepository;
        this.historyService = historyService;
        this.passwordEncoder = passwordEncoder;
    }
    
//    -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void makeATransferToAnAccount(int clientId, TransferDTO transferDTO, TypeTransfer typeTransfer, String password) throws Exception{

        Client client = clientService.getClientById(clientId);
        boolean isValid = passwordEncoder.matches(password, client.getPassword());
        if(!isValid){
            throw new IllegalArgumentException("Wrong password");
        }

        MoneyAccount receiverAccount = moneyAccountRepository.findByPhone(transferDTO.phone());

        Wallet wallet = client.getWallet();

       if(transferDTO.amount() <=0 || transferDTO.amount() > wallet.getAmount()){
           historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO.amount()+ " to "+transferDTO.phone(), "FAILED", client);
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


            historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO.amount()+ " to "+transferDTO.phone(), "SUCCESS", client);

            walletRepository.save(wallet);
            moneyAccountRepository.save(receiverAccount);
            transferRepository.save(transfer);

        }else{
            throw new IllegalArgumentException("Account not found");
        }

    }

    public void makeATransferToAWallet(int clientId, TransferDTO2 transferDTO2, TypeTransfer typeTransfer, String password) throws Exception {

        Client client = clientService.getClientById(clientId);
        
        boolean isValid = passwordEncoder.matches(password, client.getPassword());
        if(!isValid){
            throw new IllegalArgumentException("Wrong password");
        }

        Wallet wallet = client.getWallet();

        Wallet walletReceiver = walletRepository.findByWalletNumber(transferDTO2.walletNumber());

        if(walletReceiver == null){
            historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
            throw new IllegalArgumentException("This wallet account not found");
        }

        if(!walletReceiver.getWalletNumber().equals(wallet.getWalletNumber())){
            if(transferDTO2.amount() < 0){
                historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
                throw new Exception("Invalid amount");
            }
            if(transferDTO2.amount() > wallet.getAmount()){
                historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
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


            historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "SUCCESS", client);


        }else{
            historyService.setHistory(""+typeTransfer, "Transfer of "+transferDTO2.amount()+ " to "+transferDTO2.walletNumber(), "FAILED", client);
            throw new Exception("This transaction is forbidden ");
        }

    }

}
