package com.example.payment_service.service;

import com.example.payment_service.model.Client;
import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.ClientRepository;
import com.example.payment_service.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client getClientById(UUID id) {
        return clientRepository.findById(id).orElse(null);
    }
}
