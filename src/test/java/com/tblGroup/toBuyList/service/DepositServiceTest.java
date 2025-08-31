package com.tblGroup.toBuyList.service;

import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import com.tblGroup.toBuyList.services.DepositService;
import com.tblGroup.toBuyList.services.HistoryService;
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
	private CreditRepository creditRepository;
	
	@Mock
	private WalletRepository walletRepository;
	
	@Mock
	private MoneyAccountRepository moneyAccountRepository;
	
	@Mock
	private RefundRepository refundRepository;
	
	@Mock
	private DepositRepository depositRepository;
	
	@Mock
	private HistoryRepository historyRepository;
	
	@Mock
	private ClientRepository clientRepository;
	
	@Mock
	private HistoryService historyService;
	
	@Test
	void testAutoRefundTriggeredByDeposit_FullRefund() {
		// Arrange
		Client client = new Client();
		client.setId(1);
		Wallet wallet = new Wallet();
		wallet.setAmount(0);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(2000.0);
		
		CreditOffer creditOffer = new CreditOffer();
		creditOffer.setLimitationCreditAmount(1000.0);
		creditOffer.setTaxAfterDelay(0.1f);
		
		Credit credit = new Credit();
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		credit.setDateCredit(LocalDate.now().minusDays(10));
		credit.setAmountRefund(0);
		credit.setActive(true);
		
		DepositDTO depositDTO = new DepositDTO(1500.0, "Test deposit", "699123456");
		
		when(clientRepository.findById(1)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(creditRepository.findAllByClient(client)).thenReturn(List.of(credit));
		
		// Act
		depositService.makeDeposit(1, depositDTO);
		
		// Assert
		// The amount transferred to the wallet should be the deposit minus the refund
		ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
		verify(walletRepository, times(1)).save(walletCaptor.capture());
		assertEquals(400.0, walletCaptor.getValue().getAmount(), 0.0001);
		
		// The credit should be fully refunded and inactive
		ArgumentCaptor<Credit> creditCaptor = ArgumentCaptor.forClass(Credit.class);
		verify(creditRepository, times(1)).save(creditCaptor.capture());
		assertEquals(1100.0, creditCaptor.getValue().getAmountRefund(), 0.0001);
		assertFalse(creditCaptor.getValue().isActive());
		
		// A refund record should be saved with the correct amount
		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		verify(refundRepository, times(1)).save(refundCaptor.capture());
		assertEquals(1100.0, refundCaptor.getValue().getAmount(), 0.0001);
		
		// The money account and deposit should be saved correctly
		verify(moneyAccountRepository, times(1)).save(moneyAccount);
		verify(depositRepository, times(1)).save(any(Deposit.class));
	}
	
	
	@Test
	void testAutoRefundTriggeredByDeposit_PartialRefund() {
		// Arrange
		Client client = new Client();
		client.setId(1);
		Wallet wallet = new Wallet();
		wallet.setAmount(0);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(200.0);
		
		CreditOffer creditOffer = new CreditOffer();
		creditOffer.setLimitationCreditAmount(1000.0);
		creditOffer.setTaxAfterDelay(0.1f);
		
		Credit credit = new Credit();
		credit.setClient(client);
		credit.setCreditOffer(creditOffer);
		credit.setDateCredit(LocalDate.now().minusDays(10));
		credit.setAmountRefund(0);
		credit.setActive(true);
		
		// Deposit is less than the amount due (1100.0)
		DepositDTO depositDTO = new DepositDTO(150.0, "Test deposit", "699123456");
		
		when(clientRepository.findById(1)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(creditRepository.findAllByClient(client)).thenReturn(List.of(credit));
		
		// Act
		depositService.makeDeposit(1, depositDTO);
		
		// Assert
		// The amount transferred to the wallet should be the deposit minus the refund
		ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
		verify(walletRepository, times(1)).save(walletCaptor.capture());
		assertEquals(0.0, walletCaptor.getValue().getAmount());
		
		// The credit should be partially refunded and remain active
		ArgumentCaptor<Credit> creditCaptor = ArgumentCaptor.forClass(Credit.class);
		verify(creditRepository, times(1)).save(creditCaptor.capture());
		assertEquals(150.0, creditCaptor.getValue().getAmountRefund());
		assertTrue(creditCaptor.getValue().isActive());
		
		// A refund record should be saved with the correct amount
		ArgumentCaptor<Refund> refundCaptor = ArgumentCaptor.forClass(Refund.class);
		verify(refundRepository, times(1)).save(refundCaptor.capture());
		assertEquals(150.0, refundCaptor.getValue().getAmount());
		
		// The money account and deposit should be saved correctly
		verify(moneyAccountRepository, times(1)).save(moneyAccount);
		verify(depositRepository, times(1)).save(any(Deposit.class));
	}
}