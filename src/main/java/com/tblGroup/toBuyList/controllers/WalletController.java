package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.DepositeDTO;
import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.Deposit;
import com.tblGroup.toBuyList.services.DepositSercives;
import com.tblGroup.toBuyList.services.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final TransferService transferService;
    private final DepositSercives depositSercives;

    public WalletController(TransferService transferService, DepositSercives depositSercives) {
        this.transferService = transferService;
        this.depositSercives = depositSercives;
    }



//    TRANSFERT MANAGEMENT--------------------------------------------------------------------------------------------------------------------------

    @PostMapping("/transferToAccount/{clientId}")
    public ResponseEntity<?> MakeATransferToAnAccount(@PathVariable int clientId, @RequestBody TransferDTO transfer) {
        try{
            transferService.makeATransferToAnAccount(clientId, transfer);

            return ResponseEntity.ok().build();
        }catch(IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }


    }

    @PostMapping("/transferToWallet/{clientId}")
    public ResponseEntity<?> MakeATransferToWallet(@PathVariable int clientId, @RequestBody TransferDTO2 transfer){
        try{
            transferService.makeATransferToAWallet(clientId, transfer);
            return ResponseEntity.ok().build();
        }catch(IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }


    }


    //	--------------------------------------------------------------------DEPOSIT MANAGEMENT-----------------------------------------------------------------
    @PostMapping(path = "/deposit/create/{clientID}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?>create(@PathVariable int clientID,  @RequestBody DepositeDTO depositeDTO){
        try {
            depositSercives.makeDeposit(clientID, depositeDTO);

            return new ResponseEntity<>( HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/deposit/get/{clientID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Deposit>get(@PathVariable int clientID, @RequestParam int depositID){
        try {
            Deposit deposit = depositSercives.getDeposit(clientID, depositID);

            return new ResponseEntity<>(deposit, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/deposit/get/all/{clientID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Deposit>>get(@PathVariable int clientID){
        try {
            List<Deposit> depositList = depositSercives.getAllDeposit(clientID);

            return new ResponseEntity<>(depositList, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
