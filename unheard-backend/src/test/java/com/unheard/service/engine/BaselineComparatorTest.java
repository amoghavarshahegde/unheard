package com.unheard.service.engine;

import com.unheard.domain.Village;
import com.unheard.dto.BaselineComparisonDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BaselineComparatorTest {

    @Test
    @DisplayName("Baseline comparator highlights silent high-risk zones that report-volume misses")
    void testBaselineComparisonLogic() {
        BaselineComparator comparator = new BaselineComparator();
        VerificationScorer scorer = new VerificationScorer();
        ScoringWeights weights = new ScoringWeights();

        // 1. Silent high-risk village: hazard 9.0, 0 reports
        Village silentVillage = Village.builder()
                .id(1L)
                .name("Silent High Risk")
                .hazardRiskScore(9.0)
                .hazardType("FLOOD")
                .population(3000)
                .vulnerablePopulation(1200)
                .expectedReportVolume(10)
                .actualReportCount(0)
                .connectivityQuality("NO_SIGNAL")
                .simulated(true)
                .build();

        // 2. Low-risk noisy village: hazard 2.0, 50 reports
        Village noisyVillage = Village.builder()
                .id(2L)
                .name("Noisy Low Risk")
                .hazardRiskScore(2.0)
                .hazardType("FLOOD")
                .population(8000)
                .vulnerablePopulation(500)
                .expectedReportVolume(50)
                .actualReportCount(50)
                .connectivityQuality("HIGH")
                .simulated(true)
                .build();

        List<Village> villages = List.of(silentVillage, noisyVillage);
        BaselineComparisonDto result = comparator.compare(villages, scorer, weights);

        assertNotNull(result);
        assertEquals(2, result.getBaselineRanking().size());
        assertEquals(2, result.getUnheardRanking().size());

        // In baseline (report volume DESC), Noisy village is #1, Silent is #2
        assertEquals("Noisy Low Risk", result.getBaselineRanking().get(0).getVillageName());
        assertEquals("Silent High Risk", result.getBaselineRanking().get(1).getVillageName());

        // In UNHEARD (verification score DESC), Silent village is #1, Noisy is #2
        assertEquals("Silent High Risk", result.getUnheardRanking().get(0).getVillageName());
        assertEquals("Noisy Low Risk", result.getUnheardRanking().get(1).getVillageName());

        // Check metrics: 1 silent high-risk zone
        assertEquals(1, result.getMetrics().getHighRiskUnderreportedCount());
    }
}
