package com.agentguard.audit.kafka;

import com.agentguard.audit.AuditRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("kafkaAuditEventPublisher")
@Profile("kafka")
public class KafkaAuditEventPublisher implements AuditEventPublisher {

    private static final Logger log = Logger.getLogger(KafkaAuditEventPublisher.class.getName());

    @Override
    public void publish(AuditRecord record) {
        log.info("[Kafka Profile] Publishing audit event to topic 'agentguard.audit.events': ID=" + record.getId() + ", Hash=" + record.getHash());
    }
}
