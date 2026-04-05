package com.marcelo.loan.integration;

import com.marcelo.loan.controller.request.LoanRequest;
import com.marcelo.loan.entity.Customer;
import com.marcelo.loan.entity.Loan;
import com.marcelo.loan.entity.enums.LoanReason;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.event.LoanRequestedEvent;
import com.marcelo.loan.repository.CustomerRepository;
import com.marcelo.loan.repository.LoanRepository;
import com.marcelo.loan.service.LoanService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"loan-topic"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
class LoanCreationKafkaIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Value("${kafka.topic}")
    private String loanTopic;

    private KafkaMessageListenerContainer<String, LoanRequestedEvent> container;
    private BlockingQueue<ConsumerRecord<String, LoanRequestedEvent>> records;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        loanRepository.deleteAll();

        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "integration-test-group");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LoanRequestedEvent.class.getName());

        DefaultKafkaConsumerFactory<String, LoanRequestedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(configs);

        ContainerProperties containerProperties = new ContainerProperties(loanTopic);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, LoanRequestedEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, 1);
    }

    @AfterEach
    void tearDown() {
        container.stop();
        customerRepository.deleteAll();
        loanRepository.deleteAll();
    }

    @Test
    void shouldCreateLoanAndPublishEventToKafka() throws InterruptedException {
        // Given
        Customer customer = Customer.builder()
                .keycloakId("keycloak-integration-test")
                .fullName("Integration Test User")
                .cpf("11122233344")
                .email("integration@test.com")
                .monthlyIncome(new BigDecimal("7500.00"))
                .createdAt(Instant.now())
                .build();
        customerRepository.save(customer);

        LoanRequest request = new LoanRequest(
                new BigDecimal("15000.00"),
                18,
                LoanReason.EDUCATION
        );

        // When
        loanService.createLoan("keycloak-integration-test", request);

        // Then - Verificar banco de dados
        Loan savedLoan = loanRepository.findByCustomerId(customer.getId()).get(0);
        assertThat(savedLoan).isNotNull();
        assertThat(savedLoan.getStatus()).isEqualTo(LoanStatus.UNDER_ANALYSIS);
        assertThat(savedLoan.getRequestedAmount()).isEqualByComparingTo("15000.00");
        assertThat(savedLoan.getInstallments()).isEqualTo(18);

        // Then - Verificar evento Kafka
        ConsumerRecord<String, LoanRequestedEvent> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(savedLoan.getId());

        LoanRequestedEvent event = record.value();
        assertThat(event.loanId()).isEqualTo(savedLoan.getId());
        assertThat(event.customerId()).isEqualTo(customer.getId());
        assertThat(event.customerCpf()).isEqualTo("11122233344");
        assertThat(event.customerMonthlyIncome()).isEqualByComparingTo("7500.00");
        assertThat(event.requestedAmount()).isEqualByComparingTo("15000.00");
        assertThat(event.installments()).isEqualTo(18);
        assertThat(event.reason()).isEqualTo(LoanReason.EDUCATION.name());
        assertThat(event.requestedAt()).isNotNull();
    }

    @Test
    void shouldPublishEventWithAllCustomerDataForFraudAnalysis() throws InterruptedException {
        // Given - Cliente com renda alta solicitando valor alto
        Customer richCustomer = Customer.builder()
                .keycloakId("keycloak-rich")
                .fullName("Rich Customer")
                .cpf("99988877766")
                .email("rich@test.com")
                .monthlyIncome(new BigDecimal("50000.00"))
                .createdAt(Instant.now())
                .build();
        customerRepository.save(richCustomer);

        LoanRequest bigRequest = new LoanRequest(
                new BigDecimal("200000.00"),
                60,
                LoanReason.VEHICLE
        );

        // When
        loanService.createLoan("keycloak-rich", bigRequest);

        // Then
        ConsumerRecord<String, LoanRequestedEvent> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();

        LoanRequestedEvent event = record.value();
        // Verificar que todos os dados necessários para análise de fraude estão presentes
        assertThat(event.customerCpf()).isNotBlank();
        assertThat(event.customerMonthlyIncome()).isPositive();
        assertThat(event.requestedAmount()).isPositive();
        assertThat(event.installments()).isPositive();
        assertThat(event.reason()).isNotNull();

        // Verificar cálculo de risco potencial (valor vs renda)
        BigDecimal ratio = event.requestedAmount().divide(event.customerMonthlyIncome(), 2, BigDecimal.ROUND_HALF_UP);
        assertThat(ratio).isEqualByComparingTo("4.00"); // 200k / 50k = 4x a renda
    }
}
