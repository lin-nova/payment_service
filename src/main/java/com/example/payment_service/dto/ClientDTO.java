package com.example.payment_service.dto;

public record ClientDTO(
        String username
) {
    public static ClientDTO from(com.example.payment_service.model.Client rawClient) {
        return new ClientDTO(
                rawClient.getUsername()
        );
    }
}
