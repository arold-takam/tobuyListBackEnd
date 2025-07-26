package com.tblGroup.toBuyList.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreditResponseDTO(int id, String title, String description, int clientID, int walletReceiver, int mAccountReceiverID, int creditOfferID, LocalDate dateCredit, LocalTime timeCredit) {

}
