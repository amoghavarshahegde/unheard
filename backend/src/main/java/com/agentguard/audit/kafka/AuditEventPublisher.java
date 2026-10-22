package com.agentguard.audit.kafka;

import com.agentguard.audit.AuditRecord;

public interface AuditEventPublisher {
    void publish(AuditRecord record);
}
