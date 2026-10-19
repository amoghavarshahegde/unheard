package com.unheard.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recommendations", indexes = {
        @Index(name = "idx_rec_run", columnList = "analysis_run_id"),
        @Index(name = "idx_rec_village", columnList = "village_id")
})
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_run_id", nullable = false)
    private AnalysisRun analysisRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private Village village;

    @Column(nullable = false)
    private String recommendationType;

    @Column(nullable = false)
    private Double score;

    @Column(name = "recommendation_rank", nullable = false)
    private Integer rank;

    @Column(length = 500)
    private String keyFactors;

    @Column(length = 2000, nullable = false)
    private String explanation;

    @Column(nullable = false)
    private String freshnessIndicator;

    @Column(nullable = false)
    private String confidenceLevel;

    @Column(nullable = false)
    private Boolean basedOnSimulatedData;

    @Column(length = 500)
    private String scoreBreakdown;

    public Recommendation() {}

    public Recommendation(Long id, AnalysisRun analysisRun, Village village, String recommendationType,
                          Double score, Integer rank, String keyFactors, String explanation,
                          String freshnessIndicator, String confidenceLevel, Boolean basedOnSimulatedData,
                          String scoreBreakdown) {
        this.id = id;
        this.analysisRun = analysisRun;
        this.village = village;
        this.recommendationType = recommendationType;
        this.score = score;
        this.rank = rank;
        this.keyFactors = keyFactors;
        this.explanation = explanation;
        this.freshnessIndicator = freshnessIndicator;
        this.confidenceLevel = confidenceLevel;
        this.basedOnSimulatedData = basedOnSimulatedData;
        this.scoreBreakdown = scoreBreakdown;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private AnalysisRun analysisRun;
        private Village village;
        private String recommendationType;
        private Double score = 0.0;
        private Integer rank = 1;
        private String keyFactors;
        private String explanation;
        private String freshnessIndicator = "YELLOW";
        private String confidenceLevel = "MEDIUM";
        private Boolean basedOnSimulatedData = true;
        private String scoreBreakdown;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder analysisRun(AnalysisRun analysisRun) { this.analysisRun = analysisRun; return this; }
        public Builder village(Village village) { this.village = village; return this; }
        public Builder recommendationType(String recommendationType) { this.recommendationType = recommendationType; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder rank(Integer rank) { this.rank = rank; return this; }
        public Builder keyFactors(String keyFactors) { this.keyFactors = keyFactors; return this; }
        public Builder explanation(String explanation) { this.explanation = explanation; return this; }
        public Builder freshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; return this; }
        public Builder confidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; return this; }
        public Builder basedOnSimulatedData(Boolean basedOnSimulatedData) { this.basedOnSimulatedData = basedOnSimulatedData; return this; }
        public Builder scoreBreakdown(String scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; return this; }

        public Recommendation build() {
            return new Recommendation(id, analysisRun, village, recommendationType, score, rank,
                    keyFactors, explanation, freshnessIndicator, confidenceLevel, basedOnSimulatedData, scoreBreakdown);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AnalysisRun getAnalysisRun() { return analysisRun; }
    public void setAnalysisRun(AnalysisRun analysisRun) { this.analysisRun = analysisRun; }
    public Village getVillage() { return village; }
    public void setVillage(Village village) { this.village = village; }
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
}
