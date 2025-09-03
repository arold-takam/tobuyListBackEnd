package com.tblGroup.toBuyList.dto;


import com.tblGroup.toBuyList.models.Enum.MoneyAccountName;

public record MoneyAccountResponseDTO(int id, MoneyAccountName name, String phone, String password, double amount) {

}
