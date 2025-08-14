package com.tblGroup.toBuyList.services;

import com.tblGroup.toBuyList.dto.HistoryResponse;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.History;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final ClientService clientService;

    public HistoryService(HistoryRepository historyRepository, ClientRepository clientRepository, ClientService clientService) {
        this.historyRepository = historyRepository;
        this.clientService = clientService;
    }

    public List<HistoryResponse> getHistory(int clientId) {
        Client client = clientService.getClientById(clientId);

        List<History> listHistory = historyRepository.findAllByClient(client);
        List<HistoryResponse> listHistoryResponse = new ArrayList<>();

        for(History history : listHistory){
            HistoryResponse historyResponse = new HistoryResponse(
                    history.getAction(),
                    history.getDescription(),
                    history.getDateAction(),
                    history.getStatus()
            );
            listHistoryResponse.add(historyResponse);
        }

        return listHistoryResponse;


    }

    public void deleteHistory(int clientId){
        Client client = clientService.getClientById(clientId);

        historyRepository.deleteAllByClient(client);

    }
}
