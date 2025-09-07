package com.example.payment_service.service;

import com.example.payment_service.config.security.PaymentFileValidator;
import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.exceptions.CsvParserException;
import com.example.payment_service.model.PaymentType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PaymentCsvParser {

    private final Validator validator;
    private final String[] EXPECTED_HEADERS = {"payment_date", "amount", "type", "contract_number"};

    public PaymentDTO parseRow(CSVRecord record) {
        LocalDate paymentDate = LocalDate.parse(record.get("payment_date").trim());
        BigDecimal amount = new BigDecimal(record.get("amount").trim());
        PaymentType type = PaymentType.valueOf(record.get("type").trim().toUpperCase());
        String contractNumber = record.get("contract_number").trim();

        PaymentDTO parsedPayment = new PaymentDTO(
                paymentDate,
                amount,
                type,
                contractNumber
        );

        Set<ConstraintViolation<PaymentDTO>> violations = validator.validate(parsedPayment);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<PaymentDTO> violation : violations) {
                throw new CsvParserException("Validation error: " + violation.getPropertyPath() + " " + violation.getMessage());
            }
        }
        return parsedPayment;
    }

    public List<PaymentDTO> parseFile(MultipartFile paymentFile) {
        PaymentFileValidator.validatePaymentCsvFile(paymentFile);

        List<PaymentDTO> paymentsToSave = new ArrayList<>();
        try (Reader reader = new InputStreamReader(paymentFile.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.Builder.create()
                    .setHeader(EXPECTED_HEADERS)
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(new BufferedReader(reader));

            if (!records.iterator().hasNext()) {
                throw new CsvParserException("CSV file is empty: " + paymentFile.getOriginalFilename());
            }

            for (CSVRecord record : records) {
                PaymentDTO paymentDTO = parseRow(record);
                paymentsToSave.add(paymentDTO);
            }
        } catch (IOException e) {
            throw new CsvParserException("Failed to parse CSV file: " + paymentFile.getOriginalFilename() + e.getMessage());
        }

        return paymentsToSave;
    }
}
