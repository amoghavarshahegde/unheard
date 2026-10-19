package com.unheard.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "villages", indexes = {
        @Index(name = "idx_village_hazard_type", columnList = "hazardType"),
        @Index(name = "idx_village_simulated", columnList = "simulated")
})
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer population;

    @Column(nullable = false)
    private Integer vulnerablePopulation;

    @Column(nullable = false)
    private String hazardType;

    @Column(nullable = false)
    private Double hazardRiskScore;

    @Column(nullable = false)
    private Integer expectedReportVolume;

    @Column(nullable = false)
    private Integer actualReportCount;

    @Column(nullable = false)
    private String connectivityQuality;

    private LocalDateTime lastFieldVerification;

    @Column(length = 500)
    private String nearbyResources;

    private String district;

    @Column(nullable = false)
    private Boolean simulated;

    public Village() {}

    public Village(Long id, String name, Double latitude, Double longitude, Integer population,
                   Integer vulnerablePopulation, String hazardType, Double hazardRiskScore,
                   Integer expectedReportVolume, Integer actualReportCount, String connectivityQuality,
                   LocalDateTime lastFieldVerification, String nearbyResources, String district, Boolean simulated) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.vulnerablePopulation = vulnerablePopulation;
        this.hazardType = hazardType;
        this.hazardRiskScore = hazardRiskScore;
        this.expectedReportVolume = expectedReportVolume;
        this.actualReportCount = actualReportCount;
        this.connectivityQuality = connectivityQuality;
        this.lastFieldVerification = lastFieldVerification;
        this.nearbyResources = nearbyResources;
        this.district = district;
        this.simulated = simulated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer population = 0;
        private Integer vulnerablePopulation = 0;
        private String hazardType = "FLOOD";
        private Double hazardRiskScore = 0.0;
        private Integer expectedReportVolume = 0;
        private Integer actualReportCount = 0;
        private String connectivityQuality = "MEDIUM";
        private LocalDateTime lastFieldVerification;
        private String nearbyResources;
        private String district;
        private Boolean simulated = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder population(Integer population) { this.population = population; return this; }
        public Builder vulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; return this; }
        public Builder hazardType(String hazardType) { this.hazardType = hazardType; return this; }
        public Builder hazardRiskScore(Double hazardRiskScore) { this.hazardRiskScore = hazardRiskScore; return this; }
        public Builder expectedReportVolume(Integer expectedReportVolume) { this.expectedReportVolume = expectedReportVolume; return this; }
        public Builder actualReportCount(Integer actualReportCount) { this.actualReportCount = actualReportCount; return this; }
        public Builder connectivityQuality(String connectivityQuality) { this.connectivityQuality = connectivityQuality; return this; }
        public Builder lastFieldVerification(LocalDateTime lastFieldVerification) { this.lastFieldVerification = lastFieldVerification; return this; }
        public Builder nearbyResources(String nearbyResources) { this.nearbyResources = nearbyResources; return this; }
        public Builder district(String district) { this.district = district; return this; }
        public Builder simulated(Boolean simulated) { this.simulated = simulated; return this; }

        public Village build() {
            return new Village(id, name, latitude, longitude, population, vulnerablePopulation, hazardType,
                    hazardRiskScore, expectedReportVolume, actualReportCount, connectivityQuality,
                    lastFieldVerification, nearbyResources, district, simulated);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public Integer getVulnerablePopulation() { return vulnerablePopulation; }
    public void setVulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; }
    public String getHazardType() { return hazardType; }
    public void setHazardType(String hazardType) { this.hazardType = hazardType; }
    public Double getHazardRiskScore() { return hazardRiskScore; }
    public void setHazardRiskScore(Double hazardRiskScore) { this.hazardRiskScore = hazardRiskScore; }
    public Integer getExpectedReportVolume() { return expectedReportVolume; }
    public void setExpectedReportVolume(Integer expectedReportVolume) { this.expectedReportVolume = expectedReportVolume; }
    public Integer getActualReportCount() { return actualReportCount; }
    public void setActualReportCount(Integer actualReportCount) { this.actualReportCount = actualReportCount; }
    public String getConnectivityQuality() { return connectivityQuality; }
    public void setConnectivityQuality(String connectivityQuality) { this.connectivityQuality = connectivityQuality; }
    public LocalDateTime getLastFieldVerification() { return lastFieldVerification; }
    public void setLastFieldVerification(LocalDateTime lastFieldVerification) { this.lastFieldVerification = lastFieldVerification; }
    public String getNearbyResources() { return nearbyResources; }
    public void setNearbyResources(String nearbyResources) { this.nearbyResources = nearbyResources; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public Boolean getSimulated() { return simulated; }
    public void setSimulated(Boolean simulated) { this.simulated = simulated; }
}
