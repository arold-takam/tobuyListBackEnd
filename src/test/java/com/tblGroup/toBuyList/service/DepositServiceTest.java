package com.tblGroup.toBuyList.service;


import com.tblGroup.toBuyList.dto.DepositDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import com.tblGroup.toBuyList.services.DepositService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		Client client = new Client();
		Wallet wallet = new Wallet();
		wallet.setAmount(5000);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(20000);
		moneyAccount.setPhone("699123456");
		
		DepositDTO depositDTO = new DepositDTO(10000, "Deposit of 10000", "699123456");
		
		when(clientRepository.findById(2)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		when(refundRepository.findAllByCredit_Client(client)).thenReturn(List.of());
		
		depositService.makeDeposit(2, depositDTO);
		
		assertEquals(15000, wallet.getAmount());
		assertEquals(10000, moneyAccount.getAmount());
		verify(depositRepository).save(any(Deposit.class));
	}
	
	@Test
	void testMakeDeposit_worstCase() throws Exception {
		// 1. Données de test
		Client client = new Client();
		Wallet wallet = new Wallet();
		wallet.setAmount(5000);
		client.setWallet(wallet);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setAmount(20000);
		moneyAccount.setPhone("699123456");
		
		DepositDTO depositDTO = new DepositDTO(10000, "Deposit with penalty", "699123456");
		
		// Création d'un scénario de crédit en retard
		Credit overdueCredit = new Credit();
		overdueCredit.setDateCredit(LocalDate.now().minusDays(40));
		overdueCredit.setAmountRefund(0);
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(30);
		overdueCredit.setCreditOffer(offer);
		
		Refund lastRefund = new Refund();
		lastRefund.setDateRefund(LocalDate.now().minusDays(10));
		lastRefund.setEnded(false);
		
		// 2. Mocking : simuler le comportement du repository
		when(clientRepository.findById(2)).thenReturn(Optional.of(client));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		
		// Simuler le crédit et le remboursement pour déclencher le prélèvement
		when(creditRepository.findAllByClient(client)).thenReturn(List.of(overdueCredit));
		when(refundRepository.findAllByCredit_Client(client)).thenReturn(List.of(lastRefund));
		
		// 3. Exécution du test
		depositService.makeDeposit(2, depositDTO);
		
		// 4. Assertions (Vérifications)
		// Le wallet reçoit 20% du dépôt : 5000 + (10000 * 0.20) = 7000
		assertEquals(7000, wallet.getAmount());
		
		// Le moneyAccount est débité du montant total du dépôt
		assertEquals(10000, moneyAccount.getAmount());
		
		// Vérifier que le crédit a été mis à jour
		assertEquals(8000, overdueCredit.getAmountRefund());
		verify(creditRepository).save(overdueCredit);
		
		// Vérifier que le dépôt a été enregistré avec le montant net
		verify(depositRepository).save(any(Deposit.class));
		
		// Vérifier que l'historique a bien été sauvegardé
		verify(historyRepository).save(any(History.class));
	}
}
