package com.example.payment_service.dto;


import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentType;

import java.util.Date;
import java.util.Objects;

public record PaymentResponse(
        Date date,
        Double amount,
        PaymentType type,
        ClientDTO client
) {
    public static PaymentResponse from(Payment rawPayment) {
        Objects.requireNonNull(rawPayment, "Payment must not be null");
        return new PaymentResponse(
                rawPayment.getDate(),
                rawPayment.getAmount(),
                rawPayment.getType(),
                ClientDTO.from(rawPayment.getClient())
        );
    }

    //TODO: Add payment with contract details?
}