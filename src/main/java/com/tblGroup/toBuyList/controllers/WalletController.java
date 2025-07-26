package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.Deposit;
import com.tblGroup.toBuyList.services.DepositService;
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
    private final DepositService depositService;

    public WalletController(TransferService transferService, DepositService depositService) {
        this.transferService = transferService;
        this.depositService = depositService;
    }



//    TRANSFER MANAGEMENT--------------------------------------------------------------------------------------------------------------------------

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
    public ResponseEntity<?>create(@PathVariable int clientID,  @RequestBody DepositDTO depositDTO){
        try {
            depositService.makeDeposit(clientID, depositDTO);

            return new ResponseEntity<>( HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/deposit/get/{clientID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Deposit>get(@PathVariable int clientID, @RequestParam int depositID){
        try {
            Deposit deposit = depositService.getDeposit(clientID, depositID);

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
            List<Deposit> depositList = depositService.getAllDeposit(clientID);

            return new ResponseEntity<>(depositList, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
