package com.marcelo.fraud.messaging;

import com.marcelo.fraud.event.FraudAnalysisResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudResultProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.output}")
    private String topicName;

    public void publishFraudResult(FraudAnalysisResultEvent event) {
        log.info("Publicando resultado da análise no tópico {}: loanId={}, verdict={}", 
                topicName, event.loanId(), event.verdict());
        
        kafkaTemplate.send(topicName, event.loanId(), event);
    }
}
