package com.tblGroup.toBuyList.dto;

import com.tblGroup.toBuyList.models.Enum.TypeTransfer;


public record TransferDTO(double amount, String description, String phone, TypeTransfer typeTransfer) { }
