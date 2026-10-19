package com.unheard.service.engine;

import com.unheard.domain.Village;
import com.unheard.dto.BaselineComparisonDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Compares baseline (report-volume) ranking with UNHEARD verification ranking.
 *
 * The baseline method ranks areas by descending report count - a naive approach
 * that can miss high-risk, low-connectivity communities.
 *
 * DISCLAIMER: Results demonstrate prototype behavior with simulated data.
 * They do NOT represent proven operational effectiveness or real rescue outcomes.
 */
@Component
public class BaselineComparator {

    private static final double HIGH_RISK_THRESHOLD = 6.0; // hazardRiskScore >= 6
    private static final double LOW_REPORT_THRESHOLD = 0.5; // actual < 50% of expected

    public BaselineComparisonDto compare(List<Village> villages, VerificationScorer scorer,
                                         ScoringWeights weights) {
        // Compute UNHEARD scores for all villages
        List<ScoredVillage> scored = new ArrayList<>();
        for (Village v : villages) {
            ScoreComponents sc = scorer.score(v, weights);
            scored.add(new ScoredVillage(v, sc.getVerificationScore()));
        }

        // Baseline ranking: sort by actualReportCount DESC
        List<ScoredVillage> baselineRanked = scored.stream()
                .sorted(Comparator.comparingInt((ScoredVillage s) -> s.village.getActualReportCount()).reversed())
                .collect(Collectors.toList());

        // UNHEARD ranking: sort by verificationScore DESC
        List<ScoredVillage> unheardRanked = scored.stream()
                .sorted(Comparator.comparingDouble((ScoredVillage s) -> s.unheardScore).reversed())
                .collect(Collectors.toList());

        // Build DTOs
        List<BaselineComparisonDto.VillageRankingEntry> baselineDtos = new ArrayList<>();
        for (int i = 0; i < baselineRanked.size(); i++) {
            ScoredVillage sv = baselineRanked.get(i);
            baselineDtos.add(toEntry(i + 1, sv));
        }

        List<BaselineComparisonDto.VillageRankingEntry> unheardDtos = new ArrayList<>();
        for (int i = 0; i < unheardRanked.size(); i++) {
            ScoredVillage sv = unheardRanked.get(i);
            unheardDtos.add(toEntry(i + 1, sv));
        }

        // Compute metrics
        List<Village> silentHighRisk = villages.stream()
                .filter(v -> v.getHazardRiskScore() >= HIGH_RISK_THRESHOLD)
                .filter(this::isSilentOrLowReport)
                .collect(Collectors.toList());

        int totalSilentHighRisk = silentHighRisk.size();

        // Villages detected as top-half in baseline vs top-half in UNHEARD
        int halfSize = Math.max(1, villages.size() / 2);
        List<Long> baselineTopHalf = baselineRanked.subList(0, Math.min(halfSize, baselineRanked.size()))
                .stream().map(sv -> sv.village.getId()).collect(Collectors.toList());
        List<Long> unheardTopHalf = unheardRanked.subList(0, Math.min(halfSize, unheardRanked.size()))
                .stream().map(sv -> sv.village.getId()).collect(Collectors.toList());

        List<Long> silentIds = silentHighRisk.stream().map(Village::getId).collect(Collectors.toList());

        long detectedByBaseline = silentIds.stream().filter(baselineTopHalf::contains).count();
        long detectedByUnheard = silentIds.stream().filter(unheardTopHalf::contains).count();
        long missedByBaseline = totalSilentHighRisk - detectedByBaseline;
        long missedByUnheard = totalSilentHighRisk - detectedByUnheard;

        double unheardRate = totalSilentHighRisk == 0 ? 0.0 : (double) detectedByUnheard / totalSilentHighRisk;
        double baselineRate = totalSilentHighRisk == 0 ? 0.0 : (double) detectedByBaseline / totalSilentHighRisk;

        BaselineComparisonDto.ComparisonMetrics metrics = BaselineComparisonDto.ComparisonMetrics.builder()
                .totalVillages(villages.size())
                .highRiskUnderreportedCount(totalSilentHighRisk)
                .detectedByUnheard((int) detectedByUnheard)
                .detectedByBaseline((int) detectedByBaseline)
                .missedByBaseline((int) missedByBaseline)
                .missedByUnheard((int) missedByUnheard)
                .unheardDetectionRate(unheardRate)
                .baselineDetectionRate(baselineRate)
                .methodologyNote("High-risk is defined as hazardRiskScore >= " + HIGH_RISK_THRESHOLD +
                        ". Underreported means actualReportCount < 50% of expected, or connectivity LOW/NO_SIGNAL. " +
                        "Detection means the village appears in the top half of the ranking. " +
                        "These results use simulated data and demonstrate prototype behavior only.")
                .build();

        return BaselineComparisonDto.builder()
                .baselineRanking(baselineDtos)
                .unheardRanking(unheardDtos)
                .metrics(metrics)
                .disclaimer("SIMULATION RESULTS: All data is fictional seed data. " +
                        "These rankings do not represent real emergencies. " +
                        "Results demonstrate prototype behavior, not proven operational effectiveness.")
                .build();
    }

    private boolean isSilentOrLowReport(Village v) {
        if (v.getActualReportCount() == 0) return true;
        if ("LOW".equals(v.getConnectivityQuality()) || "NO_SIGNAL".equals(v.getConnectivityQuality())) return true;
        if (v.getExpectedReportVolume() > 0) {
            return (double) v.getActualReportCount() / v.getExpectedReportVolume() < LOW_REPORT_THRESHOLD;
        }
        return false;
    }

    private BaselineComparisonDto.VillageRankingEntry toEntry(int rank, ScoredVillage sv) {
        Village v = sv.village;
        boolean isSilentHighRisk = v.getHazardRiskScore() >= HIGH_RISK_THRESHOLD && isSilentOrLowReport(v);
        String explanation = isSilentHighRisk
                ? String.format("%s is a SILENT HIGH-RISK zone: high hazard score (%.1f/10) with low reporting. " +
                        "Baseline method ranks lower because it depends on report volume.",
                        v.getName(), v.getHazardRiskScore())
                : String.format("%s: hazard %.1f/10, %d reports received.",
                        v.getName(), v.getHazardRiskScore(), v.getActualReportCount());

        return BaselineComparisonDto.VillageRankingEntry.builder()
                .rank(rank)
                .villageId(v.getId())
                .villageName(v.getName())
                .actualReportCount(v.getActualReportCount())
                .unheardScore(sv.unheardScore)
                .hazardType(v.getHazardType())
                .hazardRiskScore(v.getHazardRiskScore())
                .population(v.getPopulation())
                .vulnerablePopulation(v.getVulnerablePopulation())
                .connectivityQuality(v.getConnectivityQuality())
                .isSilentHighRisk(isSilentHighRisk)
                .explanation(explanation)
                .build();
    }

    /** Inner class to hold a village + its UNHEARD score during comparison */
    static class ScoredVillage {
        final Village village;
        final double unheardScore;

        ScoredVillage(Village village, double unheardScore) {
            this.village = village;
            this.unheardScore = unheardScore;
        }
    }
}
