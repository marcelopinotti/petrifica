package com.marcelo.loan.service;

import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import com.marcelo.loan.exception.InvalidStatusTransitionException;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanStateService {

    private final StateMachine<LoanStatus, LoanEvent> stateMachine;

    public LoanStatus processEvent(LoanStatus currentStatus, LoanEvent event) {
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachine(new DefaultStateMachineContext<>(currentStatus,null,null,null)));
        stateMachine.start();
        boolean accepted = stateMachine.sendEvent(event);
        if(accepted) {
            return stateMachine.getState().getId();
        } else {
            throw new InvalidStatusTransitionException(currentStatus,event);
        }

    }
}
