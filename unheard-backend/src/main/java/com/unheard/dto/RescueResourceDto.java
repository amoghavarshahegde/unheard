package com.unheard.dto;

public class RescueResourceDto {
    private Long id;
    private String resourceType;
    private String name;
    private Double latitude;
    private Double longitude;
    private String availabilityStatus;
    private Integer capacity;
    private String currentAssignment;
    private String notes;
    private Boolean simulated;
    private Double distanceKmEstimate;
    private String distanceDisclaimer;

    public RescueResourceDto() {}

    public RescueResourceDto(Long id, String resourceType, String name, Double latitude, Double longitude,
                             String availabilityStatus, Integer capacity, String currentAssignment,
                             String notes, Boolean simulated, Double distanceKmEstimate, String distanceDisclaimer) {
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
        this.distanceKmEstimate = distanceKmEstimate;
        this.distanceDisclaimer = distanceDisclaimer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String resourceType;
        private String name;
        private Double latitude;
        private Double longitude;
        private String availabilityStatus;
        private Integer capacity;
        private String currentAssignment;
        private String notes;
        private Boolean simulated;
        private Double distanceKmEstimate;
        private String distanceDisclaimer;

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
        public Builder distanceKmEstimate(Double distanceKmEstimate) { this.distanceKmEstimate = distanceKmEstimate; return this; }
        public Builder distanceDisclaimer(String distanceDisclaimer) { this.distanceDisclaimer = distanceDisclaimer; return this; }

        public RescueResourceDto build() {
            return new RescueResourceDto(id, resourceType, name, latitude, longitude, availabilityStatus,
                    capacity, currentAssignment, notes, simulated, distanceKmEstimate, distanceDisclaimer);
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
    public Double getDistanceKmEstimate() { return distanceKmEstimate; }
    public void setDistanceKmEstimate(Double distanceKmEstimate) { this.distanceKmEstimate = distanceKmEstimate; }
    public String getDistanceDisclaimer() { return distanceDisclaimer; }
    public void setDistanceDisclaimer(String distanceDisclaimer) { this.distanceDisclaimer = distanceDisclaimer; }
}
