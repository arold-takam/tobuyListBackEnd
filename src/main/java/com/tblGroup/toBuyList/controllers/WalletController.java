package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.services.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    private final TransferService transferService;

    public WalletController(TransferService transferService) {
        this.transferService = transferService;

    }

    @PostMapping("/transfer/{clientId}")
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
}
