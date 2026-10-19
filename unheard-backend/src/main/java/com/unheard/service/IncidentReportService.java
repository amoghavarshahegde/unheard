package com.unheard.service;

import com.unheard.domain.IncidentReport;
import com.unheard.domain.Village;
import com.unheard.dto.IncidentReportDto;
import com.unheard.dto.IncidentReportRequest;
import com.unheard.dto.PagedResponse;
import com.unheard.repository.IncidentReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IncidentReportService {

    private static final Logger log = LoggerFactory.getLogger(IncidentReportService.class);

    private static final String DATA_DISCLAIMER =
            "SIMULATED DATA: All incident reports are fictional. " +
            "This is a hackathon prototype only.";

    private final IncidentReportRepository reportRepository;
    private final VillageService villageService;

    public IncidentReportService(IncidentReportRepository reportRepository, VillageService villageService) {
        this.reportRepository = reportRepository;
        this.villageService = villageService;
    }

    @Transactional
    public IncidentReportDto submitReport(IncidentReportRequest request) {
        Village village = villageService.findById(request.getVillageId());

        // Duplicate detection: look for same incidentType in same village in last 2 hours
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
        List<IncidentReport> similar = reportRepository.findSimilarReports(
                village.getId(), request.getIncidentType(), twoHoursAgo);

        boolean isDuplicate = !similar.isEmpty();
        Long originalId = isDuplicate ? similar.get(0).getId() : null;

        if (isDuplicate) {
            log.info("Duplicate suspected for village {} incidentType {}", village.getName(), request.getIncidentType());
        }

        IncidentReport report = IncidentReport.builder()
                .village(village)
                .incidentType(request.getIncidentType())
                .severity(request.getSeverity())
                .description(request.getDescription())
                .peopleAffected(request.getPeopleAffected())
                .reportTimestamp(LocalDateTime.now())
                .sourceType(request.getSourceType() != null ? request.getSourceType() : "COMMUNITY_MEMBER")
                .verificationStatus("PENDING")
                .flaggedDuplicate(isDuplicate)
                .originalReportId(originalId)
                .build();

        IncidentReport saved = reportRepository.save(report);
        log.info("Saved incident report id={} for village={}", saved.getId(), village.getName());
        return toDto(saved);
    }

    public PagedResponse<IncidentReportDto> getAllReports(int page, int size) {
        Page<IncidentReport> result = reportRepository.findAll(
                PageRequest.of(page, size, Sort.by("reportTimestamp").descending()));
        return PagedResponse.<IncidentReportDto>builder()
                .content(result.getContent().stream().map(this::toDto).collect(Collectors.toList()))
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    public List<IncidentReportDto> getReportsByVillage(Long villageId) {
        return reportRepository.findByVillageId(villageId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public IncidentReportDto updateVerificationStatus(Long reportId, String status) {
        IncidentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + reportId));
        report.setVerificationStatus(status);
        return toDto(reportRepository.save(report));
    }

    public IncidentReportDto toDto(IncidentReport r) {
        return IncidentReportDto.builder()
                .id(r.getId())
                .villageId(r.getVillage().getId())
                .villageName(r.getVillage().getName())
                .incidentType(r.getIncidentType())
                .severity(r.getSeverity())
                .description(r.getDescription())
                .peopleAffected(r.getPeopleAffected())
                .reportTimestamp(r.getReportTimestamp())
                .sourceType(r.getSourceType())
                .verificationStatus(r.getVerificationStatus())
                .flaggedDuplicate(r.getFlaggedDuplicate())
                .originalReportId(r.getOriginalReportId())
                .dataDisclaimer(DATA_DISCLAIMER)
                .build();
    }
}
