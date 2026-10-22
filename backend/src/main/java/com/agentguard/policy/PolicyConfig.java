package com.agentguard.policy;

import java.util.*;

public class PolicyConfig {
    private Map<String, List<String>> allowedTools = new HashMap<>();
    private List<String> promptInjectionPatterns = new ArrayList<>();
    private String internalDomain = "company.com";
    private int unpermittedToolScore = 80;
    private int promptInjectionScore = 45;
    private int externalRecipientScore = 35;
    private int sensitiveExternalScore = 30;
    private int sensitiveInternalScore = 5;
    private int maxAttachments = 5;
    private int excessiveAttachmentsScore = 30;
    private int deleteTier1MaxRows = 49;
    private int deleteTier1Score = 40;
    private int deleteTier2MaxRows = 999;
    private int deleteTier2Score = 60;
    private int deleteTier3Score = 80;
    private int allowThreshold = 40;
    private int blockThreshold = 75;

    public PolicyConfig() {
        // Defaults per specification
        allowedTools.put("DocBot", new ArrayList<>(Arrays.asList(
                "read_document",
                "summarize",
                "send_email",
                "delete_records"
        )));

        promptInjectionPatterns.addAll(Arrays.asList(
                "ignore previous instructions",
                "disregard your rules",
                "you are now in developer mode",
                "reveal the system prompt",
                "forward all",
                "email all .* files"
        ));
    }

    public Map<String, List<String>> getAllowedTools() {
        return allowedTools;
    }

    public void setAllowedTools(Map<String, List<String>> allowedTools) {
        this.allowedTools = allowedTools != null ? allowedTools : new HashMap<>();
    }

    public List<String> getPromptInjectionPatterns() {
        return promptInjectionPatterns;
    }

    public void setPromptInjectionPatterns(List<String> promptInjectionPatterns) {
        this.promptInjectionPatterns = promptInjectionPatterns != null ? promptInjectionPatterns : new ArrayList<>();
    }

    public String getInternalDomain() {
        return internalDomain;
    }

    public void setInternalDomain(String internalDomain) {
        this.internalDomain = internalDomain;
    }

    public int getUnpermittedToolScore() {
        return unpermittedToolScore;
    }

    public void setUnpermittedToolScore(int unpermittedToolScore) {
        this.unpermittedToolScore = unpermittedToolScore;
    }

    public int getPromptInjectionScore() {
        return promptInjectionScore;
    }

    public void setPromptInjectionScore(int promptInjectionScore) {
        this.promptInjectionScore = promptInjectionScore;
    }

    public int getExternalRecipientScore() {
        return externalRecipientScore;
    }

    public void setExternalRecipientScore(int externalRecipientScore) {
        this.externalRecipientScore = externalRecipientScore;
    }

    public int getSensitiveExternalScore() {
        return sensitiveExternalScore;
    }

    public void setSensitiveExternalScore(int sensitiveExternalScore) {
        this.sensitiveExternalScore = sensitiveExternalScore;
    }

    public int getSensitiveInternalScore() {
        return sensitiveInternalScore;
    }

    public void setSensitiveInternalScore(int sensitiveInternalScore) {
        this.sensitiveInternalScore = sensitiveInternalScore;
    }

    public int getMaxAttachments() {
        return maxAttachments;
    }

    public void setMaxAttachments(int maxAttachments) {
        this.maxAttachments = maxAttachments;
    }

    public int getExcessiveAttachmentsScore() {
        return excessiveAttachmentsScore;
    }

    public void setExcessiveAttachmentsScore(int excessiveAttachmentsScore) {
        this.excessiveAttachmentsScore = excessiveAttachmentsScore;
    }

    public int getDeleteTier1MaxRows() {
        return deleteTier1MaxRows;
    }

    public void setDeleteTier1MaxRows(int deleteTier1MaxRows) {
        this.deleteTier1MaxRows = deleteTier1MaxRows;
    }

    public int getDeleteTier1Score() {
        return deleteTier1Score;
    }

    public void setDeleteTier1Score(int deleteTier1Score) {
        this.deleteTier1Score = deleteTier1Score;
    }

    public int getDeleteTier2MaxRows() {
        return deleteTier2MaxRows;
    }

    public void setDeleteTier2MaxRows(int deleteTier2MaxRows) {
        this.deleteTier2MaxRows = deleteTier2MaxRows;
    }

    public int getDeleteTier2Score() {
        return deleteTier2Score;
    }

    public void setDeleteTier2Score(int deleteTier2Score) {
        this.deleteTier2Score = deleteTier2Score;
    }

    public int getDeleteTier3Score() {
        return deleteTier3Score;
    }

    public void setDeleteTier3Score(int deleteTier3Score) {
        this.deleteTier3Score = deleteTier3Score;
    }

    public int getAllowThreshold() {
        return allowThreshold;
    }

    public void setAllowThreshold(int allowThreshold) {
        this.allowThreshold = allowThreshold;
    }

    public int getBlockThreshold() {
        return blockThreshold;
    }

    public void setBlockThreshold(int blockThreshold) {
        this.blockThreshold = blockThreshold;
    }
}
