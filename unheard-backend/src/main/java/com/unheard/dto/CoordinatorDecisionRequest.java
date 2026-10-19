package com.unheard.dto;

import jakarta.validation.constraints.NotBlank;

public class CoordinatorDecisionRequest {

    @NotBlank(message = "Decision is required (APPROVED, REJECTED, or OVERRIDDEN)")
    private String decision;

    private String reason;

    private Long assignedResourceId;

    private Integer overriddenRank;

    private String decidedBy;

    public CoordinatorDecisionRequest() {}

    public CoordinatorDecisionRequest(String decision, String reason, Long assignedResourceId,
                                      Integer overriddenRank, String decidedBy) {
        this.decision = decision;
        this.reason = reason;
        this.assignedResourceId = assignedResourceId;
        this.overriddenRank = overriddenRank;
        this.decidedBy = decidedBy;
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getAssignedResourceId() { return assignedResourceId; }
    public void setAssignedResourceId(Long assignedResourceId) { this.assignedResourceId = assignedResourceId; }
    public Integer getOverriddenRank() { return overriddenRank; }
    public void setOverriddenRank(Integer overriddenRank) { this.overriddenRank = overriddenRank; }
    public String getDecidedBy() { return decidedBy; }
    public void setDecidedBy(String decidedBy) { this.decidedBy = decidedBy; }
}
