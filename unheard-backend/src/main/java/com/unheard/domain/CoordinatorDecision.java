package com.unheard.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coordinator_decisions", indexes = {
        @Index(name = "idx_decision_rec", columnList = "recommendation_id")
})
public class CoordinatorDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private Recommendation recommendation;

    @Column(nullable = false)
    private String decision;

    @Column(length = 2000)
    private String reason;

    private Long assignedResourceId;

    private Integer overriddenRank;

    @Column(nullable = false)
    private LocalDateTime decidedAt;

    private String decidedBy;

    private Double originalScore;
    private Integer originalRank;

    public CoordinatorDecision() {}

    public CoordinatorDecision(Long id, Recommendation recommendation, String decision, String reason,
                               Long assignedResourceId, Integer overriddenRank, LocalDateTime decidedAt,
                               String decidedBy, Double originalScore, Integer originalRank) {
        this.id = id;
        this.recommendation = recommendation;
        this.decision = decision;
        this.reason = reason;
        this.assignedResourceId = assignedResourceId;
        this.overriddenRank = overriddenRank;
        this.decidedAt = decidedAt;
        this.decidedBy = decidedBy;
        this.originalScore = originalScore;
        this.originalRank = originalRank;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Recommendation recommendation;
        private String decision = "APPROVED";
        private String reason;
        private Long assignedResourceId;
        private Integer overriddenRank;
        private LocalDateTime decidedAt = LocalDateTime.now();
        private String decidedBy = "COORDINATOR";
        private Double originalScore;
        private Integer originalRank;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder recommendation(Recommendation recommendation) { this.recommendation = recommendation; return this; }
        public Builder decision(String decision) { this.decision = decision; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder assignedResourceId(Long assignedResourceId) { this.assignedResourceId = assignedResourceId; return this; }
        public Builder overriddenRank(Integer overriddenRank) { this.overriddenRank = overriddenRank; return this; }
        public Builder decidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; return this; }
        public Builder decidedBy(String decidedBy) { this.decidedBy = decidedBy; return this; }
        public Builder originalScore(Double originalScore) { this.originalScore = originalScore; return this; }
        public Builder originalRank(Integer originalRank) { this.originalRank = originalRank; return this; }

        public CoordinatorDecision build() {
            return new CoordinatorDecision(id, recommendation, decision, reason, assignedResourceId,
                    overriddenRank, decidedAt, decidedBy, originalScore, originalRank);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Recommendation getRecommendation() { return recommendation; }
    public void setRecommendation(Recommendation recommendation) { this.recommendation = recommendation; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getAssignedResourceId() { return assignedResourceId; }
    public void setAssignedResourceId(Long assignedResourceId) { this.assignedResourceId = assignedResourceId; }
    public Integer getOverriddenRank() { return overriddenRank; }
    public void setOverriddenRank(Integer overriddenRank) { this.overriddenRank = overriddenRank; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public String getDecidedBy() { return decidedBy; }
    public void setDecidedBy(String decidedBy) { this.decidedBy = decidedBy; }
    public Double getOriginalScore() { return originalScore; }
    public void setOriginalScore(Double originalScore) { this.originalScore = originalScore; }
    public Integer getOriginalRank() { return originalRank; }
    public void setOriginalRank(Integer originalRank) { this.originalRank = originalRank; }
}
