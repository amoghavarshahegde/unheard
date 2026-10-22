package com.agentguard.audit.kafka;

import com.agentguard.audit.AuditRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(name = "kafkaAuditEventPublisher")
public class NoopAuditEventPublisher implements AuditEventPublisher {

    @Override
    public void publish(AuditRecord record) {
        // No-op when Kafka profile is not active
    }
}
