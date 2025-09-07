package com.example.payment_service.dto;

import com.example.payment_service.model.PaymentType;

import java.sql.Date;


public record PaymentDTO(Date date,
                         Double amount,
                         PaymentType type,
                         String contractNumber,
                         String clientId
) {

}