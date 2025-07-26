package com.tblGroup.toBuyList.dto;

import com.tblGroup.toBuyList.models.Enum.MoneyAccountName;

public record MoneyAccountDTO(MoneyAccountName name, String phone, String password) {

}
