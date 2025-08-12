package com.tblGroup.toBuyList.dto;

import com.tblGroup.toBuyList.models.Credit;
import com.tblGroup.toBuyList.models.MoneyAccount;

import java.time.LocalDate;
import java.time.LocalTime;

public record RefundRequestDTO(String description, int creditID, int moneyAccountID, double amount) {

}
