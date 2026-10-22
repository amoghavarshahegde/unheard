package com.agentguard.router;

import com.agentguard.router.judge.LlmJudge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiskRouterTest {

    private RiskRouterService routerService;

    @BeforeEach
    void setUp() {
        // Mock judge that attempts to return 10 (lower score) or 85 (higher score)
        LlmJudge testJudge = (agent, tool, text, ruleScore, ruleReasons) -> {
            if (text != null && text.contains("raise_to_85")) {
                return new LlmJudge.LlmJudgeAssessment(85, "Raised due to elevated contextual risk", true);
            }
            if (text != null && text.contains("try_lower_to_10")) {
                return new LlmJudge.LlmJudgeAssessment(10, "Attempting to lower score", false);
            }
            return new LlmJudge.LlmJudgeAssessment(ruleScore, "Agreed", false);
        };

        routerService = new RiskRouterService(testJudge);
    }

    @Test
    @DisplayName("Fast path: score < 30 bypasses model and saves cost")
    void fastPathLowRisk() {
        List<String> reasons = new ArrayList<>();
        RiskRouterService.RouterAssessment res = routerService.evaluateBorderline("DocBot", "read_document", "Invoice text", 0, reasons);

        assertEquals(0, res.finalScore());
        assertFalse(res.routerInfo().isModelCalled());
        assertEquals(0, routerService.getModelCalls());
        assertEquals(1, routerService.getCallsSaved());
        assertTrue(routerService.getEstimatedCostSavedUsd() > 0);
    }

    @Test
    @DisplayName("Fast path: score >= 75 immediately blocks and bypasses model")
    void fastPathHighRisk() {
        List<String> reasons = new ArrayList<>();
        RiskRouterService.RouterAssessment res = routerService.evaluateBorderline("DocBot", "shell", "rm -rf", 80, reasons);

        assertEquals(80, res.finalScore());
        assertFalse(res.routerInfo().isModelCalled());
        assertEquals(0, routerService.getModelCalls());
    }

    @Test
    @DisplayName("Borderline score (30-74) invokes LLM judge")
    void borderlineInvokesModel() {
        List<String> reasons = new ArrayList<>();
        RiskRouterService.RouterAssessment res = routerService.evaluateBorderline("DocBot", "send_email", "Email text", 35, reasons);

        assertEquals(35, res.finalScore());
        assertTrue(res.routerInfo().isModelCalled());
        assertEquals(1, routerService.getModelCalls());
    }

    @Test
    @DisplayName("LLM Judge may raise score from borderline to BLOCK")
    void judgeMayRaiseScore() {
        List<String> reasons = new ArrayList<>();
        RiskRouterService.RouterAssessment res = routerService.evaluateBorderline("DocBot", "send_email", "raise_to_85 content", 45, reasons);

        assertEquals(85, res.finalScore());
        assertTrue(res.routerInfo().isModelCalled());
        assertTrue(reasons.stream().anyMatch(r -> r.contains("LLM Judge escalated risk score from 45 to 85")));
    }

    @Test
    @DisplayName("Monotonic constraint: LLM Judge CANNOT lower score (strictly preserved)")
    void judgeCannotLowerScore() {
        List<String> reasons = new ArrayList<>();
        // Rule score is 50, judge tries to return 10
        RiskRouterService.RouterAssessment res = routerService.evaluateBorderline("DocBot", "send_email", "try_lower_to_10 content", 50, reasons);

        assertEquals(50, res.finalScore(), "Score must strictly stay at 50, judge cannot lower it");
        assertTrue(res.routerInfo().isModelCalled());
    }
}
