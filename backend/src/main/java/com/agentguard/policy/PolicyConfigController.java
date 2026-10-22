package com.agentguard.policy;

import com.agentguard.evals.EvalSuiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/policy")
public class PolicyConfigController {

    private final PolicyEngineService policyEngineService;
    private final KillSwitchService killSwitchService;
    private final EvalSuiteService evalSuiteService;

    public PolicyConfigController(PolicyEngineService policyEngineService,
                                  KillSwitchService killSwitchService,
                                  EvalSuiteService evalSuiteService) {
        this.policyEngineService = policyEngineService;
        this.killSwitchService = killSwitchService;
        this.evalSuiteService = evalSuiteService;
    }

    @GetMapping("/config")
    public ResponseEntity<PolicyConfig> getConfig() {
        return ResponseEntity.ok(policyEngineService.getConfig());
    }

    /**
     * Updates policy configuration, automatically re-runs evaluation suite,
     * and returns the new config along with the before/after attack scorecard diff.
     */
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody PolicyConfig newConfig) {
        EvalSuiteService.PolicyDiff diff = evalSuiteService.evaluateConfigDiff(() -> {
            policyEngineService.updateConfig(newConfig);
        });

        return ResponseEntity.ok(Map.of(
                "config", policyEngineService.getConfig(),
                "diff", diff
        ));
    }

    @GetMapping("/killswitch")
    public ResponseEntity<Map<String, Object>> getKillSwitchStatus() {
        return ResponseEntity.ok(Map.of("active", killSwitchService.isActive()));
    }

    @PostMapping("/killswitch/toggle")
    public ResponseEntity<Map<String, Object>> toggleKillSwitch() {
        boolean newState = killSwitchService.toggle();
        return ResponseEntity.ok(Map.of(
                "active", newState,
                "message", newState ? "Kill switch engaged: All tool executions blocked." : "Kill switch disengaged."
        ));
    }
}
