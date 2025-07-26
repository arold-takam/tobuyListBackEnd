package com.tblGroup.toBuyList.dto;

public record CreditOfferRequestDTO(String title, String description, double limitationAmount, int creditDelay, double creditTax) {
}
