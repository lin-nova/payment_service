package com.example.payment_service.dto;


import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record PaymentResponseDTO(
        LocalDate date,
        BigDecimal amount,
        PaymentType type,
        ClientDTO client
) {
    public static PaymentResponseDTO from(Payment rawPayment) {
        Objects.requireNonNull(rawPayment, "Payment must not be null");
        return new PaymentResponseDTO(
                rawPayment.getDate(),
                rawPayment.getAmount(),
                rawPayment.getType(),
                ClientDTO.from(rawPayment.getClient())
        );
    }

    //TODO: Add payment with contract details?
}