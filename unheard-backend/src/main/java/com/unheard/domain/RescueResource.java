package com.unheard.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "rescue_resources", indexes = {
        @Index(name = "idx_resource_type_status", columnList = "resourceType, availabilityStatus")
})
public class RescueResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resourceType;

    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String availabilityStatus;

    private Integer capacity;

    private String currentAssignment;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Boolean simulated;

    public RescueResource() {}

    public RescueResource(Long id, String resourceType, String name, Double latitude, Double longitude,
                          String availabilityStatus, Integer capacity, String currentAssignment,
                          String notes, Boolean simulated) {
        this.id = id;
        this.resourceType = resourceType;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availabilityStatus = availabilityStatus;
        this.capacity = capacity;
        this.currentAssignment = currentAssignment;
        this.notes = notes;
        this.simulated = simulated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String resourceType = "RESCUE_TEAM";
        private String name;
        private Double latitude;
        private Double longitude;
        private String availabilityStatus = "AVAILABLE";
        private Integer capacity = 10;
        private String currentAssignment;
        private String notes;
        private Boolean simulated = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder resourceType(String resourceType) { this.resourceType = resourceType; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder availabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; return this; }
        public Builder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public Builder currentAssignment(String currentAssignment) { this.currentAssignment = currentAssignment; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder simulated(Boolean simulated) { this.simulated = simulated; return this; }

        public RescueResource build() {
            return new RescueResource(id, resourceType, name, latitude, longitude,
                    availabilityStatus, capacity, currentAssignment, notes, simulated);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getCurrentAssignment() { return currentAssignment; }
    public void setCurrentAssignment(String currentAssignment) { this.currentAssignment = currentAssignment; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Boolean getSimulated() { return simulated; }
    public void setSimulated(Boolean simulated) { this.simulated = simulated; }
}
