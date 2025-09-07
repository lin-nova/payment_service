package com.example.payment_service.controller;

import com.example.payment_service.model.Client;
import com.example.payment_service.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@AllArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("{id}")
    public ResponseEntity<Client> getClientById(@PathVariable UUID id) {
        Client rawClient = clientService.getClientById(id);
        return ResponseEntity.ok(rawClient);
    }


}
