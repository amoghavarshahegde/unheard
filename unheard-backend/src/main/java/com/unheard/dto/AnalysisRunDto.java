package com.unheard.dto;

import java.time.LocalDateTime;

public class AnalysisRunDto {
    private Long id;
    private LocalDateTime runAt;
    private String triggerSource;
    private Integer villagesAnalyzed;
    private Integer highRiskCount;
    private Integer silentZoneCount;
    private Integer totalRecommendations;
    private String notes;

    public AnalysisRunDto() {}

    public AnalysisRunDto(Long id, LocalDateTime runAt, String triggerSource, Integer villagesAnalyzed,
                          Integer highRiskCount, Integer silentZoneCount, Integer totalRecommendations, String notes) {
        this.id = id;
        this.runAt = runAt;
        this.triggerSource = triggerSource;
        this.villagesAnalyzed = villagesAnalyzed;
        this.highRiskCount = highRiskCount;
        this.silentZoneCount = silentZoneCount;
        this.totalRecommendations = totalRecommendations;
        this.notes = notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDateTime runAt;
        private String triggerSource;
        private Integer villagesAnalyzed;
        private Integer highRiskCount;
        private Integer silentZoneCount;
        private Integer totalRecommendations;
        private String notes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder runAt(LocalDateTime runAt) { this.runAt = runAt; return this; }
        public Builder triggerSource(String triggerSource) { this.triggerSource = triggerSource; return this; }
        public Builder villagesAnalyzed(Integer villagesAnalyzed) { this.villagesAnalyzed = villagesAnalyzed; return this; }
        public Builder highRiskCount(Integer highRiskCount) { this.highRiskCount = highRiskCount; return this; }
        public Builder silentZoneCount(Integer silentZoneCount) { this.silentZoneCount = silentZoneCount; return this; }
        public Builder totalRecommendations(Integer totalRecommendations) { this.totalRecommendations = totalRecommendations; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public AnalysisRunDto build() {
            return new AnalysisRunDto(id, runAt, triggerSource, villagesAnalyzed, highRiskCount,
                    silentZoneCount, totalRecommendations, notes);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getRunAt() { return runAt; }
    public void setRunAt(LocalDateTime runAt) { this.runAt = runAt; }
    public String getTriggerSource() { return triggerSource; }
    public void setTriggerSource(String triggerSource) { this.triggerSource = triggerSource; }
    public Integer getVillagesAnalyzed() { return villagesAnalyzed; }
    public void setVillagesAnalyzed(Integer villagesAnalyzed) { this.villagesAnalyzed = villagesAnalyzed; }
    public Integer getHighRiskCount() { return highRiskCount; }
    public void setHighRiskCount(Integer highRiskCount) { this.highRiskCount = highRiskCount; }
    public Integer getSilentZoneCount() { return silentZoneCount; }
    public void setSilentZoneCount(Integer silentZoneCount) { this.silentZoneCount = silentZoneCount; }
    public Integer getTotalRecommendations() { return totalRecommendations; }
    public void setTotalRecommendations(Integer totalRecommendations) { this.totalRecommendations = totalRecommendations; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
