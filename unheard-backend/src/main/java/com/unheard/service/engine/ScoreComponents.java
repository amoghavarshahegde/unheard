package com.unheard.service.engine;

/**
 * Holds the normalized (0-1) inputs and final score for one village scoring.
 * All fields are documented so the coordinator understands each contribution.
 */
public class ScoreComponents {
    // Verification Score components
    private double hazardRiskNormalized;        // village.hazardRiskScore / 10.0
    private double vulnerabilityNormalized;      // vulnerablePop / totalPop
    private double reportingGapNormalized;       // 1 - min(actual/expected, 1.0)
    private double verificationScore;            // final weighted score 0-100

    // Response Score components
    private double incidentSeverityNormalized;   // (severity-1)/4.0
    private double unmetResponseNeedNormalized;  // affected/pop if unassigned
    private double responseScore;               // final weighted score 0-100

    // Metadata
    private boolean baselineMissing;            // true if expectedReportVolume == 0
    private String freshnessIndicator;          // GREEN / YELLOW / RED
    private String confidenceLevel;             // HIGH / MEDIUM / LOW
    private String explanation;                 // human-readable
    private String scoreBreakdownJson;          // for storage

    public ScoreComponents() {}

    public ScoreComponents(double hazardRiskNormalized, double vulnerabilityNormalized,
                           double reportingGapNormalized, double verificationScore,
                           double incidentSeverityNormalized, double unmetResponseNeedNormalized,
                           double responseScore, boolean baselineMissing, String freshnessIndicator,
                           String confidenceLevel, String explanation, String scoreBreakdownJson) {
        this.hazardRiskNormalized = hazardRiskNormalized;
        this.vulnerabilityNormalized = vulnerabilityNormalized;
        this.reportingGapNormalized = reportingGapNormalized;
        this.verificationScore = verificationScore;
        this.incidentSeverityNormalized = incidentSeverityNormalized;
        this.unmetResponseNeedNormalized = unmetResponseNeedNormalized;
        this.responseScore = responseScore;
        this.baselineMissing = baselineMissing;
        this.freshnessIndicator = freshnessIndicator;
        this.confidenceLevel = confidenceLevel;
        this.explanation = explanation;
        this.scoreBreakdownJson = scoreBreakdownJson;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double hazardRiskNormalized;
        private double vulnerabilityNormalized;
        private double reportingGapNormalized;
        private double verificationScore;
        private double incidentSeverityNormalized;
        private double unmetResponseNeedNormalized;
        private double responseScore;
        private boolean baselineMissing;
        private String freshnessIndicator;
        private String confidenceLevel;
        private String explanation;
        private String scoreBreakdownJson;

        public Builder hazardRiskNormalized(double val) { this.hazardRiskNormalized = val; return this; }
        public Builder vulnerabilityNormalized(double val) { this.vulnerabilityNormalized = val; return this; }
        public Builder reportingGapNormalized(double val) { this.reportingGapNormalized = val; return this; }
        public Builder verificationScore(double val) { this.verificationScore = val; return this; }
        public Builder incidentSeverityNormalized(double val) { this.incidentSeverityNormalized = val; return this; }
        public Builder unmetResponseNeedNormalized(double val) { this.unmetResponseNeedNormalized = val; return this; }
        public Builder responseScore(double val) { this.responseScore = val; return this; }
        public Builder baselineMissing(boolean val) { this.baselineMissing = val; return this; }
        public Builder freshnessIndicator(String val) { this.freshnessIndicator = val; return this; }
        public Builder confidenceLevel(String val) { this.confidenceLevel = val; return this; }
        public Builder explanation(String val) { this.explanation = val; return this; }
        public Builder scoreBreakdownJson(String val) { this.scoreBreakdownJson = val; return this; }

        public ScoreComponents build() {
            return new ScoreComponents(hazardRiskNormalized, vulnerabilityNormalized, reportingGapNormalized,
                    verificationScore, incidentSeverityNormalized, unmetResponseNeedNormalized, responseScore,
                    baselineMissing, freshnessIndicator, confidenceLevel, explanation, scoreBreakdownJson);
        }
    }

    public double getHazardRiskNormalized() { return hazardRiskNormalized; }
    public void setHazardRiskNormalized(double hazardRiskNormalized) { this.hazardRiskNormalized = hazardRiskNormalized; }
    public double getVulnerabilityNormalized() { return vulnerabilityNormalized; }
    public void setVulnerabilityNormalized(double vulnerabilityNormalized) { this.vulnerabilityNormalized = vulnerabilityNormalized; }
    public double getReportingGapNormalized() { return reportingGapNormalized; }
    public void setReportingGapNormalized(double reportingGapNormalized) { this.reportingGapNormalized = reportingGapNormalized; }
    public double getVerificationScore() { return verificationScore; }
    public void setVerificationScore(double verificationScore) { this.verificationScore = verificationScore; }
    public double getIncidentSeverityNormalized() { return incidentSeverityNormalized; }
    public void setIncidentSeverityNormalized(double incidentSeverityNormalized) { this.incidentSeverityNormalized = incidentSeverityNormalized; }
    public double getUnmetResponseNeedNormalized() { return unmetResponseNeedNormalized; }
    public void setUnmetResponseNeedNormalized(double unmetResponseNeedNormalized) { this.unmetResponseNeedNormalized = unmetResponseNeedNormalized; }
    public double getResponseScore() { return responseScore; }
    public void setResponseScore(double responseScore) { this.responseScore = responseScore; }
    public boolean isBaselineMissing() { return baselineMissing; }
    public void setBaselineMissing(boolean baselineMissing) { this.baselineMissing = baselineMissing; }
    public String getFreshnessIndicator() { return freshnessIndicator; }
    public void setFreshnessIndicator(String freshnessIndicator) { this.freshnessIndicator = freshnessIndicator; }
    public String getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getScoreBreakdownJson() { return scoreBreakdownJson; }
    public void setScoreBreakdownJson(String scoreBreakdownJson) { this.scoreBreakdownJson = scoreBreakdownJson; }
}
