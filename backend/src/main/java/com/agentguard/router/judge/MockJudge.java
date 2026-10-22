package com.agentguard.router.judge;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class MockJudge implements LlmJudge {

    @Override
    public LlmJudgeAssessment evaluate(String agent, String tool, String text, int ruleScore, List<String> ruleReasons) {
        if (text == null) {
            text = "";
        }
        String lower = text.toLowerCase();

        // Check for nuanced evasion or high-risk context keywords
        if (lower.contains("confidential leak") || lower.contains("exfiltrate") || lower.contains("bypass security")) {
            int raised = Math.max(ruleScore + 25, 75);
            return new LlmJudgeAssessment(
                    raised,
                    "LLM Judge identified semantic evasion/exfiltration intent in payload context",
                    true
            );
        }

        // Default: agrees with rule score
        return new LlmJudgeAssessment(
                ruleScore,
                "LLM Judge reviewed borderline context; risk profile is consistent with rule evaluation",
                false
        );
    }
}
