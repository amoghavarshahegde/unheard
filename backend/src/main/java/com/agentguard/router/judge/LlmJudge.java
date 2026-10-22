package com.agentguard.router.judge;

import java.util.List;

public interface LlmJudge {
    /**
     * Evaluates action context and returns a risk score assessment.
     * @param agent agent name
     * @param tool tool name
     * @param text prompt or payload text
     * @param ruleScore initial score from deterministic rules
     * @param ruleReasons reasons already flagged by rules
     * @return assessment with a suggested risk score and reasoning
     */
    LlmJudgeAssessment evaluate(String agent, String tool, String text, int ruleScore, List<String> ruleReasons);

    record LlmJudgeAssessment(int suggestedScore, String reasoning, boolean raisedScore) {}
}
