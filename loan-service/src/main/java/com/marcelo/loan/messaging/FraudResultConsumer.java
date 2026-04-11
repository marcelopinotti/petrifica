package com.marcelo.loan.messaging;

import com.marcelo.loan.event.FraudAnalysisResultEvent;
import com.marcelo.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudResultConsumer {

    private final LoanService loanService;

    @KafkaListener(
            topics = "${kafka.consumer.fraud-topic}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void handleFraudResult(FraudAnalysisResultEvent event) {
        log.info("Resultado de fraude recebido: loanId={}, customerId={}, verdict={}",
                event.loanId(), event.customerId(), event.verdict());

        try {
            loanService.applyFraudDecision(event);
        } catch (IllegalArgumentException e) {
            // Evento invalido nao deve bloquear o consumo do topico.
            log.error("Evento de fraude invalido para loanId={}: {}", event.loanId(), e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar resultado de fraude para loanId={}: {}",
                    event.loanId(), e.getMessage(), e);
            throw e;
        }
    }
}

