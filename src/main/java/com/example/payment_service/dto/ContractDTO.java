package com.example.payment_service.dto;

import com.example.payment_service.model.Contract;

public record ContractDTO(
        String contractNumber,
        ClientDTO client
) {
    public static ContractDTO from(Contract contract) {
        return new ContractDTO(
                contract.getNumber(),
                new ClientDTO(contract.getClient().getUsername())
        );
    }
}
