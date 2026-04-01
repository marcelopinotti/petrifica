package com.marcelo.loan.service;

import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.exception.InvalidStatusTransitionException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoanStateService {

    private final StateMachineFactory<LoanStatus, LoanEvent> stateMachineFactory;

    public LoanStatus processEvent(LoanStatus currentStatus, LoanEvent event) {
        StateMachine<LoanStatus, LoanEvent> sm = stateMachineFactory.getStateMachine();
        sm.stopReactively().block();
        sm.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachineReactively(
                        new DefaultStateMachineContext<>(currentStatus, null, null, null)).block());
        sm.startReactively().block();

        Message<LoanEvent> message = MessageBuilder.withPayload(event).build();
        boolean accepted = Boolean.TRUE.equals(
                sm.sendEvent(Mono.just(message))
                        .map(r -> r.getResultType() == StateMachineEventResult.ResultType.ACCEPTED)
                        .reduce(false, (a, b) -> a || b)
                        .block()
        );

        sm.stopReactively().block();

        if (accepted) {
            return sm.getState().getId();
        } else {
            throw new InvalidStatusTransitionException(currentStatus, event);
        }
    }
}
