package com.example.payment_service.controller;


import com.example.payment_service.config.security.PaymentFileValidator;
import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.dto.PaymentResponse;
import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentType;
import com.example.payment_service.service.PaymentService;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponse> response = payments.stream().map(PaymentResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        Payment rawPayment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(PaymentResponse.from(rawPayment));
    }

    @GetMapping("/clients/{clientName}")
    public ResponseEntity<List<PaymentResponse>> findAllPaymentsByClientUsername(@PathVariable String clientName) {
        List<Payment> payments = paymentService.findAllByClientName(clientName);
        List<PaymentResponse> response = payments.stream().map(PaymentResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<PaymentResponse>> findAllPaymentsForMultipleClients(@RequestParam List<String> clientName) {
        List<Payment> payments = paymentService.findAllByForMultipleClients(clientName);
        List<PaymentResponse> response = payments.stream().map(PaymentResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contracts/{contractNumber}")
    public ResponseEntity<List<PaymentResponse>> findAllPaymentsByContractNumber(@PathVariable String contractNumber, Authentication authentication) {
        List<Payment> payments = paymentService.findAllPaymentsByContractNumber(contractNumber);
        List<PaymentResponse> response = payments.stream().map(PaymentResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "import", consumes = "multipart/form-data")
    public ResponseEntity<List<PaymentResponse>> createPayments(@RequestParam("file") List<MultipartFile> paymentFiles, Authentication authentication) {
        String clientId = authentication.getName();
        StringBuilder errorMessages = new StringBuilder();
        String[] expectedHeaders = {"payment_date", "amount", "type", "contract_number"};

        List<PaymentResponse> savedPayments = new ArrayList<>();
        for (MultipartFile paymentFile : paymentFiles) {
            PaymentFileValidator.validatePaymentCsvFile(paymentFile);

            try (Reader reader = new InputStreamReader(paymentFile.getInputStream())) {
                Iterable<CSVRecord> records = CSVFormat.Builder.create()
                        .setHeader(expectedHeaders)
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(new BufferedReader(reader));
                for (CSVRecord record : records) {
                    try {
                        String paymentDateStr = record.get("payment_date").trim();
                        String amountStr = record.get("amount").trim();
                        String typeStr = record.get("type").trim();
                        String contractNumber = record.get("contract_number").trim();

                        Date paymentDate = Date.valueOf(paymentDateStr);
                        Double amount = Double.valueOf(amountStr);
                        PaymentType type = PaymentType.valueOf(typeStr.toUpperCase());

                        PaymentDTO paymentToSave = new PaymentDTO(
                                paymentDate,
                                amount,
                                type,
                                contractNumber,
                                clientId
                        );
                        Payment savedPayment = paymentService.savePayment(paymentToSave);
                        savedPayments.add(PaymentResponse.from(savedPayment));
                    } catch (Exception e) {
                        errorMessages.append(String.format("Error in file %s: %s\n", paymentFile.getOriginalFilename(), e.getMessage()));
                    }
                }
            } catch (Exception e) {
                errorMessages.append(String.format("Error in file %s: %s\n", paymentFile.getOriginalFilename(), e.getMessage()));
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new RuntimeException(errorMessages.toString());
        }
        return ResponseEntity.ok(savedPayments);
    }


    @PutMapping(value = "{id}", consumes = "application/json")
    public ResponseEntity<PaymentResponse> updatePayment(@PathVariable UUID id, @RequestBody PaymentDTO paymentDTO, Authentication authentication) {
        Payment updatedPayment = paymentService.updatePayment(id, paymentDTO);
        return ResponseEntity.ok(PaymentResponse.from(updatedPayment));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id, Authentication authentication) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
