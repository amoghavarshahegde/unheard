package com.unheard.service.engine;

import com.unheard.domain.Village;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Computes the UNHEARD Verification Priority Score for a village.
 *
 * Formula:
 *   VerificationScore = 100 x (W_hazard x HazardRisk + W_vuln x Vulnerability + W_gap x ReportingGap)
 *
 * All inputs are normalized to [0, 1] before weighting.
 *
 * Key decisions:
 * - If expectedReportVolume == 0 (no baseline), reportingGap = 1.0. This is a
 *   CONSERVATIVE CHOICE: it flags missing-baseline villages for human review,
 *   NOT a claim that danger exists.
 * - Freshness is computed from lastFieldVerification. Never-verified villages
 *   receive RED freshness, not an automatic danger flag.
 */
@Component
public class VerificationScorer {

    @Value("${unheard.freshness.green-hours:6}")
    private int greenHours = 6;

    @Value("${unheard.freshness.yellow-hours:24}")
    private int yellowHours = 24;

    /**
     * Score a village given the scoring weights.
     *
     * @param village the village to score
     * @param weights configured scoring weights
     * @return populated ScoreComponents with verification score and metadata
     */
    public ScoreComponents score(Village village, ScoringWeights weights) {
        // 1. Normalize inputs
        double hazardNorm = normalizeHazardRisk(village.getHazardRiskScore());
        double vulnNorm = normalizeVulnerability(village.getVulnerablePopulation(), village.getPopulation());
        boolean baselineMissing = (village.getExpectedReportVolume() == null || village.getExpectedReportVolume() == 0);
        double gapNorm = normalizeReportingGap(village.getActualReportCount(), village.getExpectedReportVolume());

        // 2. Compute weighted verification score
        double verScore = 100.0 * (
                weights.getVerification().getHazardWeight() * hazardNorm +
                weights.getVerification().getVulnerabilityWeight() * vulnNorm +
                weights.getVerification().getReportingGapWeight() * gapNorm
        );

        // 3. Freshness
        String freshness = computeFreshness(village.getLastFieldVerification());

        // 4. Confidence level
        String confidence = computeConfidence(baselineMissing, freshness, village.getConnectivityQuality());

        // 5. Human-readable explanation
        String explanation = buildExplanation(village, hazardNorm, vulnNorm, gapNorm, verScore,
                baselineMissing, freshness, confidence);

        // 6. Score breakdown JSON
        String breakdown = String.format(
                "{\"hazardRiskNorm\":%.3f,\"vulnerabilityNorm\":%.3f,\"reportingGapNorm\":%.3f,"
                + "\"weights\":{\"hazard\":%.2f,\"vulnerability\":%.2f,\"reportingGap\":%.2f}}",
                hazardNorm, vulnNorm, gapNorm,
                weights.getVerification().getHazardWeight(),
                weights.getVerification().getVulnerabilityWeight(),
                weights.getVerification().getReportingGapWeight()
        );

        return ScoreComponents.builder()
                .hazardRiskNormalized(hazardNorm)
                .vulnerabilityNormalized(vulnNorm)
                .reportingGapNormalized(gapNorm)
                .verificationScore(verScore)
                .baselineMissing(baselineMissing)
                .freshnessIndicator(freshness)
                .confidenceLevel(confidence)
                .explanation(explanation)
                .scoreBreakdownJson(breakdown)
                .build();
    }

    /** Normalize hazard risk from 0-10 scale to 0-1 */
    public double normalizeHazardRisk(double hazardRiskScore) {
        return Math.min(Math.max(hazardRiskScore / 10.0, 0.0), 1.0);
    }

    /** Normalize vulnerability as fraction of total population */
    public double normalizeVulnerability(int vulnerablePop, int totalPop) {
        if (totalPop <= 0) return 0.0;
        return Math.min((double) vulnerablePop / totalPop, 1.0);
    }

    /**
     * Normalize reporting gap.
     * Gap = 1 - min(actual / expected, 1.0)
     * If expected == 0 (no baseline), returns 1.0 (maximum uncertainty).
     * This does NOT imply danger; it flags that the baseline is missing.
     */
    public double normalizeReportingGap(int actualReports, Integer expectedReports) {
        if (expectedReports == null || expectedReports == 0) {
            return 1.0; // No baseline - maximum uncertainty, not maximum danger
        }
        double ratio = (double) actualReports / expectedReports;
        return 1.0 - Math.min(ratio, 1.0);
    }

    /** Determine freshness color from last verification time */
    public String computeFreshness(LocalDateTime lastVerification) {
        if (lastVerification == null) return "RED";
        long hoursAgo = Duration.between(lastVerification, LocalDateTime.now()).toHours();
        if (hoursAgo <= greenHours) return "GREEN";
        if (hoursAgo <= yellowHours) return "YELLOW";
        return "RED";
    }

    private String computeConfidence(boolean baselineMissing, String freshness, String connectivity) {
        if (baselineMissing || "RED".equals(freshness)) return "LOW";
        if ("YELLOW".equals(freshness) || "LOW".equals(connectivity) || "NO_SIGNAL".equals(connectivity)) return "MEDIUM";
        return "HIGH";
    }

    private String buildExplanation(Village v, double hazardNorm, double vulnNorm, double gapNorm,
                                    double score, boolean baselineMissing, String freshness, String confidence) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s has a Verification Priority Score of %.1f/100. ", v.getName(), score));
        sb.append(String.format("Hazard risk is %.0f%% (normalized: %.2f). ", v.getHazardRiskScore() * 10, hazardNorm));
        sb.append(String.format("Vulnerable population is %d of %d (%.0f%%). ",
                v.getVulnerablePopulation(), v.getPopulation(), vulnNorm * 100));

        if (baselineMissing) {
            sb.append("No reporting baseline is available - reporting gap set to maximum uncertainty (1.0). ");
            sb.append("This does NOT confirm an emergency; it indicates the absence of data. ");
        } else {
            sb.append(String.format("Reporting gap: %d of %d expected reports received (gap: %.0f%%). ",
                    v.getActualReportCount(), v.getExpectedReportVolume(), gapNorm * 100));
        }

        sb.append(String.format("Field verification freshness: %s. ", freshness));
        if ("RED".equals(freshness)) {
            sb.append(v.getLastFieldVerification() == null
                    ? "No field verification on record. "
                    : "Last field verification is more than 24 hours old. ");
        }

        sb.append(String.format("Confidence level: %s. ", confidence));
        sb.append("IMPORTANT: This recommendation is based on simulated data. ");
        sb.append("A human coordinator must verify before any field action is taken.");
        return sb.toString();
    }
}
