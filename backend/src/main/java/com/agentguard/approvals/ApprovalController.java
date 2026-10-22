package com.agentguard.approvals;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalRecord>> getApprovals(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(approvalService.getApprovals(status));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveAction(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String reviewer = body != null ? body.get("reviewer") : null;
        try {
            Map<String, Object> result = approvalService.approve(id, reviewer);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/deny")
    public ResponseEntity<?> denyAction(@PathVariable String id, @RequestBody(required = false) Map<String, String> body) {
        String reviewer = body != null ? body.get("reviewer") : null;
        String reason = body != null ? body.get("reason") : null;
        try {
            Map<String, Object> result = approvalService.deny(id, reviewer, reason);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
