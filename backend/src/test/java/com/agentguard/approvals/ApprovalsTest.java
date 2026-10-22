package com.agentguard.approvals;

import com.agentguard.audit.AuditRecord;
import com.agentguard.audit.AuditService;
import com.agentguard.model.ActionArgs;
import com.agentguard.model.ActionRequest;
import com.agentguard.model.GatewayVerdictResponse;
import com.agentguard.model.Verdict;
import com.agentguard.policy.PolicyEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApprovalsTest {

    @Autowired
    private PolicyEngineService policyEngineService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        approvalRepository.deleteAll();
    }

    @Test
    @DisplayName("HOLD verdict automatically creates a PENDING approval record")
    void holdCreatesPendingApproval() {
        // delete_records 10 rows gives +40 score -> HOLD
        ActionArgs args = new ActionArgs(null, "Cleaning draft records", 10, 0.0, List.of());
        ActionRequest req = new ActionRequest("DocBot", "delete_records", args);

        GatewayVerdictResponse response = policyEngineService.evaluate(req);
        assertEquals(Verdict.HOLD, response.getVerdict());

        List<ApprovalRecord> pending = approvalService.getApprovals("PENDING");
        assertFalse(pending.isEmpty());
        ApprovalRecord rec = pending.get(0);
        assertEquals("DocBot", rec.getAgent());
        assertEquals("delete_records", rec.getTool());
        assertEquals(40, rec.getScore());
        assertEquals("PENDING", rec.getStatus());
    }

    @Test
    @DisplayName("Approving an action releases redacted output and writes to audit chain")
    void approveReleasesOutputAndAudits() {
        ApprovalRecord pending = approvalService.createPendingApproval(
                "action-xyz", "DocBot", "send_email", "partner@gmail.com", 65, "Customer phone", "[REDACTED:PHONE]"
        );

        Map<String, Object> released = approvalService.approve(pending.getId(), "LeadAdmin");
        assertEquals("APPROVED_RELEASED", released.get("status"));
        assertEquals("LeadAdmin", released.get("reviewer"));
        assertEquals("[REDACTED:PHONE]", released.get("redactedPayload"));

        ApprovalRecord updated = approvalService.getById(pending.getId()).orElseThrow();
        assertEquals("APPROVED", updated.getStatus());
        assertEquals("LeadAdmin", updated.getReviewer());
        assertNotNull(updated.getReviewedAt());

        // Verify audit log received an APPROVED entry
        List<AuditRecord> auditRecords = auditService.getAllRecords();
        assertTrue(auditRecords.stream().anyMatch(r ->
                "APPROVED".equals(r.getVerdict()) && r.getReasons().contains("LeadAdmin")
        ));
    }

    @Test
    @DisplayName("Denying an action updates status to DENIED and writes to audit chain")
    void denyUpdatesStatusAndAudits() {
        ApprovalRecord pending = approvalService.createPendingApproval(
                "action-abc", "DocBot", "delete_records", null, 60, "Tier 2 delete", "Batch delete 200 rows"
        );

        Map<String, Object> result = approvalService.deny(pending.getId(), "SecOfficer", "Risk too high for unverified user");
        assertEquals("DENIED", result.get("status"));
        assertEquals("SecOfficer", result.get("reviewer"));

        ApprovalRecord updated = approvalService.getById(pending.getId()).orElseThrow();
        assertEquals("DENIED", updated.getStatus());

        List<AuditRecord> auditRecords = auditService.getAllRecords();
        assertTrue(auditRecords.stream().anyMatch(r ->
                "DENIED".equals(r.getVerdict()) && r.getReasons().contains("Risk too high")
        ));
    }
}
