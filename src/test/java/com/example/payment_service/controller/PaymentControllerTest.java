package com.example.payment_service.controller;

import com.example.payment_service.config.security.SecurityConfig;
import com.example.payment_service.model.Client;
import com.example.payment_service.model.Contract;
import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentType;
import com.example.payment_service.service.PaymentService;
import com.example.payment_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String CLIENT_JWT_TOKEN = "generate_token_for_client_1";

    @Test
    @WithMockUser(username = "test_user")
    void findAllPayments_returnsPayments_whenPaymentsExist() throws Exception {

        Contract contract = new Contract();
        contract.setNumber("C-123");

        Client client = new Client();
        client.setContracts(List.of(contract));
        client.setUsername("test_user");

        Payment payment = Payment.builder()
                .uuid(UUID.randomUUID())
                .date(Date.valueOf("2023-09-07"))
                .amount(100.0)
                .type(PaymentType.OUTGOING)
                .contract(contract)
                .client(client)
                .build();

        List<Payment> payments = List.of(payment);
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].date").value("2023-09-07"))
                .andExpect(jsonPath("$[0].type").value("OUTGOING"))
                .andExpect(jsonPath("$[0].client.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "test_user")
    void findAllPayments_returnsEmptyList_whenNoPaymentsExist() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
