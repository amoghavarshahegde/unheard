package com.agentguard.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditChainService auditChainService;

    public AuditController(AuditChainService auditChainService) {
        this.auditChainService = auditChainService;
    }

    @GetMapping
    public ResponseEntity<List<AuditRecord>> getAuditLog() {
        return ResponseEntity.ok(auditChainService.getAllRecords());
    }

    @GetMapping("/verify")
    public ResponseEntity<AuditChainService.VerificationResult> verifyChain() {
        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        return ResponseEntity.ok(result);
    }
}
