package com.tblGroup.toBuyList.dto;

import com.tblGroup.toBuyList.models.Enum.TitleCreditOffer;

public record CreditOfferRequestDTO(double limitationCreditAmount, int creditDelay, float taxAfterDelay) {

}
