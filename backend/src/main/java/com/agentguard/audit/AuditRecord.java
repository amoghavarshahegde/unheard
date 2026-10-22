package com.agentguard.audit;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_records", indexes = {
        @Index(name = "idx_audit_seq", columnList = "sequenceNumber")
})
public class AuditRecord {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @Column(nullable = false)
    private Long sequenceNumber;

    @Column(length = 64, nullable = false)
    private String prevHash;

    @Column(length = 64, nullable = false)
    private String hash;

    @Column(length = 64, nullable = false)
    private String agent;

    @Column(length = 64, nullable = false)
    private String tool;

    @Column(length = 16, nullable = false)
    private String verdict;

    @Column(nullable = false)
    private int score;

    @Column(length = 35, nullable = false)
    private String timestamp;

    @Column(columnDefinition = "TEXT")
    private String redactedPayload;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    public AuditRecord() {
    }

    public AuditRecord(String id, Long sequenceNumber, String prevHash, String hash,
                       String agent, String tool, String verdict, int score,
                       String timestamp, String redactedPayload, String reasons) {
        this.id = id;
        this.sequenceNumber = sequenceNumber;
        this.prevHash = prevHash;
        this.hash = hash;
        this.agent = agent;
        this.tool = tool;
        this.verdict = verdict;
        this.score = score;
        this.timestamp = timestamp;
        this.redactedPayload = redactedPayload;
        this.reasons = reasons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRedactedPayload() {
        return redactedPayload;
    }

    public void setRedactedPayload(String redactedPayload) {
        this.redactedPayload = redactedPayload;
    }

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }
}
