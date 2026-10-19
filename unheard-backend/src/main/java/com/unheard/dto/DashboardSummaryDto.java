package com.unheard.dto;

import java.time.LocalDateTime;

public class DashboardSummaryDto {
    private Integer totalVillages;
    private Integer highRiskVillages;
    private Integer silentZoneVillages;
    private Integer activeIncidents;
    private Integer availableRescueTeams;
    private Integer availableBoats;
    private Integer totalResources;
    private LocalDateTime lastAnalysisRun;
    private Long lastAnalysisRunId;
    private Integer totalRecommendations;
    private Integer pendingDecisions;
    private String dataDisclaimer;

    public DashboardSummaryDto() {}

    public DashboardSummaryDto(Integer totalVillages, Integer highRiskVillages, Integer silentZoneVillages,
                               Integer activeIncidents, Integer availableRescueTeams, Integer availableBoats,
                               Integer totalResources, LocalDateTime lastAnalysisRun, Long lastAnalysisRunId,
                               Integer totalRecommendations, Integer pendingDecisions, String dataDisclaimer) {
        this.totalVillages = totalVillages;
        this.highRiskVillages = highRiskVillages;
        this.silentZoneVillages = silentZoneVillages;
        this.activeIncidents = activeIncidents;
        this.availableRescueTeams = availableRescueTeams;
        this.availableBoats = availableBoats;
        this.totalResources = totalResources;
        this.lastAnalysisRun = lastAnalysisRun;
        this.lastAnalysisRunId = lastAnalysisRunId;
        this.totalRecommendations = totalRecommendations;
        this.pendingDecisions = pendingDecisions;
        this.dataDisclaimer = dataDisclaimer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer totalVillages;
        private Integer highRiskVillages;
        private Integer silentZoneVillages;
        private Integer activeIncidents;
        private Integer availableRescueTeams;
        private Integer availableBoats;
        private Integer totalResources;
        private LocalDateTime lastAnalysisRun;
        private Long lastAnalysisRunId;
        private Integer totalRecommendations;
        private Integer pendingDecisions;
        private String dataDisclaimer;

        public Builder totalVillages(Integer totalVillages) { this.totalVillages = totalVillages; return this; }
        public Builder highRiskVillages(Integer highRiskVillages) { this.highRiskVillages = highRiskVillages; return this; }
        public Builder silentZoneVillages(Integer silentZoneVillages) { this.silentZoneVillages = silentZoneVillages; return this; }
        public Builder activeIncidents(Integer activeIncidents) { this.activeIncidents = activeIncidents; return this; }
        public Builder availableRescueTeams(Integer availableRescueTeams) { this.availableRescueTeams = availableRescueTeams; return this; }
        public Builder availableBoats(Integer availableBoats) { this.availableBoats = availableBoats; return this; }
        public Builder totalResources(Integer totalResources) { this.totalResources = totalResources; return this; }
        public Builder lastAnalysisRun(LocalDateTime lastAnalysisRun) { this.lastAnalysisRun = lastAnalysisRun; return this; }
        public Builder lastAnalysisRunId(Long lastAnalysisRunId) { this.lastAnalysisRunId = lastAnalysisRunId; return this; }
        public Builder totalRecommendations(Integer totalRecommendations) { this.totalRecommendations = totalRecommendations; return this; }
        public Builder pendingDecisions(Integer pendingDecisions) { this.pendingDecisions = pendingDecisions; return this; }
        public Builder dataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; return this; }

        public DashboardSummaryDto build() {
            return new DashboardSummaryDto(totalVillages, highRiskVillages, silentZoneVillages,
                    activeIncidents, availableRescueTeams, availableBoats, totalResources,
                    lastAnalysisRun, lastAnalysisRunId, totalRecommendations, pendingDecisions, dataDisclaimer);
        }
    }

    public Integer getTotalVillages() { return totalVillages; }
    public void setTotalVillages(Integer totalVillages) { this.totalVillages = totalVillages; }
    public Integer getHighRiskVillages() { return highRiskVillages; }
    public void setHighRiskVillages(Integer highRiskVillages) { this.highRiskVillages = highRiskVillages; }
    public Integer getSilentZoneVillages() { return silentZoneVillages; }
    public void setSilentZoneVillages(Integer silentZoneVillages) { this.silentZoneVillages = silentZoneVillages; }
    public Integer getActiveIncidents() { return activeIncidents; }
    public void setActiveIncidents(Integer activeIncidents) { this.activeIncidents = activeIncidents; }
    public Integer getAvailableRescueTeams() { return availableRescueTeams; }
    public void setAvailableRescueTeams(Integer availableRescueTeams) { this.availableRescueTeams = availableRescueTeams; }
    public Integer getAvailableBoats() { return availableBoats; }
    public void setAvailableBoats(Integer availableBoats) { this.availableBoats = availableBoats; }
    public Integer getTotalResources() { return totalResources; }
    public void setTotalResources(Integer totalResources) { this.totalResources = totalResources; }
    public LocalDateTime getLastAnalysisRun() { return lastAnalysisRun; }
    public void setLastAnalysisRun(LocalDateTime lastAnalysisRun) { this.lastAnalysisRun = lastAnalysisRun; }
    public Long getLastAnalysisRunId() { return lastAnalysisRunId; }
    public void setLastAnalysisRunId(Long lastAnalysisRunId) { this.lastAnalysisRunId = lastAnalysisRunId; }
    public Integer getTotalRecommendations() { return totalRecommendations; }
    public void setTotalRecommendations(Integer totalRecommendations) { this.totalRecommendations = totalRecommendations; }
    public Integer getPendingDecisions() { return pendingDecisions; }
    public void setPendingDecisions(Integer pendingDecisions) { this.pendingDecisions = pendingDecisions; }
    public String getDataDisclaimer() { return dataDisclaimer; }
    public void setDataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; }
}
