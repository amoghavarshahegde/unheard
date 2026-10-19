package com.unheard.controller;

import com.unheard.dto.CoordinatorDecisionDto;
import com.unheard.dto.PagedResponse;
import com.unheard.service.CoordinatorDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "Coordinator decision audit trail and history")
public class AuditController {

    private final CoordinatorDecisionService decisionService;

    public AuditController(CoordinatorDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @GetMapping
    @Operation(summary = "Get paginated audit history of coordinator decisions")
    public ResponseEntity<PagedResponse<CoordinatorDecisionDto>> getAudit(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(decisionService.getAuditLog(page, size));
    }
}
