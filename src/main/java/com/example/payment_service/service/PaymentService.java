package com.example.payment_service.service;


import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.dto.PaymentResponseDTO;
import com.example.payment_service.exceptions.ContractNotFoundException;
import com.example.payment_service.exceptions.PaymentNotFoundException;
import com.example.payment_service.model.Contract;
import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractService contractService;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findWithContractAndClientByUuid(paymentId).orElse(null);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found with id: " + paymentId);
        }
        return payment;
    }

    public List<Payment> findAllPaymentsByContractNumber(String contractNumber) {
        return paymentRepository.findAllByContractNumber(contractNumber);
    }

    public List<Payment> findAllByClientId(UUID clientId) {
        return paymentRepository.findAllByClientUuid(clientId);
    }

    public List<Payment> findAllByForMultipleClients(List<UUID> clientIds) {
        return paymentRepository.findAllByClientUuidIn(clientIds);
    }

    public List<Payment> findByPaymentIdAndClientName(UUID paymentId, String clientName) {
        return paymentRepository.findWithContractAndClientByUuid(paymentId)
                .filter(payment -> payment.getContract().getClient().getUsername().equals(clientName))
                .map(List::of)
                .orElseThrow();
    }

    public Payment savePaymentForClient(PaymentDTO paymentDTO, UUID clientId) {
        Contract contract = contractService.getContractByNumber(paymentDTO.contractNumber());

        if (!contract.getClient().getUuid().equals(clientId)) {
            throw new ContractNotFoundException("Contract with id: " + paymentDTO.contractNumber() + " not found for client with id: " + clientId);
        }

        return paymentRepository.save(new Payment(
                paymentDTO.amount(),
                paymentDTO.type(),
                paymentDTO.date(),
                contract,
                contract.getClient()
        ));
    }

    public List<PaymentResponseDTO> savePayments(List<PaymentDTO> paymentRows, UUID clientId) {
        return paymentRows.stream()
                .map(dto -> savePaymentForClient(dto, clientId))
                .map(PaymentResponseDTO::from)
                .toList();
    }

    public Payment updatePayment(UUID paymentId, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

        Contract contract = contractService.getContractByNumber(paymentDTO.contractNumber());

        existingPayment.setAmount(paymentDTO.amount());
        existingPayment.setType(paymentDTO.type());
        existingPayment.setDate(paymentDTO.date());
        existingPayment.setContract(contract);
        existingPayment.setClient(contract.getClient());

        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(UUID paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
