package com.unheard.dto;

import java.util.List;

public class BaselineComparisonDto {
    private List<VillageRankingEntry> baselineRanking;
    private List<VillageRankingEntry> unheardRanking;
    private ComparisonMetrics metrics;
    private String disclaimer;

    public BaselineComparisonDto() {}

    public BaselineComparisonDto(List<VillageRankingEntry> baselineRanking,
                                 List<VillageRankingEntry> unheardRanking,
                                 ComparisonMetrics metrics, String disclaimer) {
        this.baselineRanking = baselineRanking;
        this.unheardRanking = unheardRanking;
        this.metrics = metrics;
        this.disclaimer = disclaimer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<VillageRankingEntry> baselineRanking;
        private List<VillageRankingEntry> unheardRanking;
        private ComparisonMetrics metrics;
        private String disclaimer;

        public Builder baselineRanking(List<VillageRankingEntry> baselineRanking) { this.baselineRanking = baselineRanking; return this; }
        public Builder unheardRanking(List<VillageRankingEntry> unheardRanking) { this.unheardRanking = unheardRanking; return this; }
        public Builder metrics(ComparisonMetrics metrics) { this.metrics = metrics; return this; }
        public Builder disclaimer(String disclaimer) { this.disclaimer = disclaimer; return this; }

        public BaselineComparisonDto build() {
            return new BaselineComparisonDto(baselineRanking, unheardRanking, metrics, disclaimer);
        }
    }

    public List<VillageRankingEntry> getBaselineRanking() { return baselineRanking; }
    public void setBaselineRanking(List<VillageRankingEntry> baselineRanking) { this.baselineRanking = baselineRanking; }
    public List<VillageRankingEntry> getUnheardRanking() { return unheardRanking; }
    public void setUnheardRanking(List<VillageRankingEntry> unheardRanking) { this.unheardRanking = unheardRanking; }
    public ComparisonMetrics getMetrics() { return metrics; }
    public void setMetrics(ComparisonMetrics metrics) { this.metrics = metrics; }
    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public static class VillageRankingEntry {
        private Integer rank;
        private Long villageId;
        private String villageName;
        private Integer actualReportCount;
        private Double unheardScore;
        private String hazardType;
        private Double hazardRiskScore;
        private Integer population;
        private Integer vulnerablePopulation;
        private String connectivityQuality;
        private Boolean isSilentHighRisk;
        private String explanation;

        public VillageRankingEntry() {}

        public VillageRankingEntry(Integer rank, Long villageId, String villageName, Integer actualReportCount,
                                   Double unheardScore, String hazardType, Double hazardRiskScore,
                                   Integer population, Integer vulnerablePopulation, String connectivityQuality,
                                   Boolean isSilentHighRisk, String explanation) {
            this.rank = rank;
            this.villageId = villageId;
            this.villageName = villageName;
            this.actualReportCount = actualReportCount;
            this.unheardScore = unheardScore;
            this.hazardType = hazardType;
            this.hazardRiskScore = hazardRiskScore;
            this.population = population;
            this.vulnerablePopulation = vulnerablePopulation;
            this.connectivityQuality = connectivityQuality;
            this.isSilentHighRisk = isSilentHighRisk;
            this.explanation = explanation;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer rank;
            private Long villageId;
            private String villageName;
            private Integer actualReportCount;
            private Double unheardScore;
            private String hazardType;
            private Double hazardRiskScore;
            private Integer population;
            private Integer vulnerablePopulation;
            private String connectivityQuality;
            private Boolean isSilentHighRisk;
            private String explanation;

            public Builder rank(Integer rank) { this.rank = rank; return this; }
            public Builder villageId(Long villageId) { this.villageId = villageId; return this; }
            public Builder villageName(String villageName) { this.villageName = villageName; return this; }
            public Builder actualReportCount(Integer actualReportCount) { this.actualReportCount = actualReportCount; return this; }
            public Builder unheardScore(Double unheardScore) { this.unheardScore = unheardScore; return this; }
            public Builder hazardType(String hazardType) { this.hazardType = hazardType; return this; }
            public Builder hazardRiskScore(Double hazardRiskScore) { this.hazardRiskScore = hazardRiskScore; return this; }
            public Builder population(Integer population) { this.population = population; return this; }
            public Builder vulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; return this; }
            public Builder connectivityQuality(String connectivityQuality) { this.connectivityQuality = connectivityQuality; return this; }
            public Builder isSilentHighRisk(Boolean isSilentHighRisk) { this.isSilentHighRisk = isSilentHighRisk; return this; }
            public Builder explanation(String explanation) { this.explanation = explanation; return this; }

            public VillageRankingEntry build() {
                return new VillageRankingEntry(rank, villageId, villageName, actualReportCount, unheardScore,
                        hazardType, hazardRiskScore, population, vulnerablePopulation, connectivityQuality,
                        isSilentHighRisk, explanation);
            }
        }

        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
        public Long getVillageId() { return villageId; }
        public void setVillageId(Long villageId) { this.villageId = villageId; }
        public String getVillageName() { return villageName; }
        public void setVillageName(String villageName) { this.villageName = villageName; }
        public Integer getActualReportCount() { return actualReportCount; }
        public void setActualReportCount(Integer actualReportCount) { this.actualReportCount = actualReportCount; }
        public Double getUnheardScore() { return unheardScore; }
        public void setUnheardScore(Double unheardScore) { this.unheardScore = unheardScore; }
        public String getHazardType() { return hazardType; }
        public void setHazardType(String hazardType) { this.hazardType = hazardType; }
        public Double getHazardRiskScore() { return hazardRiskScore; }
        public void setHazardRiskScore(Double hazardRiskScore) { this.hazardRiskScore = hazardRiskScore; }
        public Integer getPopulation() { return population; }
        public void setPopulation(Integer population) { this.population = population; }
        public Integer getVulnerablePopulation() { return vulnerablePopulation; }
        public void setVulnerablePopulation(Integer vulnerablePopulation) { this.vulnerablePopulation = vulnerablePopulation; }
        public String getConnectivityQuality() { return connectivityQuality; }
        public void setConnectivityQuality(String connectivityQuality) { this.connectivityQuality = connectivityQuality; }
        public Boolean getIsSilentHighRisk() { return isSilentHighRisk; }
        public void setIsSilentHighRisk(Boolean isSilentHighRisk) { this.isSilentHighRisk = isSilentHighRisk; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class ComparisonMetrics {
        private Integer totalVillages;
        private Integer highRiskUnderreportedCount;
        private Integer detectedByUnheard;
        private Integer detectedByBaseline;
        private Integer missedByBaseline;
        private Integer missedByUnheard;
        private Double unheardDetectionRate;
        private Double baselineDetectionRate;
        private String methodologyNote;

        public ComparisonMetrics() {}

        public ComparisonMetrics(Integer totalVillages, Integer highRiskUnderreportedCount,
                                 Integer detectedByUnheard, Integer detectedByBaseline,
                                 Integer missedByBaseline, Integer missedByUnheard,
                                 Double unheardDetectionRate, Double baselineDetectionRate,
                                 String methodologyNote) {
            this.totalVillages = totalVillages;
            this.highRiskUnderreportedCount = highRiskUnderreportedCount;
            this.detectedByUnheard = detectedByUnheard;
            this.detectedByBaseline = detectedByBaseline;
            this.missedByBaseline = missedByBaseline;
            this.missedByUnheard = missedByUnheard;
            this.unheardDetectionRate = unheardDetectionRate;
            this.baselineDetectionRate = baselineDetectionRate;
            this.methodologyNote = methodologyNote;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Integer totalVillages;
            private Integer highRiskUnderreportedCount;
            private Integer detectedByUnheard;
            private Integer detectedByBaseline;
            private Integer missedByBaseline;
            private Integer missedByUnheard;
            private Double unheardDetectionRate;
            private Double baselineDetectionRate;
            private String methodologyNote;

            public Builder totalVillages(Integer totalVillages) { this.totalVillages = totalVillages; return this; }
            public Builder highRiskUnderreportedCount(Integer highRiskUnderreportedCount) { this.highRiskUnderreportedCount = highRiskUnderreportedCount; return this; }
            public Builder detectedByUnheard(Integer detectedByUnheard) { this.detectedByUnheard = detectedByUnheard; return this; }
            public Builder detectedByBaseline(Integer detectedByBaseline) { this.detectedByBaseline = detectedByBaseline; return this; }
            public Builder missedByBaseline(Integer missedByBaseline) { this.missedByBaseline = missedByBaseline; return this; }
            public Builder missedByUnheard(Integer missedByUnheard) { this.missedByUnheard = missedByUnheard; return this; }
            public Builder unheardDetectionRate(Double unheardDetectionRate) { this.unheardDetectionRate = unheardDetectionRate; return this; }
            public Builder baselineDetectionRate(Double baselineDetectionRate) { this.baselineDetectionRate = baselineDetectionRate; return this; }
            public Builder methodologyNote(String methodologyNote) { this.methodologyNote = methodologyNote; return this; }

            public ComparisonMetrics build() {
                return new ComparisonMetrics(totalVillages, highRiskUnderreportedCount, detectedByUnheard,
                        detectedByBaseline, missedByBaseline, missedByUnheard, unheardDetectionRate,
                        baselineDetectionRate, methodologyNote);
            }
        }

        public Integer getTotalVillages() { return totalVillages; }
        public void setTotalVillages(Integer totalVillages) { this.totalVillages = totalVillages; }
        public Integer getHighRiskUnderreportedCount() { return highRiskUnderreportedCount; }
        public void setHighRiskUnderreportedCount(Integer highRiskUnderreportedCount) { this.highRiskUnderreportedCount = highRiskUnderreportedCount; }
        public Integer getDetectedByUnheard() { return detectedByUnheard; }
        public void setDetectedByUnheard(Integer detectedByUnheard) { this.detectedByUnheard = detectedByUnheard; }
        public Integer getDetectedByBaseline() { return detectedByBaseline; }
        public void setDetectedByBaseline(Integer detectedByBaseline) { this.detectedByBaseline = detectedByBaseline; }
        public Integer getMissedByBaseline() { return missedByBaseline; }
        public void setMissedByBaseline(Integer missedByBaseline) { this.missedByBaseline = missedByBaseline; }
        public Integer getMissedByUnheard() { return missedByUnheard; }
        public void setMissedByUnheard(Integer missedByUnheard) { this.missedByUnheard = missedByUnheard; }
        public Double getUnheardDetectionRate() { return unheardDetectionRate; }
        public void setUnheardDetectionRate(Double unheardDetectionRate) { this.unheardDetectionRate = unheardDetectionRate; }
        public Double getBaselineDetectionRate() { return baselineDetectionRate; }
        public void setBaselineDetectionRate(Double baselineDetectionRate) { this.baselineDetectionRate = baselineDetectionRate; }
        public String getMethodologyNote() { return methodologyNote; }
        public void setMethodologyNote(String methodologyNote) { this.methodologyNote = methodologyNote; }
    }
}
