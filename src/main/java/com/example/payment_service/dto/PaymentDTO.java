package com.example.payment_service.dto;

import com.example.payment_service.model.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDTO(
        LocalDate date,
        @DecimalMin(value = "0.0")
        BigDecimal amount,
        PaymentType type,
        String contractNumber) {
}