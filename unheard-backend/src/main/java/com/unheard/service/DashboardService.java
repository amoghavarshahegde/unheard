package com.unheard.service;

import com.unheard.dto.DashboardSummaryDto;
import com.unheard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final String DISCLAIMER =
            "PROTOTYPE - All data is simulated. Do not use for real emergency decisions.";

    private final VillageRepository villageRepository;
    private final IncidentReportRepository incidentReportRepository;
    private final RescueResourceRepository resourceRepository;
    private final AnalysisRunRepository analysisRunRepository;
    private final RecommendationRepository recommendationRepository;
    private final CoordinatorDecisionRepository decisionRepository;

    public DashboardService(VillageRepository villageRepository,
                            IncidentReportRepository incidentReportRepository,
                            RescueResourceRepository resourceRepository,
                            AnalysisRunRepository analysisRunRepository,
                            RecommendationRepository recommendationRepository,
                            CoordinatorDecisionRepository decisionRepository) {
        this.villageRepository = villageRepository;
        this.incidentReportRepository = incidentReportRepository;
        this.resourceRepository = resourceRepository;
        this.analysisRunRepository = analysisRunRepository;
        this.recommendationRepository = recommendationRepository;
        this.decisionRepository = decisionRepository;
    }

    public DashboardSummaryDto getSummary() {
        long totalVillages = villageRepository.count();
        long highRisk = villageRepository.findHighRisk(6.0).size();
        long silentZones = villageRepository.findSilentHighRisk(6.0).size();

        long totalActive = incidentReportRepository.findAll().stream()
                .filter(r -> r.getReportTimestamp().isAfter(LocalDateTime.now().minusHours(24)))
                .filter(r -> !r.getFlaggedDuplicate())
                .count();

        long availableTeams = resourceRepository.findByAvailabilityStatusAndResourceType("AVAILABLE", "RESCUE_TEAM").size();
        long availableBoats = resourceRepository.findByAvailabilityStatusAndResourceType("AVAILABLE", "BOAT").size();
        long totalResources = resourceRepository.count();

        var latestRun = analysisRunRepository.findTopByOrderByRunAtDesc();
        Long runId = latestRun.map(r -> r.getId()).orElse(null);
        LocalDateTime runAt = latestRun.map(r -> r.getRunAt()).orElse(null);
        int totalRecs = latestRun.map(r -> r.getTotalRecommendations() != null ? r.getTotalRecommendations() : 0).orElse(0);

        long pendingDecisions = runId != null
                ? recommendationRepository.findByAnalysisRunIdOrderByRankAsc(runId).stream()
                    .filter(rec -> decisionRepository.findByRecommendationId(rec.getId()).isEmpty())
                    .count()
                : 0L;

        return DashboardSummaryDto.builder()
                .totalVillages((int) totalVillages)
                .highRiskVillages((int) highRisk)
                .silentZoneVillages((int) silentZones)
                .activeIncidents((int) totalActive)
                .availableRescueTeams((int) availableTeams)
                .availableBoats((int) availableBoats)
                .totalResources((int) totalResources)
                .lastAnalysisRun(runAt)
                .lastAnalysisRunId(runId)
                .totalRecommendations(totalRecs)
                .pendingDecisions((int) pendingDecisions)
                .dataDisclaimer(DISCLAIMER)
                .build();
    }
}
