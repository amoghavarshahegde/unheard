package com.agentguard.evals;

import com.agentguard.model.ActionRequest;
import com.agentguard.model.GatewayVerdictResponse;
import com.agentguard.model.Verdict;
import com.agentguard.policy.PolicyEngineService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class EvalSuiteService {

    private final PolicyEngineService policyEngineService;
    private final List<EvalScenario> customScenarios = new CopyOnWriteArrayList<>();

    public EvalSuiteService(PolicyEngineService policyEngineService) {
        this.policyEngineService = policyEngineService;
    }

    public void addCustomScenario(String name, boolean isAttack, ActionRequest request, Verdict expectedVerdict, String note) {
        String id = "CUSTOM-" + (customScenarios.size() + 1);
        customScenarios.add(new EvalScenario(id, name, isAttack, request, expectedVerdict, note));
    }

    public List<EvalScenario> getAllScenarios() {
        List<EvalScenario> all = new ArrayList<>(EvalScenario.getStandardSuite());
        all.addAll(customScenarios);
        return all;
    }

    public EvalScorecard runEvaluations() {
        List<EvalScenario> scenarios = getAllScenarios();

        long attacksTotal = 0;
        long normalTotal = 0;

        // Baseline (no gateway): all actions execute unimpeded
        long baselineAttacksSucceeded = 0;

        // With Gateway metrics
        long attacksBlocked = 0;
        long attacksHeld = 0;
        long attacksSucceeded = 0;

        long normalAllowed = 0;
        long normalHeld = 0;
        long normalWronglyBlocked = 0;

        List<ScenarioResult> scenarioResults = new ArrayList<>();
        List<MissDetail> misses = new ArrayList<>();

        for (EvalScenario scenario : scenarios) {
            boolean isAttack = scenario.isAttack();

            if (isAttack) {
                attacksTotal++;
                // In absence of gateway, every tool call executes -> attack succeeds
                baselineAttacksSucceeded++;
            } else {
                normalTotal++;
            }

            // Real evaluation through AgentGuard Gateway Policy Engine
            GatewayVerdictResponse response = policyEngineService.evaluate(scenario.getRequest());
            Verdict actualVerdict = response.getVerdict();

            boolean passedExpected = (actualVerdict == scenario.getExpectedVerdict());

            if (isAttack) {
                if (actualVerdict == Verdict.BLOCK) {
                    attacksBlocked++;
                } else if (actualVerdict == Verdict.HOLD) {
                    attacksHeld++;
                } else { // ALLOW -> Attack slipped through!
                    attacksSucceeded++;
                    misses.add(new MissDetail(
                            scenario.getId(),
                            scenario.getName(),
                            "Attack produced ALLOW verdict (score " + response.getScore() + "). " + scenario.getExpectedOutcomeNote()
                    ));
                }
            } else {
                if (actualVerdict == Verdict.ALLOW) {
                    normalAllowed++;
                } else if (actualVerdict == Verdict.HOLD) {
                    normalHeld++;
                } else {
                    normalWronglyBlocked++;
                }
            }

            scenarioResults.add(new ScenarioResult(
                    scenario.getId(),
                    scenario.getName(),
                    isAttack,
                    scenario.getExpectedVerdict(),
                    actualVerdict,
                    response.getScore(),
                    passedExpected,
                    response.getReasons()
            ));
        }

        return new EvalScorecard(
                Instant.now().toString(),
                scenarios.size(),
                attacksTotal,
                normalTotal,
                new BaselineStats(baselineAttacksSucceeded, attacksTotal, 0),
                new GatewayStats(attacksBlocked, attacksHeld, attacksSucceeded, (attacksBlocked + attacksHeld), attacksTotal),
                new NormalTaskStats(normalAllowed, normalHeld, normalWronglyBlocked, normalTotal),
                misses,
                scenarioResults
        );
    }

    public PolicyDiff evaluateConfigDiff(Runnable configUpdater) {
        EvalScorecard before = runEvaluations();
        configUpdater.run();
        EvalScorecard after = runEvaluations();

        Set<String> beforeMissIds = new HashSet<>();
        before.misses().forEach(m -> beforeMissIds.add(m.scenarioId()));

        Set<String> afterMissIds = new HashSet<>();
        after.misses().forEach(m -> afterMissIds.add(m.scenarioId()));

        List<String> fixedAttacks = new ArrayList<>();
        for (String id : beforeMissIds) {
            if (!afterMissIds.contains(id)) {
                fixedAttacks.add(id);
            }
        }

        List<String> newlyMissedAttacks = new ArrayList<>();
        for (String id : afterMissIds) {
            if (!beforeMissIds.contains(id)) {
                newlyMissedAttacks.add(id);
            }
        }

        return new PolicyDiff(before, after, fixedAttacks, newlyMissedAttacks);
    }

    public record BaselineStats(long attacksSucceeded, long attacksTotal, long attacksMitigated) {}
    public record GatewayStats(long blocked, long held, long succeeded, long totalMitigated, long attacksTotal) {}
    public record NormalTaskStats(long allowed, long held, long wronglyBlocked, long normalTotal) {}
    public record MissDetail(String scenarioId, String name, String details) {}
    public record ScenarioResult(String id, String name, boolean isAttack, Verdict expected, Verdict actual, int score, boolean matchesExpected, List<String> reasons) {}
    public record PolicyDiff(EvalScorecard before, EvalScorecard after, List<String> fixedAttacks, List<String> newlyMissedAttacks) {}

    public record EvalScorecard(
            String timestamp,
            int totalScenarios,
            long totalAttacks,
            long totalNormalTasks,
            BaselineStats noGatewayBaseline,
            GatewayStats gatewayResults,
            NormalTaskStats normalTaskResults,
            List<MissDetail> misses,
            List<ScenarioResult> scenarioResults
    ) {}
}
