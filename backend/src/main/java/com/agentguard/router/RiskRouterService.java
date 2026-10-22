package com.agentguard.router;

import com.agentguard.model.RouterInfo;
import com.agentguard.router.judge.LlmJudge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RiskRouterService {

    public static final double ESTIMATED_COST_PER_CALL_USD = 0.002;

    private final LlmJudge llmJudge;
    private final AtomicLong totalCalls = new AtomicLong(0);
    private final AtomicLong modelCalls = new AtomicLong(0);

    public RiskRouterService(LlmJudge llmJudge) {
        this.llmJudge = llmJudge;
    }

    public RouterAssessment evaluateBorderline(String agent, String tool, String text, int ruleScore, List<String> reasons) {
        totalCalls.incrementAndGet();

        // LLM judge is only invoked in borderline score window: 30 to 74
        if (ruleScore >= 30 && ruleScore <= 74) {
            modelCalls.incrementAndGet();
            LlmJudge.LlmJudgeAssessment assessment = llmJudge.evaluate(agent, tool, text, ruleScore, reasons);

            // Strict Monotonic Rule: Judge may ONLY raise the score, never lower it
            if (assessment.suggestedScore() > ruleScore) {
                int escalatedScore = Math.min(100, assessment.suggestedScore());
                reasons.add("LLM Judge escalated risk score from " + ruleScore + " to " + escalatedScore + ": " + assessment.reasoning());
                return new RouterAssessment(
                        escalatedScore,
                        new RouterInfo(true, getEstimatedCostSavedUsd(), "Borderline score (" + ruleScore + ") -> LLM Judge invoked. Score escalated to " + escalatedScore)
                );
            } else {
                return new RouterAssessment(
                        ruleScore, // Strictly preserved, cannot be lowered
                        new RouterInfo(true, getEstimatedCostSavedUsd(), "Borderline score (" + ruleScore + ") -> LLM Judge concurred with deterministic rule score")
                );
            }
        }

        // Fast path: < 30 or >= 75 saves model call
        String detail = ruleScore < 30
                ? "Low-risk fast path (score " + ruleScore + " < 30): LLM judge bypassed"
                : "High-risk immediate block (score " + ruleScore + " >= 75): LLM judge bypassed";

        return new RouterAssessment(
                ruleScore,
                new RouterInfo(false, getEstimatedCostSavedUsd(), detail)
        );
    }

    public long getTotalCalls() {
        return totalCalls.get();
    }

    public long getModelCalls() {
        return modelCalls.get();
    }

    public long getCallsSaved() {
        return Math.max(0, totalCalls.get() - modelCalls.get());
    }

    public double getEstimatedCostSavedUsd() {
        return getCallsSaved() * ESTIMATED_COST_PER_CALL_USD;
    }

    public void resetTelemetry() {
        totalCalls.set(0);
        modelCalls.set(0);
    }

    public record RouterAssessment(int finalScore, RouterInfo routerInfo) {}
}
