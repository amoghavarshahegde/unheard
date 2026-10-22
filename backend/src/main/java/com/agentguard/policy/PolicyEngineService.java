package com.agentguard.policy;

import com.agentguard.approvals.ApprovalService;
import com.agentguard.audit.AuditService;
import com.agentguard.model.*;
import com.agentguard.privacy.ContextAwarePrivacyLens;
import com.agentguard.router.RiskRouterService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class PolicyEngineService {

    private final PolicyConfig config = new PolicyConfig();
    private final KillSwitchService killSwitchService;
    private final ContextAwarePrivacyLens privacyLens;
    private final AuditService auditService;
    private final RiskRouterService riskRouterService;
    private final ApprovalService approvalService;

    public PolicyEngineService(KillSwitchService killSwitchService,
                               ContextAwarePrivacyLens privacyLens,
                               AuditService auditService,
                               RiskRouterService riskRouterService,
                               ApprovalService approvalService) {
        this.killSwitchService = killSwitchService;
        this.privacyLens = privacyLens;
        this.auditService = auditService;
        this.riskRouterService = riskRouterService;
        this.approvalService = approvalService;
    }

    public PolicyConfig getConfig() {
        return config;
    }

    public synchronized void updateConfig(PolicyConfig newConfig) {
        if (newConfig != null) {
            this.config.setAllowedTools(newConfig.getAllowedTools());
            this.config.setPromptInjectionPatterns(newConfig.getPromptInjectionPatterns());
            this.config.setInternalDomain(newConfig.getInternalDomain());
            this.config.setUnpermittedToolScore(newConfig.getUnpermittedToolScore());
            this.config.setPromptInjectionScore(newConfig.getPromptInjectionScore());
            this.config.setExternalRecipientScore(newConfig.getExternalRecipientScore());
            this.config.setSensitiveExternalScore(newConfig.getSensitiveExternalScore());
            this.config.setSensitiveInternalScore(newConfig.getSensitiveInternalScore());
            this.config.setMaxAttachments(newConfig.getMaxAttachments());
            this.config.setExcessiveAttachmentsScore(newConfig.getExcessiveAttachmentsScore());
            this.config.setDeleteTier1MaxRows(newConfig.getDeleteTier1MaxRows());
            this.config.setDeleteTier1Score(newConfig.getDeleteTier1Score());
            this.config.setDeleteTier2MaxRows(newConfig.getDeleteTier2MaxRows());
            this.config.setDeleteTier2Score(newConfig.getDeleteTier2Score());
            this.config.setDeleteTier3Score(newConfig.getDeleteTier3Score());
            this.config.setAllowThreshold(newConfig.getAllowThreshold());
            this.config.setBlockThreshold(newConfig.getBlockThreshold());
        }
    }

    public GatewayVerdictResponse evaluate(ActionRequest request) {
        String id = UUID.randomUUID().toString();
        String timestamp = Instant.now().toString();
        String agent = request.getAgent();
        String tool = request.getTool();
        ActionArgs args = request.getArgs() != null ? request.getArgs() : new ActionArgs();

        List<String> reasons = new ArrayList<>();
        int score = 0;

        // 1. Kill Switch check
        if (killSwitchService.isActive()) {
            reasons.add("EMERGENCY KILL SWITCH ACTIVE: all actions blocked unconditionally");
            Verdict verdict = Verdict.BLOCK;
            score = 100;
            Map<String, Object> safeOutput = createSafeOutput(verdict, tool, "Kill switch active", "");
            auditService.appendRecord(id, agent, tool, verdict.name(), score, "", String.join("; ", reasons));
            return new GatewayVerdictResponse(
                    id, agent, tool, verdict, score,
                    reasons, List.of(), safeOutput,
                    new RouterInfo(false, riskRouterService.getEstimatedCostSavedUsd(), "Emergency override"),
                    timestamp
            );
        }

        // 2. Tool permission check
        List<String> agentAllowedTools = config.getAllowedTools().get(agent);
        boolean toolAllowed = agentAllowedTools != null && agentAllowedTools.stream()
                .anyMatch(t -> t.equalsIgnoreCase(tool));
        if (!toolAllowed) {
            score += config.getUnpermittedToolScore();
            reasons.add("Tool '" + tool + "' is not permitted for agent '" + agent + "' (+" + config.getUnpermittedToolScore() + ")");
        }

        // 3. Prompt-injection pattern in text
        String rawText = args.getText() != null ? args.getText() : "";
        String matchedInjectionPattern = findInjectionMatch(rawText);
        if (matchedInjectionPattern != null) {
            score += config.getPromptInjectionScore();
            reasons.add("Prompt-injection pattern detected: \"" + matchedInjectionPattern + "\" (+" + config.getPromptInjectionScore() + ")");
        }

        // 4. Recipient domain check
        boolean isExternal = false;
        String recipient = args.getTo();
        if (recipient != null && !recipient.isBlank()) {
            String domain = extractDomain(recipient);
            if (!config.getInternalDomain().equalsIgnoreCase(domain)) {
                isExternal = true;
                score += config.getExternalRecipientScore();
                reasons.add("External recipient domain '" + domain + "' (expected exact '" + config.getInternalDomain() + "') (+" + config.getExternalRecipientScore() + ")");
            }
        }

        // 5. Context-aware sensitive data detection & Redaction
        List<FindingDto> findings = privacyLens.detect(rawText);
        boolean hasSensitiveData = findings.stream().anyMatch(FindingDto::isSensitive);
        if (hasSensitiveData) {
            if (isExternal) {
                score += config.getSensitiveExternalScore();
                reasons.add("Sensitive data leaving company perimeter to external destination (+" + config.getSensitiveExternalScore() + ")");
            } else {
                score += config.getSensitiveInternalScore();
                reasons.add("Sensitive data on internal route (+" + config.getSensitiveInternalScore() + ", logged only)");
            }
        }

        // Redacted text is produced for external egress; on internal egress, sensitive text remains readable
        String redactedText = privacyLens.redact(rawText, isExternal);

        // 6. Excessive attachments check
        int attachmentCount = args.getAttachments() != null ? args.getAttachments().size() : 0;
        if (attachmentCount > config.getMaxAttachments()) {
            score += config.getExcessiveAttachmentsScore();
            reasons.add("More than " + config.getMaxAttachments() + " attachments detected (" + attachmentCount + ") (+" + config.getExcessiveAttachmentsScore() + ")");
        }

        // 7. delete_records row thresholds check
        if ("delete_records".equalsIgnoreCase(tool)) {
            int rows = args.getRows() != null ? args.getRows() : 0;
            if (rows >= 1 && rows <= config.getDeleteTier1MaxRows()) {
                score += config.getDeleteTier1Score();
                reasons.add("delete_records row count " + rows + " (Tier 1: 1-" + config.getDeleteTier1MaxRows() + " rows) (+" + config.getDeleteTier1Score() + ")");
            } else if (rows > config.getDeleteTier1MaxRows() && rows <= config.getDeleteTier2MaxRows()) {
                score += config.getDeleteTier2Score();
                reasons.add("delete_records row count " + rows + " (Tier 2: " + (config.getDeleteTier1MaxRows() + 1) + "-" + config.getDeleteTier2MaxRows() + " rows) (+" + config.getDeleteTier2Score() + ")");
            } else if (rows > config.getDeleteTier2MaxRows()) {
                score += config.getDeleteTier3Score();
                reasons.add("delete_records row count " + rows + " (Tier 3: >= " + (config.getDeleteTier2MaxRows() + 1) + " rows) (+" + config.getDeleteTier3Score() + ")");
            }
        }

        // Cap rule score at 100 before router
        score = Math.min(100, Math.max(0, score));

        // 8. Risk Router: Borderline evaluation with LLM Judge (scores 30 to 74)
        RiskRouterService.RouterAssessment routerAssessment =
                riskRouterService.evaluateBorderline(agent, tool, rawText, score, reasons);
        score = routerAssessment.finalScore();
        RouterInfo routerInfo = routerAssessment.routerInfo();

        // Determine verdict
        Verdict verdict;
        if (score < config.getAllowThreshold()) {
            verdict = Verdict.ALLOW;
        } else if (score < config.getBlockThreshold()) {
            verdict = Verdict.HOLD;
        } else {
            verdict = Verdict.BLOCK;
        }

        // If action is on HOLD, automatically queue it for human-in-the-loop approval
        if (verdict == Verdict.HOLD) {
            approvalService.createPendingApproval(
                    id, agent, tool, recipient, score,
                    String.join("; ", reasons), redactedText
            );
        }

        Map<String, Object> safeOutput = createSafeOutput(verdict, tool,
                verdict == Verdict.ALLOW ? "Mock execution succeeded" : (verdict == Verdict.HOLD ? "Action queued for human approval" : "Execution withheld by policy"),
                redactedText);

        // Append to cryptographically sealed audit chain (storing ONLY redacted text)
        auditService.appendRecord(id, agent, tool, verdict.name(), score, redactedText, String.join("; ", reasons));

        return new GatewayVerdictResponse(
                id, agent, tool, verdict, score,
                reasons, findings, safeOutput, routerInfo, timestamp
        );
    }

    private String findInjectionMatch(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (String patternStr : config.getPromptInjectionPatterns()) {
            if (patternStr.contains(".*")) {
                Pattern regex = Pattern.compile("(?i)" + patternStr);
                if (regex.matcher(text).find()) {
                    return patternStr;
                }
            } else {
                if (text.toLowerCase().contains(patternStr.toLowerCase())) {
                    return patternStr;
                }
            }
        }
        return null;
    }

    private String extractDomain(String recipient) {
        int atIdx = recipient.lastIndexOf('@');
        if (atIdx != -1 && atIdx < recipient.length() - 1) {
            return recipient.substring(atIdx + 1).trim();
        }
        return recipient.trim();
    }

    private Map<String, Object> createSafeOutput(Verdict verdict, String tool, String message, String redactedText) {
        Map<String, Object> output = new HashMap<>();
        output.put("tool", tool);
        output.put("status", verdict == Verdict.ALLOW ? "MOCK_EXECUTED" : (verdict == Verdict.HOLD ? "PENDING_APPROVAL" : "WITHHELD"));
        output.put("message", message);
        output.put("redactedText", redactedText);
        output.put("executedAt", Instant.now().toString());
        return output;
    }
}
