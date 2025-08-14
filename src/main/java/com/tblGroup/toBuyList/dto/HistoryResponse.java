package com.tblGroup.toBuyList.dto;

import java.util.Date;

public record HistoryResponse(String action, String Description, Date dateAction, String status) {
}
