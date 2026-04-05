package com.marcelo.loan.messaging;

import com.marcelo.loan.event.LoanRequestedEvent;
import com.marcelo.loan.entity.enums.LoanReason;
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
class LoanEventProducerTest {

    @Autowired
    private LoanEventProducer loanEventProducer;

    @Value("${kafka.topic}")
    private String loanTopic;

    private KafkaMessageListenerContainer<String, LoanRequestedEvent> container;
    private BlockingQueue<ConsumerRecord<String, LoanRequestedEvent>> records;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
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
    }

    @Test
    void shouldPublishLoanRequestedEventSuccessfully() throws InterruptedException {
        // Given
        LoanRequestedEvent event = new LoanRequestedEvent(
                "loan-123",
                "customer-456",
                "12345678900",
                new BigDecimal("5000.00"),
                new BigDecimal("10000.00"),
                12,
                LoanReason.HOME.name(),
                Instant.now()
        );

        // When
        loanEventProducer.publishLoanRequested(event);

        // Then
        ConsumerRecord<String, LoanRequestedEvent> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("loan-123");
        assertThat(received.value()).isNotNull();
        assertThat(received.value().loanId()).isEqualTo("loan-123");
        assertThat(received.value().customerId()).isEqualTo("customer-456");
        assertThat(received.value().customerCpf()).isEqualTo("12345678900");
        assertThat(received.value().customerMonthlyIncome()).isEqualByComparingTo("5000.00");
        assertThat(received.value().requestedAmount()).isEqualByComparingTo("10000.00");
        assertThat(received.value().installments()).isEqualTo(12);
        assertThat(received.value().reason()).isEqualTo(LoanReason.HOME.name());
    }

    @Test
    void shouldPublishMultipleEventsInOrder() throws InterruptedException {
        // Given
        LoanRequestedEvent event1 = new LoanRequestedEvent(
                "loan-1", "customer-1", "11111111111",
                new BigDecimal("3000"), new BigDecimal("5000"),
                6, LoanReason.VEHICLE.name(), Instant.now()
        );
        LoanRequestedEvent event2 = new LoanRequestedEvent(
                "loan-2", "customer-2", "22222222222",
                new BigDecimal("8000"), new BigDecimal("15000"),
                24, LoanReason.EDUCATION.name(), Instant.now()
        );

        // When
        loanEventProducer.publishLoanRequested(event1);
        loanEventProducer.publishLoanRequested(event2);

        // Then
        ConsumerRecord<String, LoanRequestedEvent> received1 = records.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, LoanRequestedEvent> received2 = records.poll(10, TimeUnit.SECONDS);

        assertThat(received1).isNotNull();
        assertThat(received1.value().loanId()).isEqualTo("loan-1");

        assertThat(received2).isNotNull();
        assertThat(received2.value().loanId()).isEqualTo("loan-2");
    }

    @Test
    void shouldUseCorrectPartitioningByLoanId() throws InterruptedException {
        // Given
        String sameLoanId = "loan-same-key";
        LoanRequestedEvent event1 = new LoanRequestedEvent(
                sameLoanId, "customer-1", "11111111111",
                new BigDecimal("3000"), new BigDecimal("5000"),
                6, LoanReason.HOME.name(), Instant.now()
        );
        LoanRequestedEvent event2 = new LoanRequestedEvent(
                sameLoanId, "customer-2", "22222222222",
                new BigDecimal("8000"), new BigDecimal("15000"),
                24, LoanReason.EDUCATION.name(), Instant.now()
        );

        // When
        loanEventProducer.publishLoanRequested(event1);
        loanEventProducer.publishLoanRequested(event2);

        // Then
        ConsumerRecord<String, LoanRequestedEvent> received1 = records.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, LoanRequestedEvent> received2 = records.poll(10, TimeUnit.SECONDS);

        assertThat(received1).isNotNull();
        assertThat(received2).isNotNull();
        // Mesma chave deve ir para mesma partição
        assertThat(received1.partition()).isEqualTo(received2.partition());
    }
}
