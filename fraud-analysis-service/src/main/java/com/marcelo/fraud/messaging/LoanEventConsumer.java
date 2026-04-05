package com.marcelo.fraud.messaging;

import com.marcelo.fraud.event.LoanRequestedEvent;
import com.marcelo.fraud.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanEventConsumer {

    private final FraudAnalysisService fraudAnalysisService;

    @KafkaListener(
            topics = "${kafka.topics.input}",
            groupId = "${kafka.group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLoanRequested(LoanRequestedEvent event) {
        log.info("Evento recebido: loanId={}, customerId={}, amount={}",
                event.loanId(), event.customerId(), event.requestedAmount());

        try {
            fraudAnalysisService.analyzeLoan(event);
            log.info("Analise de fraude concluida com sucesso: loanId={}", event.loanId());
        } catch (Exception e) {
            log.error("Erro ao processar analise de fraude: loanId={}, error={}",
                    event.loanId(), e.getMessage(), e);
            throw e;
        }
    }
}
