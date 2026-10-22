package com.agentguard.compliance;

import com.agentguard.audit.AuditRecord;
import com.agentguard.audit.AuditService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ComplianceReportService {

    private final AuditService auditService;

    public ComplianceReportService(AuditService auditService) {
        this.auditService = auditService;
    }

    public ComplianceReport generateReport() {
        List<AuditRecord> records = auditService.getAllRecords();

        long totalActions = records.size();
        long allowedCount = records.stream().filter(r -> "ALLOW".equalsIgnoreCase(r.getVerdict()) || "APPROVED".equalsIgnoreCase(r.getVerdict())).count();
        long heldCount = records.stream().filter(r -> "HOLD".equalsIgnoreCase(r.getVerdict()) || "PENDING".equalsIgnoreCase(r.getVerdict())).count();
        long blockedCount = records.stream().filter(r -> "BLOCK".equalsIgnoreCase(r.getVerdict()) || "DENIED".equalsIgnoreCase(r.getVerdict())).count();

        // OWASP LLM01: Prompt Injection
        long llm01Events = records.stream().filter(r ->
                r.getReasons() != null && (
                        r.getReasons().contains("Prompt-injection") ||
                        r.getReasons().contains("developer mode") ||
                        r.getReasons().contains("ignore previous") ||
                        r.getReasons().contains("reveal the system") ||
                        r.getReasons().contains("forward all")
                )
        ).count();

        // OWASP LLM02: Sensitive Information Disclosure
        long llm02Events = records.stream().filter(r ->
                r.getReasons() != null && (
                        r.getReasons().contains("Sensitive data") ||
                        r.getReasons().contains("PAN") ||
                        r.getReasons().contains("Aadhaar") ||
                        r.getReasons().contains("phone") ||
                        r.getReasons().contains("Salary")
                )
        ).count();

        // OWASP LLM06: Excessive Agency
        long llm06Events = records.stream().filter(r ->
                r.getReasons() != null && (
                        r.getReasons().contains("not permitted") ||
                        r.getReasons().contains("delete_records") ||
                        r.getReasons().contains("attachments detected")
                )
        ).count();

        List<OwaspControl> controls = List.of(
                new OwaspControl(
                        "LLM01:2025",
                        "Prompt Injection",
                        "Deterministic heuristic patterns and regex filters intercept malicious system prompts, jailbreaks, and indirect exfiltration cues.",
                        llm01Events,
                        "ACTIVE (Enforced at Gateway)"
                ),
                new OwaspControl(
                        "LLM02:2025",
                        "Sensitive Information Disclosure",
                        "PS 2.2 Context-Aware Privacy Lens detects PAN, Aadhaar, Salary, and differentiates public helpline cues from customer PII.",
                        llm02Events,
                        "ACTIVE (Auto-Redaction [REDACTED:TYPE] on External Egress)"
                ),
                new OwaspControl(
                        "LLM06:2025",
                        "Excessive Agency",
                        "Agent-to-tool capabilities are strictly whitelisted (DocBot limited to 4 tools). Delete operations enforce batch size thresholds and human approval.",
                        llm06Events,
                        "ACTIVE (Granular Tier Limits & HOLD Escalations)"
                )
        );

        String verifiedAt = Instant.now().toString();
        var verifyRes = auditService.verifyChain();

        return new ComplianceReport(
                "AgentGuard Security & AI Governance Compliance Audit Report",
                "PS 2.1 AI Agent Safety and Control & PS 2.2 Context-Aware Data Privacy",
                verifiedAt,
                verifyRes.valid(),
                totalActions,
                allowedCount,
                heldCount,
                blockedCount,
                controls
        );
    }

    public record OwaspControl(String id, String name, String safeguard, long detectionsCount, String status) {}

    public record ComplianceReport(
            String reportTitle,
            String standard,
            String generatedAt,
            boolean chainIntegrityValid,
            long totalAuditRecords,
            long allowedCount,
            long heldCount,
            long blockedCount,
            List<OwaspControl> controls
    ) {}
}
