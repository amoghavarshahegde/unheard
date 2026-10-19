package com.unheard.service.engine;

import com.unheard.domain.IncidentReport;
import com.unheard.domain.Village;
import org.springframework.stereotype.Component;

/**
 * Computes the UNHEARD Response Priority Score for a confirmed incident.
 *
 * Formula:
 *   ResponseScore = 100 x (W_sev x IncidentSeverity + W_vuln x Vulnerability + W_need x UnmetResponseNeed)
 *
 * All inputs normalized to [0, 1].
 */
@Component
public class ResponseScorer {

    public ScoreComponents score(IncidentReport incident, Village village,
                                 ScoringWeights weights, boolean hasAssignedResource) {
        double severityNorm = normalizeSeverity(incident.getSeverity());
        double vulnNorm = normalizeVulnerability(village.getVulnerablePopulation(), village.getPopulation());
        double unmetNorm = normalizeUnmetNeed(
                incident.getPeopleAffected(), village.getPopulation(), hasAssignedResource);

        double responseScore = 100.0 * (
                weights.getResponse().getSeverityWeight() * severityNorm +
                weights.getResponse().getVulnerabilityWeight() * vulnNorm +
                weights.getResponse().getUnmetNeedWeight() * unmetNorm
        );

        String breakdown = String.format(
                "{\"incidentSeverityNorm\":%.3f,\"vulnerabilityNorm\":%.3f,\"unmetNeedNorm\":%.3f,"
                + "\"weights\":{\"severity\":%.2f,\"vulnerability\":%.2f,\"unmetNeed\":%.2f}}",
                severityNorm, vulnNorm, unmetNorm,
                weights.getResponse().getSeverityWeight(),
                weights.getResponse().getVulnerabilityWeight(),
                weights.getResponse().getUnmetNeedWeight()
        );

        String explanation = buildExplanation(incident, village, severityNorm, vulnNorm, unmetNorm,
                responseScore, hasAssignedResource);

        return ScoreComponents.builder()
                .incidentSeverityNormalized(severityNorm)
                .vulnerabilityNormalized(vulnNorm)
                .unmetResponseNeedNormalized(unmetNorm)
                .responseScore(responseScore)
                .freshnessIndicator("YELLOW") // incidents are recent but source may vary
                .confidenceLevel(incident.getVerificationStatus().equals("VERIFIED") ? "HIGH" : "MEDIUM")
                .explanation(explanation)
                .scoreBreakdownJson(breakdown)
                .build();
    }

    /** Normalize severity from 1-5 to 0-1 */
    public double normalizeSeverity(int severity) {
        return Math.min(Math.max((severity - 1) / 4.0, 0.0), 1.0);
    }

    public double normalizeVulnerability(int vulnerablePop, int totalPop) {
        if (totalPop <= 0) return 0.0;
        return Math.min((double) vulnerablePop / totalPop, 1.0);
    }

    /**
     * Unmet response need:
     * - If no resource assigned: min(peopleAffected / population, 1.0)
     * - If resource assigned: 0.5 (partial coverage)
     * - If peopleAffected is null: 0.5 (unknown)
     */
    public double normalizeUnmetNeed(Integer peopleAffected, int population, boolean hasAssignedResource) {
        if (hasAssignedResource) return 0.5;
        if (peopleAffected == null || population <= 0) return 0.5;
        return Math.min((double) peopleAffected / population, 1.0);
    }

    private String buildExplanation(IncidentReport incident, Village village,
                                    double sevNorm, double vulnNorm, double unmetNorm,
                                    double score, boolean hasResource) {
        return String.format(
                "%s in %s has a Response Priority Score of %.1f/100. " +
                "Severity %d/5 (normalized: %.2f). Vulnerable population %.0f%%. " +
                "Unmet response need: %.0f%% (%s). " +
                "Verification status: %s. " +
                "IMPORTANT: This recommendation is based on simulated data. " +
                "A human coordinator must verify before any field action is taken.",
                incident.getIncidentType(), village.getName(), score,
                incident.getSeverity(), sevNorm,
                vulnNorm * 100,
                unmetNorm * 100, hasResource ? "resource assigned" : "no resource assigned",
                incident.getVerificationStatus()
        );
    }
}
