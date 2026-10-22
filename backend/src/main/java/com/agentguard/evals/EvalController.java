package com.agentguard.evals;

import com.agentguard.model.ActionRequest;
import com.agentguard.model.Verdict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evals")
public class EvalController {

    private final EvalSuiteService evalSuiteService;

    public EvalController(EvalSuiteService evalSuiteService) {
        this.evalSuiteService = evalSuiteService;
    }

    @PostMapping("/run")
    public ResponseEntity<EvalSuiteService.EvalScorecard> runEvals() {
        return ResponseEntity.ok(evalSuiteService.runEvaluations());
    }

    @GetMapping("/scenarios")
    public ResponseEntity<List<EvalScenario>> getScenarios() {
        return ResponseEntity.ok(evalSuiteService.getAllScenarios());
    }

    @PostMapping("/custom")
    public ResponseEntity<Map<String, Object>> addCustomScenario(@RequestBody Map<String, Object> body) {
        String name = (String) body.getOrDefault("name", "Custom Red-Team Scenario");
        boolean isAttack = (Boolean) body.getOrDefault("isAttack", true);
        String agent = (String) body.getOrDefault("agent", "DocBot");
        String tool = (String) body.getOrDefault("tool", "send_email");

        Map<String, Object> rawArgs = (Map<String, Object>) body.get("args");
        com.agentguard.model.ActionArgs args = new com.agentguard.model.ActionArgs();
        if (rawArgs != null) {
            args.setTo((String) rawArgs.get("to"));
            args.setText((String) rawArgs.get("text"));
            if (rawArgs.get("rows") != null) args.setRows(((Number) rawArgs.get("rows")).intValue());
            if (rawArgs.get("amount") != null) args.setAmount(((Number) rawArgs.get("amount")).doubleValue());
        }

        ActionRequest req = new ActionRequest(agent, tool, args);
        evalSuiteService.addCustomScenario(name, isAttack, req, Verdict.BLOCK, "Added from Red-Team Sandbox");

        return ResponseEntity.ok(Map.of(
                "message", "Scenario added to evaluation benchmark suite",
                "totalScenarios", evalSuiteService.getAllScenarios().size()
        ));
    }
}
