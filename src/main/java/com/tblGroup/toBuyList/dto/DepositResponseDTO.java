package com.tblGroup.toBuyList.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DepositResponseDTO(int id, double walletAmount, String description, int mAccountID, double mAccountAmount, int clientID, LocalDate dateDeposit, LocalTime timeDeposit) {

}
