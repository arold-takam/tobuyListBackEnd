package com.tblGroup.toBuyList.controllers;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.dto.HistoryResponse;
import com.tblGroup.toBuyList.dto.TransferDTO;
import com.tblGroup.toBuyList.dto.TransferDTO2;
import com.tblGroup.toBuyList.models.Deposit;
import com.tblGroup.toBuyList.models.Enum.TypeTransfer;
import com.tblGroup.toBuyList.services.DepositService;
import com.tblGroup.toBuyList.services.HistoryService;
import com.tblGroup.toBuyList.services.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/wallet", produces = APPLICATION_JSON_VALUE)
public class WalletController {
    
    private final TransferService transferService;
    private final DepositService depositService;
    private final HistoryService historyService;
    
    public WalletController(TransferService transferService, DepositService depositService, HistoryService historyService) {
        this.transferService = transferService;
        this.depositService = depositService;
        this.historyService = historyService;
    }
    
    // --- TRANSFER MANAGEMENT ------------------------------------------------------------
    
    @PostMapping(path = "/transfer/account/{clientId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> transferToAccount(@PathVariable int clientId, @RequestBody TransferDTO transfer, @RequestParam TypeTransfer type) {
        return handle(() -> {
            transferService.makeATransferToAnAccount(clientId, transfer, type);
            return ResponseEntity.ok().build();
        });
    }
    
    @PostMapping(path = "/transfer/wallet/{clientId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> transferToWallet(@PathVariable int clientId, @RequestBody TransferDTO2 transfer, @RequestParam TypeTransfer type) {
        return handle(() -> {
            transferService.makeATransferToAWallet(clientId, transfer, type);
            return ResponseEntity.ok().build();
        });
    }
    
    // --- DEPOSIT MANAGEMENT -------------------------------------------------------------
    
    @PostMapping(path = "/deposit/{clientId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createDeposit(@PathVariable int clientId, @RequestBody DepositDTO depositDTO) {
        return handle(() -> {
            depositService.makeDeposit(clientId, depositDTO);
            return ResponseEntity.ok().build();
        });
    }
    
    @GetMapping(path = "/deposit/{clientId}")
    public ResponseEntity<Deposit> getDeposit(@PathVariable int clientId, @RequestParam int depositID) {
        return handle(() -> new ResponseEntity<>(depositService.getDeposit(clientId, depositID), OK));
    }
    
    @GetMapping(path = "/deposit/all/{clientId}")
    public ResponseEntity<List<Deposit>> getAllDeposits(@PathVariable int clientId) {
        return handle(() -> {
            List<Deposit> deposits = depositService.getAllDeposit(clientId);
            return deposits.isEmpty() ? new ResponseEntity<>(NO_CONTENT) : ResponseEntity.ok(deposits);
        });
    }
    
    // --- HISTORY MANAGEMENT -------------------------------------------------------------
    
    @GetMapping(path = "/history/{clientId}")
    public ResponseEntity<List<HistoryResponse>> getHistory(@PathVariable int clientId) {
        return handle(() -> ResponseEntity.ok(historyService.getHistory(clientId)));
    }
    
    @DeleteMapping(path = "/history/{clientId}")
    public ResponseEntity<Void> deleteHistory(@PathVariable int clientId) {
        return handle(() -> {
            historyService.deleteHistory(clientId);
            return ResponseEntity.noContent().build();
        });
    }
    
    // --- ERROR HANDLING UTILITY ---------------------------------------------------------
    
    private <T> ResponseEntity<T> handle(SupplierWithException<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (IllegalArgumentException e) {
            System.out.println("Not found: " + e.getMessage());
            return new ResponseEntity<>(NOT_FOUND);
        } catch (Exception e) {
            System.out.println("Bad request: " + e.getMessage());
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }
    
    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
