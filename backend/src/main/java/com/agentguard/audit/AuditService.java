package com.agentguard.audit;

import java.util.List;

public interface AuditService {
    AuditRecord appendRecord(String id, String agent, String tool, String verdict,
                             int score, String redactedPayload, String reasons);

    List<AuditRecord> getAllRecords();

    AuditChainService.VerificationResult verifyChain();
}
