package com.example.payment_service.service;


import com.example.payment_service.dto.PaymentDTO;
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

    public List<Payment> findAllByClientName(String clientName) {
        return paymentRepository.findAllByClientUsername(clientName);
    }

    public List<Payment> findAllByForMultipleClients(List<String> clientNames) {
        return paymentRepository.findAllByClientUsernameIn(clientNames);
    }

    public List<Payment> findByPaymentIdAndClientName(UUID paymentId, String clientName) {
        return paymentRepository.findWithContractAndClientByUuid(paymentId)
                .filter(payment -> payment.getContract().getClient().getUsername().equals(clientName))
                .map(List::of)
                .orElseThrow();
    }

    public Payment savePayment(PaymentDTO paymentDTO) {
        Contract contract = contractService.getContractByNumber(paymentDTO.contractNumber());
        return paymentRepository.save(new Payment(
                paymentDTO.amount(),
                paymentDTO.type(),
                paymentDTO.date(),
                contract,
                contract.getClient()
        ));
    }

    public Payment updatePayment(UUID paymentId, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(paymentId).orElse(null);
        if (existingPayment == null) {
            throw new PaymentNotFoundException("Payment not found with id: " + paymentId);
        }
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
