package com.marcelo.loan.config;

import com.marcelo.loan.entity.enums.LoanEvent;
import com.marcelo.loan.entity.enums.LoanStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class LoanStateMachineConfig extends StateMachineConfigurerAdapter<LoanStatus, LoanEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<LoanStatus, LoanEvent> states) throws Exception {
        states.withStates()
                .initial(LoanStatus.PENDING)
                .states(EnumSet.allOf(LoanStatus.class))
                .end(LoanStatus.APPROVED)
                .end(LoanStatus.REJECTED)
                .end(LoanStatus.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LoanStatus, LoanEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(LoanStatus.PENDING).target(LoanStatus.UNDER_ANALYSIS).event(LoanEvent.SUBMIT)
                .and()
                .withExternal()
                .source(LoanStatus.PENDING).target(LoanStatus.CANCELLED).event(LoanEvent.CANCEL)
                .and()
                .withExternal()
                .source(LoanStatus.UNDER_ANALYSIS).target(LoanStatus.APPROVED).event(LoanEvent.APPROVE)
                .and()
                .withExternal()
                .source(LoanStatus.UNDER_ANALYSIS).target(LoanStatus.REJECTED).event(LoanEvent.REJECT);
    }
}
