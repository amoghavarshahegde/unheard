package com.unheard.service;

import com.unheard.domain.*;
import com.unheard.dto.AnalysisRunDto;
import com.unheard.dto.RecommendationDto;
import com.unheard.repository.*;
import com.unheard.service.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core UNHEARD analysis engine.
 *
 * Produces two types of recommendations per run:
 * 1. VERIFICATION_PRIORITY - villages with no/low reports but high hazard risk
 * 2. RESPONSE_PRIORITY - villages with active verified/pending incidents
 *
 * PROTOTYPE LIMITATIONS (documented for transparency):
 * - All data is simulated seed data
 * - Weights have not been validated with real disaster outcomes
 * - Human coordinator review is MANDATORY before any field action
 */
@Service
public class AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private static final String SIMULATED_DATA_DISCLAIMER =
            "PROTOTYPE: All data is simulated. Recommendations must be reviewed by a human coordinator " +
            "before any field action. Weights and thresholds are illustrative defaults only.";

    private final VillageRepository villageRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final AnalysisRunRepository analysisRunRepository;
    private final RecommendationRepository recommendationRepository;
    private final CoordinatorDecisionRepository decisionRepository;
    private final VerificationScorer verificationScorer;
    private final ResponseScorer responseScorer;
    private final ScoringWeights scoringWeights;

    public AnalysisService(VillageRepository villageRepository,
                           IncidentReportRepository incidentReportRepository,
                           AnalysisRunRepository analysisRunRepository,
                           RecommendationRepository recommendationRepository,
                           CoordinatorDecisionRepository decisionRepository,
                           VerificationScorer verificationScorer,
                           ResponseScorer responseScorer,
                           ScoringWeights scoringWeights) {
        this.villageRepository = villageRepository;
        this.incidentReportRepository = incidentReportRepository;
        this.analysisRunRepository = analysisRunRepository;
        this.recommendationRepository = recommendationRepository;
        this.decisionRepository = decisionRepository;
        this.verificationScorer = verificationScorer;
        this.responseScorer = responseScorer;
        this.scoringWeights = scoringWeights;
    }

    /**
     * Run a full analysis over all villages and active incidents.
     *
     * @param triggerSource who/what triggered the run (MANUAL, SCHEDULED, SEED)
     * @return the persisted AnalysisRun record
     */
    @Transactional
    public AnalysisRunDto runAnalysis(String triggerSource) {
        log.info("Starting UNHEARD analysis run. Trigger: {}", triggerSource);

        List<Village> allVillages = villageRepository.findAll();
        LocalDateTime windowStart = LocalDateTime.now().minusHours(24);

        // Step 1: Update actual report counts from database (reflects submissions since window start)
        for (Village v : allVillages) {
            long count = incidentReportRepository.countByVillageIdAndReportTimestampAfter(v.getId(), windowStart);
            v.setActualReportCount((int) count);
        }
        villageRepository.saveAll(allVillages);

        // Step 2: Score all villages for VERIFICATION priority
        List<ScoredVillageEntry> verificationCandidates = new ArrayList<>();
        for (Village v : allVillages) {
            ScoreComponents sc = verificationScorer.score(v, scoringWeights);
            verificationCandidates.add(new ScoredVillageEntry(v, sc, "VERIFICATION_PRIORITY"));
        }
        verificationCandidates.sort(Comparator.comparingDouble(e -> -e.components.getVerificationScore()));

        // Step 3: Score active incidents for RESPONSE priority
        List<IncidentReport> activeIncidents = incidentReportRepository.findAll().stream()
                .filter(r -> !r.getFlaggedDuplicate())
                .filter(r -> r.getReportTimestamp().isAfter(windowStart))
                .collect(Collectors.toList());

        // Group by village - take highest-severity incident per village
        Map<Long, IncidentReport> worstIncidentByVillage = new LinkedHashMap<>();
        for (IncidentReport r : activeIncidents) {
            Long vid = r.getVillage().getId();
            if (!worstIncidentByVillage.containsKey(vid)
                    || r.getSeverity() > worstIncidentByVillage.get(vid).getSeverity()) {
                worstIncidentByVillage.put(vid, r);
            }
        }

        List<ScoredVillageEntry> responseCandidates = new ArrayList<>();
        for (Map.Entry<Long, IncidentReport> entry : worstIncidentByVillage.entrySet()) {
            Village v = entry.getValue().getVillage();
            // Eagerly fetch village if needed
            Village fullVillage = allVillages.stream()
                    .filter(village -> village.getId().equals(v.getId()))
                    .findFirst()
                    .orElse(v);
            boolean hasResource = false; // In MVP, no resource assignment tracking per incident
            ScoreComponents sc = responseScorer.score(entry.getValue(), fullVillage, scoringWeights, hasResource);
            responseCandidates.add(new ScoredVillageEntry(fullVillage, sc, "RESPONSE_PRIORITY"));
        }
        responseCandidates.sort(Comparator.comparingDouble(e -> -e.components.getResponseScore()));

        // Step 4: Persist the AnalysisRun
        long highRiskCount = verificationCandidates.stream()
                .filter(e -> e.components.getVerificationScore() >= 60.0)
                .count();
        long silentZoneCount = allVillages.stream()
                .filter(v -> v.getActualReportCount() == 0 && v.getHazardRiskScore() >= 6.0)
                .count();

        AnalysisRun run = AnalysisRun.builder()
                .runAt(LocalDateTime.now())
                .triggerSource(triggerSource)
                .villagesAnalyzed(allVillages.size())
                .highRiskCount((int) highRiskCount)
                .silentZoneCount((int) silentZoneCount)
                .totalRecommendations(verificationCandidates.size() + responseCandidates.size())
                .notes(SIMULATED_DATA_DISCLAIMER)
                .build();
        run = analysisRunRepository.save(run);

        // Step 5: Persist Recommendations
        List<Recommendation> allRecs = new ArrayList<>();

        for (int i = 0; i < verificationCandidates.size(); i++) {
            ScoredVillageEntry e = verificationCandidates.get(i);
            allRecs.add(buildRecommendation(run, e.village, e.components, "VERIFICATION_PRIORITY", i + 1));
        }
        for (int i = 0; i < responseCandidates.size(); i++) {
            ScoredVillageEntry e = responseCandidates.get(i);
            allRecs.add(buildRecommendation(run, e.village, e.components, "RESPONSE_PRIORITY", i + 1));
        }

        recommendationRepository.saveAll(allRecs);
        log.info("Analysis complete. Run id={}. {} verification + {} response recommendations.",
                run.getId(), verificationCandidates.size(), responseCandidates.size());

        return toRunDto(run);
    }

    public AnalysisRunDto getLatestRun() {
        return analysisRunRepository.findTopByOrderByRunAtDesc()
                .map(this::toRunDto)
                .orElse(null);
    }

    public List<AnalysisRunDto> getRecentRuns() {
        return analysisRunRepository.findAll().stream()
                .sorted((a, b) -> b.getRunAt().compareTo(a.getRunAt()))
                .limit(10)
                .map(this::toRunDto)
                .collect(Collectors.toList());
    }

    public List<RecommendationDto> getRecommendationsForRun(Long runId, String type) {
        List<Recommendation> recs = type != null
                ? recommendationRepository.findByAnalysisRunIdAndRecommendationTypeOrderByRankAsc(runId, type)
                : recommendationRepository.findByAnalysisRunIdOrderByRankAsc(runId);

        return recs.stream()
                .map(this::toRecDto)
                .collect(Collectors.toList());
    }

    public List<RecommendationDto> getLatestRecommendations() {
        return getLatestRecommendations(null);
    }

    public List<RecommendationDto> getLatestRecommendations(String type) {
        return analysisRunRepository.findTopByOrderByRunAtDesc()
                .map(run -> getRecommendationsForRun(run.getId(), type))
                .orElse(Collections.emptyList());
    }

    private Recommendation buildRecommendation(AnalysisRun run, Village village,
                                                ScoreComponents sc, String type, int rank) {
        double score = "VERIFICATION_PRIORITY".equals(type)
                ? sc.getVerificationScore()
                : sc.getResponseScore();

        List<String> factors = new ArrayList<>();
        if (sc.getHazardRiskNormalized() > 0.6) factors.add("HIGH_HAZARD_RISK");
        if (sc.getVulnerabilityNormalized() > 0.4) factors.add("HIGH_VULNERABILITY");
        if (sc.getReportingGapNormalized() > 0.5) factors.add("SIGNIFICANT_REPORTING_GAP");
        if (sc.isBaselineMissing()) factors.add("NO_REPORTING_BASELINE");
        if ("RED".equals(sc.getFreshnessIndicator())) factors.add("STALE_FIELD_DATA");
        if (sc.getIncidentSeverityNormalized() > 0.6) factors.add("HIGH_SEVERITY_INCIDENT");
        if (sc.getUnmetResponseNeedNormalized() > 0.6) factors.add("HIGH_UNMET_NEED");

        return Recommendation.builder()
                .analysisRun(run)
                .village(village)
                .recommendationType(type)
                .score(Math.round(score * 10.0) / 10.0)
                .rank(rank)
                .keyFactors(String.join("|", factors))
                .explanation(sc.getExplanation())
                .freshnessIndicator(sc.getFreshnessIndicator())
                .confidenceLevel(sc.getConfidenceLevel())
                .basedOnSimulatedData(true)
                .scoreBreakdown(sc.getScoreBreakdownJson())
                .build();
    }

    private RecommendationDto toRecDto(Recommendation r) {
        Optional<CoordinatorDecision> decision = decisionRepository.findByRecommendationId(r.getId());
        return RecommendationDto.builder()
                .id(r.getId())
                .analysisRunId(r.getAnalysisRun().getId())
                .villageId(r.getVillage().getId())
                .villageName(r.getVillage().getName())
                .recommendationType(r.getRecommendationType())
                .score(r.getScore())
                .rank(r.getRank())
                .keyFactors(r.getKeyFactors())
                .explanation(r.getExplanation())
                .freshnessIndicator(r.getFreshnessIndicator())
                .confidenceLevel(r.getConfidenceLevel())
                .basedOnSimulatedData(r.getBasedOnSimulatedData())
                .scoreBreakdown(r.getScoreBreakdown())
                .dataDisclaimer(SIMULATED_DATA_DISCLAIMER)
                .decision(decision.map(this::toDecisionDto).orElse(null))
                .build();
    }

    private com.unheard.dto.CoordinatorDecisionDto toDecisionDto(CoordinatorDecision d) {
        return com.unheard.dto.CoordinatorDecisionDto.builder()
                .id(d.getId())
                .recommendationId(d.getRecommendation().getId())
                .decision(d.getDecision())
                .reason(d.getReason())
                .assignedResourceId(d.getAssignedResourceId())
                .overriddenRank(d.getOverriddenRank())
                .decidedAt(d.getDecidedAt())
                .decidedBy(d.getDecidedBy())
                .originalScore(d.getOriginalScore())
                .originalRank(d.getOriginalRank())
                .build();
    }

    private AnalysisRunDto toRunDto(AnalysisRun r) {
        return AnalysisRunDto.builder()
                .id(r.getId())
                .runAt(r.getRunAt())
                .triggerSource(r.getTriggerSource())
                .villagesAnalyzed(r.getVillagesAnalyzed())
                .highRiskCount(r.getHighRiskCount())
                .silentZoneCount(r.getSilentZoneCount())
                .totalRecommendations(r.getTotalRecommendations())
                .notes(r.getNotes())
                .build();
    }

    /** Internal holder for a village + its scored components */
    private static class ScoredVillageEntry {
        final Village village;
        final ScoreComponents components;
        final String type;

        ScoredVillageEntry(Village village, ScoreComponents components, String type) {
            this.village = village;
            this.components = components;
            this.type = type;
        }
    }
}
