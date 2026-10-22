package com.agentguard.approvals;

import jakarta.persistence.*;

@Entity
@Table(name = "approval_records", indexes = {
        @Index(name = "idx_approval_status", columnList = "status")
})
public class ApprovalRecord {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @Column(length = 64, nullable = false)
    private String actionId;

    @Column(length = 64, nullable = false)
    private String agent;

    @Column(length = 64, nullable = false)
    private String tool;

    @Column(length = 255)
    private String recipient;

    @Column(nullable = false)
    private int score;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    @Column(columnDefinition = "TEXT")
    private String redactedPayload;

    @Column(length = 16, nullable = false)
    private String status; // PENDING, APPROVED, DENIED

    @Column(length = 64)
    private String reviewer;

    @Column(length = 35)
    private String reviewedAt;

    @Column(length = 35, nullable = false)
    private String createdAt;

    public ApprovalRecord() {
    }

    public ApprovalRecord(String id, String actionId, String agent, String tool, String recipient,
                          int score, String reasons, String redactedPayload, String status,
                          String reviewer, String reviewedAt, String createdAt) {
        this.id = id;
        this.actionId = actionId;
        this.agent = agent;
        this.tool = tool;
        this.recipient = recipient;
        this.score = score;
        this.reasons = reasons;
        this.redactedPayload = redactedPayload;
        this.status = status;
        this.reviewer = reviewer;
        this.reviewedAt = reviewedAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getRedactedPayload() {
        return redactedPayload;
    }

    public void setRedactedPayload(String redactedPayload) {
        this.redactedPayload = redactedPayload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(String reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
