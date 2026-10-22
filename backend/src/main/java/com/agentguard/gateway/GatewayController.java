package com.agentguard.gateway;

import com.agentguard.audit.AuditRecord;
import com.agentguard.audit.AuditService;
import com.agentguard.model.ActionRequest;
import com.agentguard.model.GatewayVerdictResponse;
import com.agentguard.policy.KillSwitchService;
import com.agentguard.policy.PolicyEngineService;
import com.agentguard.router.RiskRouterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController {

    private final PolicyEngineService policyEngineService;
    private final KillSwitchService killSwitchService;
    private final RiskRouterService riskRouterService;
    private final AuditService auditService;

    public GatewayController(PolicyEngineService policyEngineService,
                             KillSwitchService killSwitchService,
                             RiskRouterService riskRouterService,
                             AuditService auditService) {
        this.policyEngineService = policyEngineService;
        this.killSwitchService = killSwitchService;
        this.riskRouterService = riskRouterService;
        this.auditService = auditService;
    }

    @PostMapping("/actions")
    public ResponseEntity<GatewayVerdictResponse> processAction(@Valid @RequestBody ActionRequest request) {
        GatewayVerdictResponse response = policyEngineService.evaluate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<AuditRecord> records = auditService.getAllRecords();

        long total = records.size();
        long allowed = records.stream().filter(r -> "ALLOW".equalsIgnoreCase(r.getVerdict()) || "APPROVED".equalsIgnoreCase(r.getVerdict())).count();
        long held = records.stream().filter(r -> "HOLD".equalsIgnoreCase(r.getVerdict()) || "PENDING".equalsIgnoreCase(r.getVerdict())).count();
        long blocked = records.stream().filter(r -> "BLOCK".equalsIgnoreCase(r.getVerdict()) || "DENIED".equalsIgnoreCase(r.getVerdict())).count();

        boolean chainValid = auditService.verifyChain().valid();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActions", total);
        stats.put("allowed", allowed);
        stats.put("held", held);
        stats.put("blocked", blocked);
        stats.put("killSwitchActive", killSwitchService.isActive());
        stats.put("modelCalls", riskRouterService.getModelCalls());
        stats.put("callsSaved", riskRouterService.getCallsSaved());
        stats.put("estimatedCostSavedUsd", riskRouterService.getEstimatedCostSavedUsd());
        stats.put("chainIntegrityValid", chainValid);

        return ResponseEntity.ok(stats);
    }
}
