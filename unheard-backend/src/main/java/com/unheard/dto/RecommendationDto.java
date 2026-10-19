package com.unheard.dto;

public class RecommendationDto {
    private Long id;
    private Long analysisRunId;
    private Long villageId;
    private String villageName;
    private String recommendationType;
    private Double score;
    private Integer rank;
    private String keyFactors;
    private String explanation;
    private String freshnessIndicator;
    private String confidenceLevel;
    private Boolean basedOnSimulatedData;
    private String scoreBreakdown;
    private String dataDisclaimer;
    private CoordinatorDecisionDto decision;

    public RecommendationDto() {}

    public RecommendationDto(Long id, Long analysisRunId, Long villageId, String villageName,
                             String recommendationType, Double score, Integer rank, String keyFactors,
                             String explanation, String freshnessIndicator, String confidenceLevel,
                             Boolean basedOnSimulatedData, String scoreBreakdown, String dataDisclaimer,
                             CoordinatorDecisionDto decision) {
        this.id = id;
        this.analysisRunId = analysisRunId;
        this.villageId = villageId;
        this.villageName = villageName;
        this.recommendationType = recommendationType;
        this.score = score;
        this.rank = rank;
        this.keyFactors = keyFactors;
        this.explanation = explanation;
        this.freshnessIndicator = freshnessIndicator;
        this.confidenceLevel = confidenceLevel;
        this.basedOnSimulatedData = basedOnSimulatedData;
        this.scoreBreakdown = scoreBreakdown;
        this.dataDisclaimer = dataDisclaimer;
        this.decision = decision;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long analysisRunId;
        private Long villageId;
        private String villageName;
        private String recommendationType;
        private Double score;
        private Integer rank;
        private String keyFactors;
        private String explanation;
        private String freshnessIndicator;
        private String confidenceLevel;
        private Boolean basedOnSimulatedData;
        private String scoreBreakdown;
        private String dataDisclaimer;
        private CoordinatorDecisionDto decision;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder analysisRunId(Long analysisRunId) { this.analysisRunId = analysisRunId; return this; }
        public Builder villageId(Long villageId) { this.villageId = villageId; return this; }
        public Builder villageName(String villageName) { this.villageName = villageName; return this; }
        public Builder recommendationType(String recommendationType) { this.recommendationType = recommendationType; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder rank(Integer rank) { this.rank = rank; return this; }
        public Builder keyFactors(String keyFactors) { this.keyFactors = keyFactors; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }
        public Builder freshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; return this; }
        public Builder confidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; return this; }
        public Builder basedOnSimulatedData(Boolean basedOnSimulatedData) { this.basedOnSimulatedData = basedOnSimulatedData; return this; }
        public Builder scoreBreakdown(String scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; return this; }
        public Builder dataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; return this; }
        public Builder decision(CoordinatorDecisionDto decision) { this.decision = decision; return this; }

        public RecommendationDto build() {
            return new RecommendationDto(id, analysisRunId, villageId, villageName, recommendationType,
                    score, rank, keyFactors, explanation, freshnessIndicator, confidenceLevel,
                    basedOnSimulatedData, scoreBreakdown, dataDisclaimer, decision);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnalysisRunId() { return analysisRunId; }
    public void setAnalysisRunId(Long analysisRunId) { this.analysisRunId = analysisRunId; }
    public Long getVillageId() { return villageId; }
    public void setVillageId(Long villageId) { this.villageId = villageId; }
    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public String getKeyFactors() { return keyFactors; }
    public void setKeyFactors(String keyFactors) { this.keyFactors = keyFactors; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getFreshnessIndicator() { return freshnessIndicator; }
    public void setFreshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; }
    public String getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public Boolean getBasedOnSimulatedData() { return basedOnSimulatedData; }
    public void setBasedOnSimulatedData(Boolean basedOnSimulatedData) { this.basedOnSimulatedData = basedOnSimulatedData; }
    public String getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(String scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
    public String getDataDisclaimer() { return dataDisclaimer; }
    public void setDataDisclaimer(String dataDisclaimer) { this.dataDisclaimer = dataDisclaimer; }
    public CoordinatorDecisionDto getDecision() { return decision; }
    public void setDecision(CoordinatorDecisionDto decision) { this.decision = decision; }
}
