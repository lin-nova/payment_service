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

@WebMvcTest(PaymentController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentCsvParser paymentCsvParser;

    @MockitoBean
    private JwtUtil jwtUtil;

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
                .date(LocalDate.of(2023, 9, 7))
                .amount(BigDecimal.valueOf(100.0))
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

    @Test
    @WithMockUser(username = "test_user")
    void getPaymentById_returnsPayment_whenPaymentExists() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Contract contract = new Contract();
        contract.setNumber("C-123");

        Client client = new Client();
        client.setContracts(List.of(contract));
        client.setUsername("test_user");

        Payment payment = Payment.builder()
                .uuid(paymentId)
                .date(LocalDate.of(2023, 9, 7))
                .amount(BigDecimal.valueOf(100.0))
                .type(PaymentType.OUTGOING)
                .contract(contract)
                .client(client)
                .build();

        when(paymentService.getPaymentById(paymentId)).thenReturn(payment);

        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.date").value("2023-09-07"))
                .andExpect(jsonPath("$.type").value("OUTGOING"))
                .andExpect(jsonPath("$.client.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "test_user")
    void getPaymentById_returnsNotFound_whenPaymentDoesNotExist() throws Exception {
        UUID paymentId = UUID.randomUUID();
        when(paymentService.getPaymentById(paymentId)).thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(get("/api/v1/payments/{id}", paymentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Payment not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments/" + paymentId));
    }

    @Test
    @WithMockUser(username = "test_user")
    void updatePayment_returnsUpdatedPayment_whenPaymentExists() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Contract contract = new Contract();
        contract.setNumber("C-123");

        Client client = new Client();
        client.setContracts(List.of(contract));
        client.setUsername("test_user");

        Payment updatedPayment = Payment.builder()
                .uuid(paymentId)
                .date(LocalDate.of(2023, 9, 8))
                .amount(BigDecimal.valueOf(200.0))
                .type(PaymentType.INCOMING)
                .contract(contract)
                .client(client)
                .build();

        String paymentDtoJson = "{" +
                "\"date\":\"2023-09-08\"," +
                "\"amount\":200.0," +
                "\"type\":\"INCOMING\"," +
                "\"contractNumber\":\"C-123\"}";

        when(paymentService.updatePayment(eq(paymentId), any())).thenReturn(updatedPayment);

        mockMvc.perform(put("/api/v1/payments/{id}", paymentId)
                        .contentType("application/json")
                        .content(paymentDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.date").value("2023-09-08"))
                .andExpect(jsonPath("$.type").value("INCOMING"))
                .andExpect(jsonPath("$.client.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "test_user")
    void updatePayment_returnsNotFound_whenPaymentDoesNotExist() throws Exception {
        UUID paymentId = UUID.randomUUID();
        String paymentDtoJson = "{" +
                "\"date\":\"2023-09-08\"," +
                "\"amount\":200.0," +
                "\"type\":\"INCOMING\"," +
                "\"contractNumber\":\"C-123\"}";

        when(paymentService.updatePayment(eq(paymentId), any())).thenThrow(new PaymentNotFoundException("Payment not found with id: " + paymentId));

        mockMvc.perform(put("/api/v1/payments/{id}", paymentId)
                        .contentType("application/json")
                        .content(paymentDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments/" + paymentId))
                .andExpect(jsonPath("$.message").value("Payment not found with id: " + paymentId));
    }

    @Test
    @WithMockUser(username = "test_user")
    void createPayments_returnsSavedPayments_whenValidFileUploaded() throws Exception {
        UUID clientId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "payments.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "date,amount,type,contractNumber\n2023-09-07,100.0,OUTGOING,C-123".getBytes()
        );

        PaymentDTO paymentDTO = new PaymentDTO(LocalDate.of(2023, 9, 7), BigDecimal.valueOf(100.0), PaymentType.OUTGOING, "C-123");

        ClientDTO client = new ClientDTO("test_user");
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(LocalDate.of(2023, 9, 7), BigDecimal.valueOf(100.0), PaymentType.OUTGOING, client);

        when(paymentCsvParser.parseFile(any())).thenReturn(List.of(paymentDTO));
        when(paymentService.savePayments(anyList(), eq(clientId))).thenReturn(List.of(responseDTO));

        mockMvc.perform(multipart("/api/v1/payments/clients/{clientId}", clientId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2023-09-07"))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("OUTGOING"))
                .andExpect(jsonPath("$[0].client.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "test_user")
    void createPayments_returnsSavedPayments_whenMultipleValidFilesUploaded() throws Exception {
        UUID clientId = UUID.randomUUID();
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "payments1.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "date,amount,type,contractNumber\n2023-09-07,100.0,OUTGOING,C-123".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file",
                "payments2.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "date,amount,type,contractNumber\n2023-09-08,200.0,INCOMING,C-124".getBytes()
        );

        PaymentDTO paymentDTO1 = new PaymentDTO(LocalDate.of(2023, 9, 7), BigDecimal.valueOf(100.0), PaymentType.OUTGOING, "C-123");
        PaymentDTO paymentDTO2 = new PaymentDTO(LocalDate.of(2023, 9, 8), BigDecimal.valueOf(200.0), PaymentType.INCOMING, "C-124");

        ClientDTO client = new ClientDTO("test_user");
        PaymentResponseDTO responseDTO1 = new PaymentResponseDTO(LocalDate.of(2023, 9, 7), BigDecimal.valueOf(100.0), PaymentType.OUTGOING, client);
        PaymentResponseDTO responseDTO2 = new PaymentResponseDTO(LocalDate.of(2023, 9, 8), BigDecimal.valueOf(200.0), PaymentType.INCOMING, client);

        when(paymentCsvParser.parseFile(any())).thenReturn(List.of(paymentDTO1), List.of(paymentDTO2));
        when(paymentService.savePayments(anyList(), eq(clientId))).thenReturn(List.of(responseDTO1, responseDTO2));

        mockMvc.perform(multipart("/api/v1/payments/clients/{clientId}", clientId)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2023-09-07"))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].type").value("OUTGOING"))
                .andExpect(jsonPath("$[0].client.username").value("test_user"))
                .andExpect(jsonPath("$[1].date").value("2023-09-08"))
                .andExpect(jsonPath("$[1].amount").value(200.0))
                .andExpect(jsonPath("$[1].type").value("INCOMING"))
                .andExpect(jsonPath("$[1].client.username").value("test_user"));
    }

    @Test
    @WithMockUser(username = "test_user")
    void createPayments_returnsBadRequest_whenInvalidMediaTypeUploaded() throws Exception {
        UUID clientId = UUID.randomUUID();
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/payments/clients/{clientId}", clientId)
                        .file(invalidFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Content-Type", "text/csv"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Content-Type 'text/csv' is not supported"))
                .andExpect(jsonPath("$.path").value("/api/v1/payments/clients/" + clientId))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
