package com.example.payment_service.service;

import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.exceptions.CsvParserException;
import com.example.payment_service.exceptions.InvalidFileTypeException;
import com.example.payment_service.model.PaymentType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentCsvParserTest {

    private PaymentCsvParser paymentCsvParser;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        paymentCsvParser = new PaymentCsvParser(validator);
    }

    @Test
    void parseRow_validRecord_returnsPaymentDTO() {
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("payment_date")).thenReturn("2025-09-07");
        when(record.get("amount")).thenReturn("100.50");
        when(record.get("type")).thenReturn("OUTGOING");
        when(record.get("contract_number")).thenReturn("12345");

        PaymentDTO expectedPayment = new PaymentDTO(
                LocalDate.of(2025, 9, 7),
                new BigDecimal("100.50"),
                PaymentType.OUTGOING,
                "12345"
        );

        PaymentDTO result = paymentCsvParser.parseRow(record);
        assertThat(result).isEqualTo(expectedPayment);
    }

    @Test
    void parseRow_invalidRecord_throwsCsvParserException() {
        CSVRecord record = mock(CSVRecord.class);
        when(record.get("payment_date")).thenReturn("2025-09-07");
        when(record.get("amount")).thenReturn("-100.50");
        when(record.get("type")).thenReturn("INCOMING");
        when(record.get("contract_number")).thenReturn("12345");

        CsvParserException exception = assertThrows(CsvParserException.class, () -> paymentCsvParser.parseRow(record));
        assertThat(exception.getMessage()).contains("Validation error: amount must be greater than or equal to 0.0");
    }

    @Test
    void parseFile_validFile_returnsListOfPaymentDTO() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        String csvContent = "payment_date,amount,type,contract_number\n2025-09-07,100.50,OUTGOING,12345\n2025-09-08,200.75,INCOMING,67890";
        when(mockFile.getOriginalFilename()).thenReturn("valid_file.csv");
        when(mockFile.getContentType()).thenReturn("text/csv");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        List<PaymentDTO> result = paymentCsvParser.parseFile(mockFile);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(new PaymentDTO(LocalDate.of(2025, 9, 7), new BigDecimal("100.50"), PaymentType.OUTGOING, "12345"));
        assertThat(result.get(1)).isEqualTo(new PaymentDTO(LocalDate.of(2025, 9, 8), new BigDecimal("200.75"), PaymentType.INCOMING, "67890"));
    }

    @Test
    void parseFile_invalidFile_throwsInvalidFileTypeException() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        String csvContent = "payment_date,amount,type,contract_number\n2025-09-07,-100.50,OUTGOING,12345";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        InvalidFileTypeException exception = assertThrows(InvalidFileTypeException.class, () -> paymentCsvParser.parseFile(mockFile));
        assertThat(exception.getMessage()).contains("Invalid File Type. Only CSV files are allowed.");
    }

    @Test
    void parseFile_emptyFile_throwsCsvParserException() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        String csvContent = "payment_date,amount,type,contract_number\n";
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));
        when(mockFile.getOriginalFilename()).thenReturn("invalid_file.csv");
        when(mockFile.getContentType()).thenReturn("text/csv");

        CsvParserException exception = assertThrows(CsvParserException.class, () -> paymentCsvParser.parseFile(mockFile));
        assertThat(exception.getMessage()).contains("CSV file is empty: invalid_file.csv");
    }

    @Test
    void parseFile_missingHeaders_throwsCsvParserException() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        String csvContent = "2025-09-07,100.50,OUTGOING,12345";
        when(mockFile.getOriginalFilename()).thenReturn("invalid_file.csv");
        when(mockFile.getContentType()).thenReturn("text/csv");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        CsvParserException exception = assertThrows(CsvParserException.class, () -> paymentCsvParser.parseFile(mockFile));
        assertThat(exception.getMessage()).contains("CSV file is empty: invalid_file.csv");
    }
}
