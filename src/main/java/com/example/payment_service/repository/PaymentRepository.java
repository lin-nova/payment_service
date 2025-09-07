package com.example.payment_service.repository;

import com.example.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findWithContractAndClientByUuid(UUID uuid);

    List<Payment> findAllByContractNumber(String contractNumber);

    List<Payment> findAllByClientUuid(UUID username);

    List<Payment> findAllByClientUuidIn(List<UUID> clientIds);
}
