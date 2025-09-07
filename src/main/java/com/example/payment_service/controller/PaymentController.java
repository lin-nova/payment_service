package com.example.payment_service.controller;


import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.dto.PaymentResponseDTO;
import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentCsvParser;
import com.example.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentCsvParser paymentCsvParser;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> findAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponseDTO> response = payments.stream().map(PaymentResponseDTO::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
        Payment rawPayment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(PaymentResponseDTO.from(rawPayment));
    }

    @PutMapping(value = "{id}", consumes = "application/json")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable UUID id, @Valid @RequestBody PaymentDTO paymentDTO, Authentication authentication) {
        Payment updatedPayment = paymentService.updatePayment(id, paymentDTO);
        return ResponseEntity.ok(PaymentResponseDTO.from(updatedPayment));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id, Authentication authentication) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/clients/{clientId}", consumes = "multipart/form-data")
    public ResponseEntity<List<PaymentResponseDTO>> createPayments(@RequestParam("file") List<MultipartFile> paymentFiles, @PathVariable UUID clientId) {
        List<PaymentDTO> paymentsToSave = new ArrayList<>();

        for (MultipartFile paymentFile : paymentFiles) {
            paymentsToSave = paymentCsvParser.parseFile(paymentFile);
        }

        List<PaymentResponseDTO> savedPayments = paymentService.savePayments(paymentsToSave, clientId);
        return ResponseEntity.ok(savedPayments);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<PaymentResponseDTO>> findAllPaymentsForMultipleClients(@RequestParam List<UUID> clientIds) {
        List<Payment> payments = paymentService.findAllByForMultipleClients(clientIds);
        List<PaymentResponseDTO> response = payments.stream().map(PaymentResponseDTO::from).toList();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<PaymentResponseDTO>> findAllPaymentsByClientId(@PathVariable UUID clientId) {
        List<Payment> payments = paymentService.findAllByClientId(clientId);
        List<PaymentResponseDTO> response = payments.stream().map(PaymentResponseDTO::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contracts/{contractNumber}")
    public ResponseEntity<List<PaymentResponseDTO>> findAllPaymentsByContractNumber(@PathVariable String contractNumber, Authentication authentication) {
        List<Payment> payments = paymentService.findAllPaymentsByContractNumber(contractNumber);
        List<PaymentResponseDTO> response = payments.stream().map(PaymentResponseDTO::from).toList();
        return ResponseEntity.ok(response);
    }
}
