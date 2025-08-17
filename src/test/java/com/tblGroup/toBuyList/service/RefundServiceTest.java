package com.tblGroup.toBuyList.service;

import com.tblGroup.toBuyList.dto.RefundRequestByMoneyAccountDTO;
import com.tblGroup.toBuyList.dto.RefundRequestByWalletDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import com.tblGroup.toBuyList.services.ClientService;
import com.tblGroup.toBuyList.services.RefundService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {
	
	// Injection du service à tester
	@InjectMocks
	private RefundService refundService;
	
	// Déclaration des dépendances à simuler (mocks)
	@Mock
	private CreditRepository creditRepository;
	@Mock
	private WalletRepository walletRepository;
	@Mock
	private RefundRepository refundRepository;
	@Mock
	private MoneyAccountRepository moneyAccountRepository;
	@Mock
	private ClientService clientService;
	
	@Test
	void makeRefundByWallet_bestCase_shouldSucceed() {
		// --- 1. Préparation des données (ARRANGE) ---
		
		// Créer un client, un portefeuille et un crédit pour le test
		Client client = new Client();
		client.setId(1);
		
		Wallet wallet = new Wallet();
		wallet.setAmount(10000);
		client.setWallet(wallet);
		
		Credit credit = new Credit();
		credit.setId(2);
		credit.setClient(client);
		credit.setAmountRefund(0); // Pas de remboursement initial
		credit.setDateCredit(LocalDate.now().minusDays(5)); // Crédit de 5 jours
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(30); // Délai de 30 jours
		offer.setLimitationCreditAmount(5000); // Montant max du crédit
		credit.setCreditOffer(offer);
		
		RefundRequestByWalletDTO request = new RefundRequestByWalletDTO(
			"Remboursement à temps", 3000
				);
		
		// Simuler le comportement des mocks
		// Quand le service cherche le crédit par ID, il retourne notre objet 'credit'.
		when(creditRepository.findById(2)).thenReturn(Optional.of(credit));
		
		// --- 2. Exécution de la méthode à tester (ACT) ---
		refundService.makeRefundByWallet(2, request);
		
		// --- 3. Vérification des résultats (ASSERT) ---
		
		// Vérifier que le solde du portefeuille a été mis à jour
		assertEquals(7000, wallet.getAmount());
		
		// Vérifier que le solde du crédit a été mis à jour
		assertEquals(3000, credit.getAmountRefund());
		
		// Vérifier que les méthodes de sauvegarde ont été appelées
		// On s'assure que le portefeuille et le crédit ont été sauvegardés
		verify(walletRepository, times(1)).save(wallet);
		verify(creditRepository, times(1)).save(credit);
		
		// On vérifie qu'un nouvel objet Refund a été sauvegardé
		verify(refundRepository, times(1)).save(any(Refund.class));
	}
	
	@Test
	void makeRefundByWallet_worstCase(){
		Client client = new Client();
		client.setId(1);
		
		Wallet wallet = new Wallet();
		wallet.setAmount(1500);
		client.setWallet(wallet);
		
		Credit credit = new Credit();
		credit.setId(2);
		credit.setClient(client);
		credit.setAmountRefund(0);
		credit.setDateCredit(LocalDate.parse("2025-08-15"));
		
		CreditOffer creditOffer = new CreditOffer();
		creditOffer.setCreditDelay(1);
		creditOffer.setTaxAfterDelay(0.75f);
		creditOffer.setLimitationCreditAmount(2000);
		
		credit.setCreditOffer(creditOffer);
		
		RefundRequestByWalletDTO request = new RefundRequestByWalletDTO("first late refund", 500);
		
		when(creditRepository.findById(2)).thenReturn(Optional.of(credit));
		
		refundService.makeRefundByWallet(2, request);
		
		assertEquals(250, wallet.getAmount());
		assertEquals(1250, credit.getAmountRefund());
		
		verify(walletRepository, times(1)).save(wallet);
		verify(creditRepository, times(1)).save(credit);
		
		verify(refundRepository, times(1)).save(any(Refund.class));
	}
	
	@Test
	void makeRefundByMoneyAccount_bestCase_shouldSucceed() {
		// --- 1. Préparation des données (ARRANGE) ---
		Client client = new Client();
		client.setId(1);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setPhone("699123456");
		moneyAccount.setAmount(10000);
		
		Credit credit = new Credit();
		credit.setId(2);
		credit.setClient(client);
		credit.setAmountRefund(0);
		credit.setDateCredit(LocalDate.now().minusDays(5));
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(30);
		offer.setLimitationCreditAmount(5000);
		credit.setCreditOffer(offer);
		
		RefundRequestByMoneyAccountDTO request = new RefundRequestByMoneyAccountDTO("Remboursement par compte monétaire",
			"699123456", 3000
			);
		
		// Simuler le comportement des mocks
		when(creditRepository.findById(2)).thenReturn(Optional.of(credit));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		
		// --- 2. Exécution de la méthode à tester (ACT) ---
		refundService.makeRefundByMoneyAccount(2, request);
		
		// --- 3. Vérification des résultats (ASSERT) ---
		// Le solde du compte monétaire a été mis à jour
		assertEquals(7000, moneyAccount.getAmount());
		
		// Le solde du crédit a été mis à jour
		assertEquals(3000, credit.getAmountRefund());
		
		// Les appels aux méthodes de sauvegarde ont été vérifiés
		verify(moneyAccountRepository, times(1)).save(moneyAccount);
		verify(creditRepository, times(1)).save(credit);
		verify(refundRepository, times(1)).save(any(Refund.class));
	}
	
	@Test
	void makeRefundByMoneyAccount_worstCase_shouldSucceed() {
		// --- 1. Préparation des données (ARRANGE) ---
		Client client = new Client();
		client.setId(1);
		
		MoneyAccount moneyAccount = new MoneyAccount();
		moneyAccount.setPhone("699123456");
		moneyAccount.setAmount(1500); // Solde suffisant pour couvrir la pénalité
		
		Credit credit = new Credit();
		credit.setId(2);
		credit.setClient(client);
		credit.setAmountRefund(0);
		credit.setDateCredit(LocalDate.parse("2025-08-15")); // Crédit en retard
		
		CreditOffer offer = new CreditOffer();
		offer.setCreditDelay(1);
		offer.setTaxAfterDelay(0.75f); // Taux de pénalité
		offer.setLimitationCreditAmount(2000);
		credit.setCreditOffer(offer);
		
		RefundRequestByMoneyAccountDTO request = new RefundRequestByMoneyAccountDTO("Remboursement avec pénalité"
			, "699123456", 500
			);
		
		// Simuler le comportement des mocks
		when(creditRepository.findById(2)).thenReturn(Optional.of(credit));
		when(moneyAccountRepository.findByPhone("699123456")).thenReturn(moneyAccount);
		
		// --- 2. Exécution de la méthode à tester (ACT) ---
		refundService.makeRefundByMoneyAccount(2, request);
		
		// --- 3. Vérification des résultats (ASSERT) ---
		// Le solde du compte monétaire doit être de 250 (1500 - 1250)
		assertEquals(250, moneyAccount.getAmount());
		
		// Le montant du crédit doit être de 1250 (500 + 750 de pénalité)
		assertEquals(1250, credit.getAmountRefund());
		
		// Les appels aux méthodes de sauvegarde ont été vérifiés
		verify(moneyAccountRepository, times(1)).save(moneyAccount);
		verify(creditRepository, times(1)).save(credit);
		verify(refundRepository, times(1)).save(any(Refund.class));
	}
}