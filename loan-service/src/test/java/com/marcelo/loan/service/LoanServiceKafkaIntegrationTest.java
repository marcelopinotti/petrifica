package com.marcelo.loan.service;

import com.marcelo.loan.controller.mapper.LoanMapper;
import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.controller.response.LoanResponse;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanReason;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.event.LoanRequestedEvent;
import com.marcelo.loan.messaging.LoanEventProducer;
import com.marcelo.loan.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceKafkaIntegrationTest {

    @Mock
    private LoanStateService loanStateService;

    @Mock
    private CustomerService customerService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanMapper loanMapper;

    @Mock
    private LoanEventProducer loanEventProducer;

    @InjectMocks
    private LoanService loanService;

    private Customer customer;
    private Loan loan;
    private LoanRequest loanRequest;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id("customer-123")
                .keycloakId("keycloak-456")
                .fullName("João Silva")
                .cpf("12345678900")
                .email("joao@email.com")
                .monthlyIncome(new BigDecimal("5000.00"))
                .createdAt(Instant.now())
                .build();

        loan = Loan.builder()
                .id("loan-789")
                .customerId("customer-123")
                .requestedAmount(new BigDecimal("10000.00"))
                .installments(12)
                .reason(LoanReason.HOME)
                .status(LoanStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        loanRequest = new LoanRequest(
                new BigDecimal("10000.00"),
                12,
                LoanReason.HOME
        );
    }

    @Test
    void shouldPublishKafkaEventWhenLoanIsCreated() {
        // Given
        when(customerService.getByKeycloakId("keycloak-456")).thenReturn(customer);
        when(loanMapper.toEntity(loanRequest, "customer-123")).thenReturn(loan);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanStateService.processEvent(any(), eq(LoanEvent.SUBMIT))).thenReturn(LoanStatus.UNDER_ANALYSIS);
        when(loanMapper.toDTO(loan)).thenReturn(mock(LoanResponse.class));

        // When
        loanService.createLoan("keycloak-456", loanRequest);

        // Then
        ArgumentCaptor<LoanRequestedEvent> eventCaptor = ArgumentCaptor.forClass(LoanRequestedEvent.class);
        verify(loanEventProducer, times(1)).publishLoanRequested(eventCaptor.capture());

        LoanRequestedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.loanId()).isEqualTo("loan-789");
        assertThat(capturedEvent.customerId()).isEqualTo("customer-123");
        assertThat(capturedEvent.customerCpf()).isEqualTo("12345678900");
        assertThat(capturedEvent.customerMonthlyIncome()).isEqualByComparingTo("5000.00");
        assertThat(capturedEvent.requestedAmount()).isEqualByComparingTo("10000.00");
        assertThat(capturedEvent.installments()).isEqualTo(12);
        assertThat(capturedEvent.reason()).isEqualTo(LoanReason.HOME.name());
        assertThat(capturedEvent.requestedAt()).isNotNull();
    }

    @Test
    void shouldPublishEventWithCorrectCustomerData() {
        // Given
        Customer richCustomer = Customer.builder()
                .id("customer-999")
                .keycloakId("keycloak-999")
                .fullName("Maria Santos")
                .cpf("99999999999")
                .email("maria@email.com")
                .monthlyIncome(new BigDecimal("25000.00"))
                .createdAt(Instant.now())
                .build();

        Loan bigLoan = Loan.builder()
                .id("loan-big")
                .customerId("customer-999")
                .requestedAmount(new BigDecimal("100000.00"))
                .installments(48)
                .reason(LoanReason.VEHICLE)
                .status(LoanStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        LoanRequest bigRequest = new LoanRequest(
                new BigDecimal("100000.00"),
                48,
                LoanReason.VEHICLE
        );

        when(customerService.getByKeycloakId("keycloak-999")).thenReturn(richCustomer);
        when(loanMapper.toEntity(bigRequest, "customer-999")).thenReturn(bigLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(bigLoan);
        when(loanStateService.processEvent(any(), eq(LoanEvent.SUBMIT))).thenReturn(LoanStatus.UNDER_ANALYSIS);
        when(loanMapper.toDTO(bigLoan)).thenReturn(mock(LoanResponse.class));

        // When
        loanService.createLoan("keycloak-999", bigRequest);

        // Then
        ArgumentCaptor<LoanRequestedEvent> eventCaptor = ArgumentCaptor.forClass(LoanRequestedEvent.class);
        verify(loanEventProducer).publishLoanRequested(eventCaptor.capture());

        LoanRequestedEvent event = eventCaptor.getValue();
        assertThat(event.customerMonthlyIncome()).isEqualByComparingTo("25000.00");
        assertThat(event.requestedAmount()).isEqualByComparingTo("100000.00");
        assertThat(event.installments()).isEqualTo(48);
    }

    @Test
    void shouldNotPublishEventWhenLoanCreationFails() {
        // Given
        when(customerService.getByKeycloakId("keycloak-456")).thenReturn(customer);
        when(loanMapper.toEntity(loanRequest, "customer-123")).thenReturn(loan);
        when(loanRepository.save(any(Loan.class))).thenThrow(new RuntimeException("Database error"));

        // When/Then
        try {
            loanService.createLoan("keycloak-456", loanRequest);
        } catch (RuntimeException e) {
            // Expected
        }

        verify(loanEventProducer, never()).publishLoanRequested(any());
    }
}
