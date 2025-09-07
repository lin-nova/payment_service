package com.example.payment_service.controller;

import com.example.payment_service.config.security.SecurityConfig;
import com.example.payment_service.dto.ClientDTO;
import com.example.payment_service.dto.PaymentDTO;
import com.example.payment_service.dto.PaymentResponseDTO;
import com.example.payment_service.exceptions.PaymentNotFoundException;
import com.example.payment_service.model.Client;
import com.example.payment_service.model.Contract;
import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentType;
import com.example.payment_service.service.PaymentCsvParser;
import com.example.payment_service.service.PaymentService;
import com.example.payment_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: To be implemented
class PaymentControllerIntegrationTest {

}
