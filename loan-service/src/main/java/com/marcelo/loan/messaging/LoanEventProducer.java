package com.marcelo.loan.messaging;

import com.marcelo.loan.event.LoanRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic}")
    private String loanTopic;

    public void publishLoanRequested(LoanRequestedEvent event) {
        log.info("Publicando evento de empréstimo solicitado: loanId={}, customerId={}, amount={}",
                event.loanId(), event.customerId(), event.requestedAmount());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(loanTopic, event.loanId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento publicado com sucesso: loanId={}, partition={}, offset={}",
                        event.loanId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Erro ao publicar evento: loanId={}, error={}",
                        event.loanId(), ex.getMessage(), ex);
            }
        });
    }
}
