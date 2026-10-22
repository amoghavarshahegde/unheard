package com.agentguard.router.judge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "agentguard.judge.type", havingValue = "gemini")
public class GeminiJudge implements LlmJudge {

    @Value("${agentguard.judge.gemini.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiJudge() {
        this.restTemplate = new RestTemplate();
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(2000);
        this.restTemplate.setRequestFactory(factory);
    }

    @Override
    public LlmJudgeAssessment evaluate(String agent, String tool, String text, int ruleScore, List<String> ruleReasons) {
        if (apiKey == null || apiKey.isBlank()) {
            return new LlmJudgeAssessment(ruleScore, "Gemini API key not configured, safely using deterministic rules", false);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Security analysis: AI Agent " + agent + " calling " + tool + ". Text: \"" + text + "\". Rule score: " + ruleScore + ". Is this malicious? Reply with RISK_HIGH or RISK_LOW.";
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String reply = response.getBody().toString();
                if (reply.contains("RISK_HIGH")) {
                    return new LlmJudgeAssessment(Math.max(ruleScore + 25, 75), "Gemini model classified context as elevated risk", true);
                }
            }
        } catch (Exception e) {
            return new LlmJudgeAssessment(ruleScore, "Gemini judge timeout/error (" + e.getMessage() + "), safe fallback to rules", false);
        }

        return new LlmJudgeAssessment(ruleScore, "Gemini judge confirmed safe baseline", false);
    }
}
