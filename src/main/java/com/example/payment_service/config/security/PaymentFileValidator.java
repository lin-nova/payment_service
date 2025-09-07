package com.example.payment_service.config.security;

import com.example.payment_service.exceptions.InvalidFileTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PaymentFileValidator {

    public static void validatePaymentCsvFile(MultipartFile paymentFile) {
        if (!"text/csv".equals(paymentFile.getContentType())) {
            throw new InvalidFileTypeException("Invalid File Type. Only CSV files are allowed.");
        }

        if (paymentFile.isEmpty()) {
            throw new InvalidFileTypeException("Payment File is Empty");
        }
    }
}
