package com.unheard.dto;

import jakarta.validation.constraints.*;

public class IncidentReportRequest {

    @NotNull(message = "Village ID is required")
    private Long villageId;

    @NotBlank(message = "Incident type is required")
    private String incidentType;

    @NotNull(message = "Severity is required")
    @Min(value = 1, message = "Severity must be at least 1")
    @Max(value = 5, message = "Severity must be at most 5")
    private Integer severity;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Min(value = 0, message = "People affected cannot be negative")
    private Integer peopleAffected;

    private String sourceType;

    public IncidentReportRequest() {}

    public IncidentReportRequest(Long villageId, String incidentType, Integer severity,
                                 String description, Integer peopleAffected, String sourceType) {
        this.villageId = villageId;
        this.incidentType = incidentType;
        this.severity = severity;
        this.description = description;
        this.peopleAffected = peopleAffected;
        this.sourceType = sourceType;
    }

    public Long getVillageId() { return villageId; }
    public void setVillageId(Long villageId) { this.villageId = villageId; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public Integer getSeverity() { return severity; }
    public void setSeverity(Integer severity) { this.severity = severity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(Integer peopleAffected) { this.peopleAffected = peopleAffected; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
