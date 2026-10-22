package com.agentguard.approvals;

import com.agentguard.audit.AuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class ApprovalService {

    private final ApprovalRepository repository;
    private final AuditService auditService;

    public ApprovalService(ApprovalRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public ApprovalRecord createPendingApproval(String actionId, String agent, String tool,
                                                String recipient, int score, String reasons,
                                                String redactedPayload) {
        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        ApprovalRecord record = new ApprovalRecord(
                id, actionId, agent, tool, recipient,
                score, reasons, redactedPayload,
                "PENDING", null, null, now
        );

        return repository.save(record);
    }

    public List<ApprovalRecord> getApprovals(String status) {
        if (status != null && !status.isBlank()) {
            return repository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
        }
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<ApprovalRecord> getById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public Map<String, Object> approve(String id, String reviewer) {
        ApprovalRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval record not found: " + id));

        if (!"PENDING".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalStateException("Action is already " + record.getStatus());
        }

        String reviewerName = (reviewer != null && !reviewer.isBlank()) ? reviewer : "SecOps-Officer";
        String now = Instant.now().toString();

        record.setStatus("APPROVED");
        record.setReviewer(reviewerName);
        record.setReviewedAt(now);
        repository.save(record);

        // Every approval is appended to the tamper-evident audit hash chain
        auditService.appendRecord(
                "appr-" + record.getId(),
                record.getAgent(),
                record.getTool(),
                "APPROVED",
                record.getScore(),
                record.getRedactedPayload(),
                "Approved by human reviewer: " + reviewerName
        );

        Map<String, Object> releasedOutput = new HashMap<>();
        releasedOutput.put("approvalId", record.getId());
        releasedOutput.put("actionId", record.getActionId());
        releasedOutput.put("status", "APPROVED_RELEASED");
        releasedOutput.put("tool", record.getTool());
        releasedOutput.put("reviewer", reviewerName);
        releasedOutput.put("reviewedAt", now);
        releasedOutput.put("redactedPayload", record.getRedactedPayload());

        return releasedOutput;
    }

    @Transactional
    public Map<String, Object> deny(String id, String reviewer, String reason) {
        ApprovalRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval record not found: " + id));

        if (!"PENDING".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalStateException("Action is already " + record.getStatus());
        }

        String reviewerName = (reviewer != null && !reviewer.isBlank()) ? reviewer : "SecOps-Officer";
        String denialReason = (reason != null && !reason.isBlank()) ? reason : "Rejected by human reviewer";
        String now = Instant.now().toString();

        record.setStatus("DENIED");
        record.setReviewer(reviewerName);
        record.setReviewedAt(now);
        repository.save(record);

        // Every denial is appended to the tamper-evident audit hash chain
        auditService.appendRecord(
                "deny-" + record.getId(),
                record.getAgent(),
                record.getTool(),
                "DENIED",
                record.getScore(),
                record.getRedactedPayload(),
                "Denied by human reviewer " + reviewerName + ": " + denialReason
        );

        return Map.of(
                "approvalId", record.getId(),
                "actionId", record.getActionId(),
                "status", "DENIED",
                "reviewer", reviewerName,
                "reviewedAt", now,
                "reason", denialReason
        );
    }
}
