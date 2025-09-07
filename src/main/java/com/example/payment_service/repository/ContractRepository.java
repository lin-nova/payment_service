package com.example.payment_service.repository;

import com.example.payment_service.model.Contract;
import com.example.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {
    Optional<Contract> findByNumber(String number);
}
