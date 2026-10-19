package com.unheard.service.engine;

import com.unheard.domain.IncidentReport;
import com.unheard.domain.Village;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResponseScorerTest {

    private ResponseScorer scorer;
    private ScoringWeights weights;

    @BeforeEach
    void setup() {
        scorer = new ResponseScorer();
        weights = new ScoringWeights();
    }

    @Test
    @DisplayName("normalizeSeverity: 1->0.0, 3->0.5, 5->1.0")
    void testNormalizeSeverity() {
        assertEquals(0.0, scorer.normalizeSeverity(1), 1e-6);
        assertEquals(0.5, scorer.normalizeSeverity(3), 1e-6);
        assertEquals(1.0, scorer.normalizeSeverity(5), 1e-6);
    }

    @Test
    @DisplayName("normalizeUnmetNeed: resource assigned -> 0.5")
    void testUnmetNeedWithResource() {
        assertEquals(0.5, scorer.normalizeUnmetNeed(500, 1000, true), 1e-6);
    }

    @Test
    @DisplayName("normalizeUnmetNeed: no resource, 800 of 1000 -> 0.8")
    void testUnmetNeedNoResource() {
        assertEquals(0.8, scorer.normalizeUnmetNeed(800, 1000, false), 1e-6);
    }

    @Test
    @DisplayName("normalizeUnmetNeed: null peopleAffected -> 0.5")
    void testUnmetNeedNullAffected() {
        assertEquals(0.5, scorer.normalizeUnmetNeed(null, 1000, false), 1e-6);
    }

    @Test
    @DisplayName("Critical severity, high vulnerability, no resource -> high response score")
    void testHighResponseScore() {
        Village v = Village.builder()
                .name("Test Village")
                .population(3000)
                .vulnerablePopulation(1500)
                .hazardRiskScore(8.0)
                .hazardType("FLOOD")
                .expectedReportVolume(10)
                .actualReportCount(2)
                .connectivityQuality("LOW")
                .simulated(true)
                .build();

        IncidentReport incident = IncidentReport.builder()
                .village(v)
                .incidentType("FLOOD_INUNDATION")
                .severity(5)
                .peopleAffected(2000)
                .reportTimestamp(LocalDateTime.now())
                .verificationStatus("VERIFIED")
                .flaggedDuplicate(false)
                .build();

        ScoreComponents sc = scorer.score(incident, v, weights, false);
        assertTrue(sc.getResponseScore() >= 75.0,
                "Critical incident should score >= 75, got: " + sc.getResponseScore());
    }

    @Test
    @DisplayName("Verification score and response score operate independently")
    void testScoresAreIndependent() {
        VerificationScorer vs = new VerificationScorer();

        Village v = Village.builder()
                .name("Test")
                .population(2000)
                .vulnerablePopulation(500)
                .hazardRiskScore(3.0)
                .expectedReportVolume(10)
                .actualReportCount(9)
                .connectivityQuality("HIGH")
                .lastFieldVerification(LocalDateTime.now().minusHours(1))
                .simulated(true)
                .build();

        IncidentReport incident = IncidentReport.builder()
                .village(v)
                .incidentType("FLOOD")
                .severity(5)
                .peopleAffected(1800)
                .reportTimestamp(LocalDateTime.now())
                .verificationStatus("VERIFIED")
                .flaggedDuplicate(false)
                .build();

        ScoreComponents verSc = vs.score(v, weights);
        ScoreComponents respSc = scorer.score(incident, v, weights, false);

        // Low hazard risk + complete reporting -> low verification score
        assertTrue(verSc.getVerificationScore() < 35.0, "Verification score should be low: " + verSc.getVerificationScore());
        // Critical severity (5) -> high response score
        assertTrue(respSc.getResponseScore() > 65.0, "Response score should be high: " + respSc.getResponseScore());
    }
}
