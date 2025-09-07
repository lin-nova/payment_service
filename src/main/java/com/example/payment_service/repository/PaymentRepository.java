package com.example.payment_service.repository;

import com.example.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	@EntityGraph(value = "Payment.contract.client", type = EntityGraph.EntityGraphType.FETCH)
	Optional<Payment> findWithContractAndClientByUuid(UUID uuid);

    List<Payment> findAllByContractNumber(String contractNumber);

    List<Payment> findAllByClientUsername(String username);

    List<Payment> findAllByClientUsernameIn(List<String> usernames);
}
