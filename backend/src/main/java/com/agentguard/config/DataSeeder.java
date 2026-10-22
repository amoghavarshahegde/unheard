package com.agentguard.config;

import com.agentguard.approvals.ApprovalService;
import com.agentguard.audit.AuditService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuditService auditService;
    private final ApprovalService approvalService;

    public DataSeeder(AuditService auditService, ApprovalService approvalService) {
        this.auditService = auditService;
        this.approvalService = approvalService;
    }

    @Override
    public void run(String... args) {
        if (auditService.getAllRecords().isEmpty()) {
            // Seed record 1: Benign Allowed Action
            auditService.appendRecord(
                    UUID.randomUUID().toString(),
                    "DocBot",
                    "summarize",
                    "ALLOW",
                    0,
                    "Q3 Marketing growth metrics summary",
                    "Clean execution permitted"
            );

            // Seed record 2: Blocked SQL Injection Attack
            auditService.appendRecord(
                    UUID.randomUUID().toString(),
                    "DocBot",
                    "run_sql",
                    "BLOCK",
                    80,
                    "DROP TABLE customer_records;",
                    "Tool 'run_sql' is not permitted for agent 'DocBot' (+80)"
            );

            // Seed record 3: Sensitive PII Redacted on Egress
            auditService.appendRecord(
                    UUID.randomUUID().toString(),
                    "DocBot",
                    "send_email",
                    "HOLD",
                    65,
                    "Client tax PAN [REDACTED:PAN] sent to external audit",
                    "External recipient domain 'external-audit.com' (+35); Sensitive data leaving company perimeter (+30)"
            );

            // Seed 1 Pending Approval in the queue
            approvalService.createPendingApproval(
                    UUID.randomUUID().toString(),
                    "DocBot",
                    "send_email",
                    "recruiter@external-talent.com",
                    65,
                    "External recipient domain (+35); Sensitive data leaving company perimeter (+30)",
                    "Candidate executive compensation includes CTC of [REDACTED:SALARY] with incentive."
            );
        }
    }
}
