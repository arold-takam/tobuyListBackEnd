package com.tblGroup.toBuyList.dto;

import com.tblGroup.toBuyList.models.Enum.TypeTransfer;

public record TransferDTO2(double amount, String description,String walletNumber, TypeTransfer typeTransfer) {
}
