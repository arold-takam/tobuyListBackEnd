package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.*;
import com.tblGroup.toBuyList.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClientService {
	private final ClientRepository clientRepository;
	private final WalletRepository walletRepository;
	private final TransferRepository transferRepository;

    private final PasswordEncoder passwordEncoder;
	private final CreditRepository creditRepository;
	private final DepositRepository depositRepository;
	private final HistoryRepository historyRepository;
	private final MoneyAccountRepository moneyAccountRepository;
	private final RefundRepository refundRepository;


	public ClientService(ClientRepository clientRepository, WalletRepository walletRepository, TransferRepository transferRepository, PasswordEncoder passwordEncoder, CreditRepository creditRepository, DepositRepository depositRepository, HistoryRepository historyRepository, MoneyAccountRepository moneyAccountRepository, RefundRepository refundRepository) {
		this.clientRepository = clientRepository;
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
        this.passwordEncoder = passwordEncoder;
		this.creditRepository = creditRepository;
		this.depositRepository = depositRepository;
		this.historyRepository = historyRepository;
		this.moneyAccountRepository = moneyAccountRepository;
		this.refundRepository = refundRepository;
	}


//	 /*0231------------------------------------------------------------------------------------------------------------------
//	---------------------------------CLIENT MANAGEMENT----------------------------------------------
	@Transactional
	public Client createClient(ClientDTO client){
		Wallet wallet = new Wallet();

		Client clientSaved = new Client();

		clientSaved.setName(client.name());
		clientSaved.setUsername(client.username());
		clientSaved.setMail(client.mail());
		clientSaved.setPassword(passwordEncoder.encode(client.password()));
		wallet.setWalletNumber(autoGenerateAWalletNumber());

		if(clientRepository.existsByMail(client.mail())){
			throw new IllegalArgumentException("Email already exists");
		}

		walletRepository.save(wallet);
		clientSaved.setWallet(wallet);

		return clientRepository.save(clientSaved);
	}

	public Client getClientById(int clientID){
		Optional<Client>optionalClient =  clientRepository.findById(clientID);

		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with ID: "+clientID+" not found");
		}

		return optionalClient.get();
	}

	public List<Client>getAllClients(){
		return clientRepository.findAll();
	}

	public Client updateClient(int id, ClientDTO newClient){
		Client existingClient = getClientById(id) ;

		existingClient.setName(newClient.name());
		existingClient.setUsername(newClient.username());
		existingClient.setMail(newClient.mail());
		existingClient.setPassword(passwordEncoder.encode(newClient.password()));

		return clientRepository.save(existingClient);
	}

	@Transactional
	public void deleteClient(int id){
		if (!clientRepository.existsById(id)){
			throw new IllegalArgumentException("Client with ID: "+id+" not found");
		}

		Client clientToDelete = getClientById(id);

		List<Credit>creditList = creditRepository.findAllByClient(clientToDelete);
		List<Deposit>depositList = depositRepository.findAllByClientId(clientToDelete.getId());
		List<History>historyList = historyRepository.findAllByClient(clientToDelete);
		List<MoneyAccount>moneyAccountList = moneyAccountRepository.findAllByClientId(clientToDelete.getId());
		List<Refund>refundList = refundRepository.findAllByCredit_Client(clientToDelete);
		List<Transfer>transferList = transferRepository.findAllByClient(clientToDelete);

		creditRepository.deleteAll(creditList);
		depositRepository.deleteAll(depositList);
		historyRepository.deleteAll(historyList);
		moneyAccountRepository.deleteAll(moneyAccountList);
		refundRepository.deleteAll(refundList);
		transferRepository.deleteAll(transferList);

		transferRepository.deleteByClient_id(id);
		clientRepository.deleteById(id);
		walletRepository.deleteById(clientToDelete.getWallet().getId());

	}

//	------------------------------------------------------------------------------------------------------------------

	private String autoGenerateAWalletNumber(){
		String walletNumber;
		do {
			walletNumber = String.format("%06d", new Random().nextInt(1000000));
		} while (walletRepository.existsByWalletNumber(walletNumber));

		return walletNumber;
	}

//	----------------------------------------------WALLET MANAGEMENT-------------------------------------------------------------------------------------------------

	public Wallet getWallet(int clientID){
		return getClientById(clientID).getWallet();
	}


//----------------------------------------UTILITY FUNCTIONS---------------------------------------------------------------------------------------------------------

    public boolean authentification(int clientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Client client = getClientById(clientId);
        return client.getUsername().equals(auth.getName());
    }


}
