package com.agentguard.router.judge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "agentguard.judge.type", havingValue = "ollama")
public class OllamaJudge implements LlmJudge {

    @Value("${agentguard.judge.ollama.url:http://localhost:11434}")
    private String ollamaUrl;

    @Value("${agentguard.judge.ollama.model:llama3}")
    private String model;

    private final RestTemplate restTemplate;

    public OllamaJudge() {
        this.restTemplate = new RestTemplate();
        // Configure 2-second timeout
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(2000);
        this.restTemplate.setRequestFactory(factory);
    }

    @Override
    public LlmJudgeAssessment evaluate(String agent, String tool, String text, int ruleScore, List<String> ruleReasons) {
        String prompt = "Evaluate security risk for agent '" + agent + "' calling tool '" + tool + "' with text: \"" + text + "\". " +
                "Rule risk score is " + ruleScore + ". If malicious evasion or unauthorized exfiltration is detected, escalate score.";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of(
                    "model", model,
                    "prompt", prompt,
                    "stream", false
            );
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ollamaUrl + "/api/generate", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String reply = (String) response.getBody().get("response");
                if (reply != null && (reply.toLowerCase().contains("malicious") || reply.toLowerCase().contains("high risk"))) {
                    return new LlmJudgeAssessment(Math.max(ruleScore + 20, 75), "Ollama LLaMA-3 flagged semantic risk: " + reply.substring(0, Math.min(80, reply.length())), true);
                }
            }
        } catch (Exception e) {
            // Safe fallback to rule score on timeout or connection error
            return new LlmJudgeAssessment(ruleScore, "Ollama judge unreachable/timed out (" + e.getMessage() + "), safely fell back to rules", false);
        }

        return new LlmJudgeAssessment(ruleScore, "Ollama LLM judged context consistent with baseline rules", false);
    }
}
