package com.unheard.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_reports", indexes = {
        @Index(name = "idx_report_village", columnList = "village_id"),
        @Index(name = "idx_report_type_status", columnList = "incidentType, verificationStatus")
})
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @Column(nullable = false)
    private String incidentType;

    @Column(nullable = false)
    private Integer severity;

    @Column(length = 2000)
    private String description;

    private Integer peopleAffected;

    @Column(nullable = false)
    private LocalDateTime reportTimestamp;

    private String sourceType;

    @Column(nullable = false)
    private String verificationStatus;

    @Column(nullable = false)
    private Boolean flaggedDuplicate;

    private Long originalReportId;

    public IncidentReport() {}

    public IncidentReport(Long id, Village village, String incidentType, Integer severity,
                          String description, Integer peopleAffected, LocalDateTime reportTimestamp,
                          String sourceType, String verificationStatus, Boolean flaggedDuplicate, Long originalReportId) {
        this.id = id;
        this.village = village;
        this.incidentType = incidentType;
        this.severity = severity;
        this.description = description;
        this.peopleAffected = peopleAffected;
        this.reportTimestamp = reportTimestamp;
        this.sourceType = sourceType;
        this.verificationStatus = verificationStatus;
        this.flaggedDuplicate = flaggedDuplicate;
        this.originalReportId = originalReportId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Village village;
        private String incidentType;
        private Integer severity = 1;
        private String description;
        private Integer peopleAffected;
        private LocalDateTime reportTimestamp = LocalDateTime.now();
        private String sourceType = "COMMUNITY_MEMBER";
        private String verificationStatus = "PENDING";
        private Boolean flaggedDuplicate = false;
        private Long originalReportId;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder village(Village village) { this.village = village; return this; }
        public Builder incidentType(String incidentType) { this.incidentType = incidentType; return this; }
        public Builder severity(Integer severity) { this.severity = severity; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder peopleAffected(Integer peopleAffected) { this.peopleAffected = peopleAffected; return this; }
        public Builder reportTimestamp(LocalDateTime reportTimestamp) { this.reportTimestamp = reportTimestamp; return this; }
        public Builder sourceType(String sourceType) { this.sourceType = sourceType; return this; }
        public Builder verificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public Builder flaggedDuplicate(Boolean flaggedDuplicate) { this.flaggedDuplicate = flaggedDuplicate; return this; }
        public Builder originalReportId(Long originalReportId) { this.originalReportId = originalReportId; return this; }

        public IncidentReport build() {
            return new IncidentReport(id, village, incidentType, severity, description, peopleAffected,
                    reportTimestamp, sourceType, verificationStatus, flaggedDuplicate, originalReportId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Village getVillage() { return village; }
    public void setVillage(Village village) { this.village = village; }
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
}
