package com.unheard.dto;

import java.time.LocalDateTime;

public class IncidentReportDto {
    private Long id;
    private Long villageId;
    private String villageName;
    private String incidentType;
    private Integer severity;
    private String description;
    private Integer peopleAffected;
    private LocalDateTime reportTimestamp;
    private String sourceType;
    private String verificationStatus;
    private Boolean flaggedDuplicate;
    private Long originalReportId;
    private String dataDisclaimer;

    public IncidentReportDto() {}

    public IncidentReportDto(Long id, Long villageId, String villageName, String incidentType,
                             Integer severity, String description, Integer peopleAffected,
                             LocalDateTime reportTimestamp, String sourceType, String verificationStatus,
                             Boolean flaggedDuplicate, Long originalReportId, String dataDisclaimer) {
        this.id = id;
        this.villageId = villageId;
        this.villageName = villageName;
        this.incidentType = incidentType;
        this.severity = severity;
        this.description = description;
        this.peopleAffected = peopleAffected;
        this.reportTimestamp = reportTimestamp;
        this.sourceType = sourceType;
        this.verificationStatus = verificationStatus;
        this.flaggedDuplicate = flaggedDuplicate;
        this.originalReportId = originalReportId;
        this.dataDisclaimer = dataDisclaimer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long villageId;
        private String villageName;
        private String incidentType;
        private Integer severity;
        private String description;
        private Integer peopleAffected;
        private LocalDateTime reportTimestamp;
        private String sourceType;
        private String verificationStatus;
        private Boolean flaggedDuplicate;
        private Long originalReportId;
        private String dataDisclaimer;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder villageId(Long villageId) { this.villageId = villageId; return this; }
        public Builder villageName(String villageName) { this.villageName = villageName; return this; }
        public Builder incidentType(String incidentType) { this.incidentType = incidentType; return this; }
        public Builder severity(Integer severity) { this.severity = severity; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder peopleAffected(Integer peopleAffected) { this.peopleAffected = peopleAffected; return this; }
        public Builder reportTimestamp(LocalDateTime reportTimestamp) { this.reportTimestamp = reportTimestamp; return this; }
        public Builder sourceType(String sourceType) { this.sourceType = sourceType; return this; }
        public Builder verificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public Builder flaggedDuplicate(Boolean flaggedDuplicate) { this.flaggedDuplicate = flaggedDuplicate; return this; }
        public Builder originalReportId(Long originalReportId) { this.originalReportId = originalReportId; return this; }
        public Builder dataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; return this; }

        public IncidentReportDto build() {
            return new IncidentReportDto(id, villageId, villageName, incidentType, severity, description,
                    peopleAffected, reportTimestamp, sourceType, verificationStatus, flaggedDuplicate,
                    originalReportId, dataDisclaimer);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVillageId() { return villageId; }
    public void setVillageId(Long villageId) { this.villageId = villageId; }
    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(Integer peopleAffected) { this.peopleAffected = peopleAffected; }
    public LocalDateTime getReportTimestamp() { return reportTimestamp; }
    public void setReportTimestamp(LocalDateTime reportTimestamp) { this.reportTimestamp = reportTimestamp; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public Boolean getFlaggedDuplicate() { return flaggedDuplicate; }
    public void setFlaggedDuplicate(Boolean flaggedDuplicate) { this.flaggedDuplicate = flaggedDuplicate; }
    public Long getOriginalReportId() { return originalReportId; }
    public void setOriginalReportId(Long originalReportId) { this.originalReportId = originalReportId; }
    public String getDataDisclaimer() { return dataDisclaimer; }
    public void setDataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; }
}
