package com.unheard.dto;

import java.time.LocalDateTime;

public class VillageDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer population;
    private Integer vulnerablePopulation;
    private String hazardType;
    private Double hazardRiskScore;
    private Integer expectedReportVolume;
    private Integer actualReportCount;
    private String connectivityQuality;
    private LocalDateTime lastFieldVerification;
    private String nearbyResources;
    private String district;
    private Boolean simulated;
    private Double verificationScore;
    private Double responseScore;
    private String riskLevel;
    private String freshnessIndicator;
    private String dataDisclaimer;

    public VillageDto() {}

    public VillageDto(Long id, String name, Double latitude, Double longitude, Integer population,
                      Integer vulnerablePopulation, String hazardType, Double hazardRiskScore,
                      Integer expectedReportVolume, Integer actualReportCount, String connectivityQuality,
                      LocalDateTime lastFieldVerification, String nearbyResources, String district,
                      Boolean simulated, Double verificationScore, Double responseScore, String riskLevel,
                      String freshnessIndicator, String dataDisclaimer) {
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
        this.verificationScore = verificationScore;
        this.responseScore = responseScore;
        this.riskLevel = riskLevel;
        this.freshnessIndicator = freshnessIndicator;
        this.dataDisclaimer = dataDisclaimer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer population;
        private Integer vulnerablePopulation;
        private String hazardType;
        private Double hazardRiskScore;
        private Integer expectedReportVolume;
        private Integer actualReportCount;
        private String connectivityQuality;
        private LocalDateTime lastFieldVerification;
        private String nearbyResources;
        private String district;
        private Boolean simulated;
        private Double verificationScore;
        private Double responseScore;
        private String riskLevel;
        private String freshnessIndicator;
        private String dataDisclaimer;

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
        public Builder verificationScore(Double verificationScore) { this.verificationScore = verificationScore; return this; }
        public Builder responseScore(Double responseScore) { this.responseScore = responseScore; return this; }
        public Builder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public Builder freshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; return this; }
        public Builder dataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; return this; }

        public VillageDto build() {
            return new VillageDto(id, name, latitude, longitude, population, vulnerablePopulation, hazardType,
                    hazardRiskScore, expectedReportVolume, actualReportCount, connectivityQuality,
                    lastFieldVerification, nearbyResources, district, simulated, verificationScore,
                    responseScore, riskLevel, freshnessIndicator, dataDisclaimer);
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
    public Double getVerificationScore() { return verificationScore; }
    public void setVerificationScore(Double verificationScore) { this.verificationScore = verificationScore; }
    public Double getResponseScore() { return responseScore; }
    public void setResponseScore(Double responseScore) { this.responseScore = responseScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getFreshnessIndicator() { return freshnessIndicator; }
    public void setFreshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; }
    public String getDataDisclaimer() { return dataDisclaimer; }
    public void setDataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; }
}
