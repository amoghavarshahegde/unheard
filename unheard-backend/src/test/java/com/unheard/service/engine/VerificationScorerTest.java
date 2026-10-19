package com.unheard.service.engine;

import com.unheard.domain.Village;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VerificationScorerTest {

    private VerificationScorer scorer;
    private ScoringWeights weights;

    @BeforeEach
    void setup() {
        scorer = new VerificationScorer();
        weights = new ScoringWeights();
    }

    @Test
    @DisplayName("normalizeHazardRisk: 10.0 -> 1.0, 0.0 -> 0.0, 5.0 -> 0.5")
    void testNormalizeHazardRisk() {
        assertEquals(1.0, scorer.normalizeHazardRisk(10.0), 1e-6);
        assertEquals(0.0, scorer.normalizeHazardRisk(0.0), 1e-6);
        assertEquals(0.5, scorer.normalizeHazardRisk(5.0), 1e-6);
        assertEquals(1.0, scorer.normalizeHazardRisk(15.0), 1e-6); // clamped
    }

    @Test
    @DisplayName("normalizeVulnerability: 500/1000 -> 0.5")
    void testNormalizeVulnerability() {
        assertEquals(0.5, scorer.normalizeVulnerability(500, 1000), 1e-6);
        assertEquals(0.0, scorer.normalizeVulnerability(0, 1000), 1e-6);
        assertEquals(1.0, scorer.normalizeVulnerability(1000, 1000), 1e-6);
        assertEquals(0.0, scorer.normalizeVulnerability(100, 0), 1e-6); // zero population safe check
    }

    @Test
    @DisplayName("normalizeReportingGap: 0 actual, 10 expected -> 1.0 (full gap)")
    void testZeroReportsFullGap() {
        assertEquals(1.0, scorer.normalizeReportingGap(0, 10), 1e-6);
    }

    @Test
    @DisplayName("normalizeReportingGap: missing baseline (0 expected) -> 1.0 (max uncertainty)")
    void testMissingBaseline() {
        assertEquals(1.0, scorer.normalizeReportingGap(0, 0), 1e-6);
        assertEquals(1.0, scorer.normalizeReportingGap(5, null), 1e-6);
    }

    @Test
    @DisplayName("normalizeReportingGap: full reporting -> 0.0 gap")
    void testFullReporting() {
        assertEquals(0.0, scorer.normalizeReportingGap(10, 10), 1e-6);
        assertEquals(0.0, scorer.normalizeReportingGap(15, 10), 1e-6); // over-reporting
    }

    @Test
    @DisplayName("computeFreshness: null verification -> RED")
    void testFreshnessNeverVerified() {
        assertEquals("RED", scorer.computeFreshness(null));
    }

    @Test
    @DisplayName("computeFreshness: verified 1 hour ago -> GREEN")
    void testFreshnessGreen() {
        assertEquals("GREEN", scorer.computeFreshness(LocalDateTime.now().minusHours(1)));
    }

    @Test
    @DisplayName("computeFreshness: verified 36 hours ago -> RED")
    void testFreshnessRed() {
        assertEquals("RED", scorer.computeFreshness(LocalDateTime.now().minusHours(36)));
    }

    @Test
    @DisplayName("Zero-report high-risk village gets high verification score")
    void testSilentHighRiskVillageScore() {
        Village v = Village.builder()
                .name("Silent Village")
                .population(3000)
                .vulnerablePopulation(1200)
                .hazardRiskScore(9.0)
                .expectedReportVolume(10)
                .actualReportCount(0)
                .connectivityQuality("NO_SIGNAL")
                .lastFieldVerification(null)
                .simulated(true)
                .build();

        ScoreComponents sc = scorer.score(v, weights);
        assertTrue(sc.getVerificationScore() >= 75.0,
                "Silent high-risk village should score >= 75, got: " + sc.getVerificationScore());
        assertEquals("RED", sc.getFreshnessIndicator());
        assertEquals("LOW", sc.getConfidenceLevel());
    }

    @Test
    @DisplayName("Well-reported low-risk village gets low verification score")
    void testWellReportedLowRiskVillageScore() {
        Village v = Village.builder()
                .name("Well Reported Village")
                .population(5000)
                .vulnerablePopulation(500)
                .hazardRiskScore(2.0)
                .expectedReportVolume(20)
                .actualReportCount(19)
                .connectivityQuality("HIGH")
                .lastFieldVerification(LocalDateTime.now().minusHours(1))
                .simulated(true)
                .build();

        ScoreComponents sc = scorer.score(v, weights);
        assertTrue(sc.getVerificationScore() <= 30.0,
                "Well-reported low-risk village should score <= 30, got: " + sc.getVerificationScore());
        assertEquals("GREEN", sc.getFreshnessIndicator());
    }

    @Test
    @DisplayName("Missing baseline flagged in explanation and metadata")
    void testMissingBaselineExplanation() {
        Village v = Village.builder()
                .name("No Baseline Village")
                .population(2000)
                .vulnerablePopulation(800)
                .hazardRiskScore(7.0)
                .expectedReportVolume(0)
                .actualReportCount(0)
                .connectivityQuality("LOW")
                .lastFieldVerification(null)
                .simulated(true)
                .build();

        ScoreComponents sc = scorer.score(v, weights);
        assertTrue(sc.isBaselineMissing(), "Missing baseline should be flagged");
        assertTrue(sc.getExplanation().contains("No reporting baseline"),
                "Explanation should explicitly mention missing baseline");
    }
}
