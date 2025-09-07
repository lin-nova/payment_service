package com.example.payment_service.service;

import com.example.payment_service.exceptions.ContractNotFoundException;
import com.example.payment_service.model.Contract;
import com.example.payment_service.repository.ContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;

    public Contract getContractById(UUID id) {
        return contractRepository.findById(id).orElseThrow(() -> new ContractNotFoundException("Contract not found with id: " + id));
    }

    public Contract getContractByNumber(String number) {
        return contractRepository.findByNumber(number).orElseThrow(() -> new ContractNotFoundException("Contract not found with number: " + number));
    }
}
