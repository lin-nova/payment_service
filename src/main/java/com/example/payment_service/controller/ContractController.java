package com.example.payment_service.controller;

import com.example.payment_service.model.Contract;
import com.example.payment_service.service.ContractService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contracts")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @GetMapping("{id}")
    public ResponseEntity<Contract> getClientById(@PathVariable UUID id) {
        Contract rawContract = contractService.getContractById(id);
        return ResponseEntity.ok(rawContract);
    }
}
