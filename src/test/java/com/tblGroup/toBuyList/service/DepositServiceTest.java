package com.tblGroup.toBuyList.service;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import com.tblGroup.toBuyList.services.DepositService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositServiceTest {
	
	@InjectMocks
	private DepositService depositService;
	
	@Mock
	private ClientRepository clientRepository;
	
	@Mock
	private MoneyAccountRepository moneyAccountRepository;
	
	@Mock
	private WalletRepository walletRepository;
	
	@Mock
	private DepositRepository depositRepository;
	
	@Mock
	private RefundRepository refundRepository;
	
	@Mock
	private CreditRepository creditRepository;
	
	@Mock
	private HistoryRepository historyRepository;
	
	@Test
	void testMakeDeposit_bestCase() throws Exception {
		// Arrange
		Client client = new Client();
		client.setId(1);
		Wallet wallet = new Wallet();
		wallet.setAmount(5000);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(20000);
		moneyAccount.setPhone("699123456");
		
		DepositDTO depositDTO = new DepositDTO(10000, "Deposit of 10000", "699123456");
		
		// Mocking
		when(clientRepository.findById(1)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(creditRepository.findAllByClient(client)).thenReturn(List.of());
		
		// Act
		depositService.makeDeposit(1, depositDTO);
		
		// Assert
		assertEquals(15000, wallet.getAmount());
		assertEquals(10000, moneyAccount.getAmount());
		verify(depositRepository).save(any(Deposit.class));
	}
	
	
	@Test
	void testMakeDeposit_withLateCreditAndPenaltyApplied() {
		// Arrange
		int clientID = 1;
		double depositAmount = 10000;
		double creditLimit = 15000;
		double alreadyRefunded = 5000;
		float taxRate = 0.02f;
		int creditDelay = 30;
		
		DepositDTO depositDTO = new DepositDTO(depositAmount, "Dépôt avec pénalité", "699123456");
		
		Wallet wallet = new Wallet();
		wallet.setAmount(2000);
		Client client = new Client();
		client.setId(clientID);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(15000);
		moneyAccount.setPhone("699123456");
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(creditDelay);
		offer.setLimitationCreditAmount(creditLimit);
		offer.setTaxAfterDelay(taxRate);
		
		Credit credit = new Credit();
		credit.setClient(client);
		credit.setCreditOffer(offer);
		credit.setDateCredit(LocalDate.now().minusDays(45));
		credit.setAmountRefund(alreadyRefunded);
		credit.setActive(true);
		
		// Mocking
		when(clientRepository.findById(clientID)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(creditRepository.findAllByClient(client)).thenReturn(List.of(credit));
		
		// Calculs corrigés
		double amountToTakeFromDeposit = depositAmount / (1 + taxRate); // ≈ 9803.92
		double penalty = depositAmount - amountToTakeFromDeposit;       // ≈ 196.08
		double totalToDebit = depositAmount;                            // 10000
		double expectedWalletAmount = wallet.getAmount();               // 2000 (rien ajouté)
		double expectedMoneyAccountAmount = moneyAccount.getAmount() - depositAmount; // 5000
		double expectedCreditRefund = alreadyRefunded + totalToDebit;  // 15000
		
		// Act
		depositService.makeDeposit(clientID, depositDTO);
		
		// Assert
		double delta = 0.01;
		assertEquals(expectedWalletAmount, wallet.getAmount(), delta);
		assertEquals(expectedMoneyAccountAmount, moneyAccount.getAmount(), delta);
		assertEquals(expectedCreditRefund, credit.getAmountRefund(), delta);
		
		// Vérification du Refund
		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		verify(refundRepository).save(refundCaptor.capture());
		Refund refund = refundCaptor.getValue();
		
		assertEquals(depositAmount, refund.getAmount(), delta);
		assertTrue(refund.isEnded());
		assertEquals("Prélèvement automatique pour crédit en retard", refund.getDescription());
		
		verify(depositRepository).save(any(Deposit.class));
		verify(historyRepository).save(any(History.class));
	}
	
	@Test
	void testMakeDeposit_withInsufficientDepositTriggersPenaltyRecalculation() {
		// Arrange
		int clientID = 1;
		double depositAmount = 5000;
		double creditLimit = 15000;
		double alreadyRefunded = 5000;
		float taxRate = 0.02f;
		int creditDelay = 30;
		
		DepositDTO depositDTO = new DepositDTO(depositAmount, "Dépôt partiel avec pénalité", "699123456");
		
		Wallet wallet = new Wallet();
		wallet.setAmount(1000);
		Client client = new Client();
		client.setId(clientID);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(8000);
		moneyAccount.setPhone("699123456");
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(creditDelay);
		offer.setLimitationCreditAmount(creditLimit);
		offer.setTaxAfterDelay(taxRate);
		
		Credit credit = new Credit();
		credit.setClient(client);
		credit.setCreditOffer(offer);
		credit.setDateCredit(LocalDate.now().minusDays(45));
		credit.setAmountRefund(alreadyRefunded);
		credit.setActive(true);
		
		// Mocking
		when(clientRepository.findById(clientID)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(creditRepository.findAllByClient(client)).thenReturn(List.of(credit));
		
		// Calculs attendus
		double amountToTakeFromDeposit = depositAmount / (1 + taxRate); // ≈ 4901.96
		double penalty = depositAmount - amountToTakeFromDeposit;       // ≈ 98.04
		double totalAmountForRefund = depositAmount;                    // 5000
		double expectedWalletAmount = wallet.getAmount();               // 1000 (rien ajouté)
		double expectedMoneyAccountAmount = moneyAccount.getAmount() - depositAmount; // 3000
		double expectedCreditRefund = alreadyRefunded + totalAmountForRefund; // 10000
		
		// Act
		depositService.makeDeposit(clientID, depositDTO);
		
		// Assert
		double delta = 0.01;
		assertEquals(expectedWalletAmount, wallet.getAmount(), delta);
		assertEquals(expectedMoneyAccountAmount, moneyAccount.getAmount(), delta);
		assertEquals(expectedCreditRefund, credit.getAmountRefund(), delta);
		
		// Vérification du Refund
		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		verify(refundRepository).save(refundCaptor.capture());
		Refund refund = refundCaptor.getValue();
		
		assertEquals(depositAmount, refund.getAmount(), delta);
		assertFalse(refund.isEnded()); // le crédit n'est pas encore totalement remboursé
		assertEquals("Prélèvement automatique pour crédit en retard", refund.getDescription());
		
		verify(depositRepository).save(any(Deposit.class));
		verify(historyRepository).save(any(History.class));
	}
	
	
}