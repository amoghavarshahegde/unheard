package com.unheard.service;

import com.unheard.domain.Village;
import com.unheard.dto.VillageDto;
import com.unheard.repository.VillageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VillageService {

    private static final Logger log = LoggerFactory.getLogger(VillageService.class);

    private static final String DATA_DISCLAIMER =
            "SIMULATED DATA: All village data is fictional. " +
            "This platform is a hackathon prototype and must NOT be used for real emergency decisions.";

    private final VillageRepository villageRepository;

    public VillageService(VillageRepository villageRepository) {
        this.villageRepository = villageRepository;
    }

    public List<VillageDto> getAllVillages() {
        return getAllVillages(null);
    }

    public List<VillageDto> getAllVillages(String hazardType) {
        List<Village> list = (hazardType != null && !hazardType.isBlank())
                ? villageRepository.findByHazardType(hazardType)
                : villageRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public VillageDto getVillageById(Long id) {
        Village v = villageRepository.findById(id)
                .orElseThrow(() -> new com.unheard.exception.ResourceNotFoundException("Village not found with id: " + id));
        return toDto(v);
    }

    public List<VillageDto> getHighRiskVillages(double minRisk) {
        return villageRepository.findHighRisk(minRisk).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VillageDto> getSilentHighRiskVillages(double minRisk) {
        return villageRepository.findSilentHighRisk(minRisk).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VillageDto createVillage(Village village) {
        village.setSimulated(true);
        Village saved = villageRepository.save(village);
        log.info("Created simulated village: {}", saved.getName());
        return toDto(saved);
    }

    public VillageDto toDto(Village v) {
        String riskLevel = computeRiskLevel(v.getHazardRiskScore());
        return VillageDto.builder()
                .id(v.getId())
                .name(v.getName())
                .latitude(v.getLatitude())
                .longitude(v.getLongitude())
                .population(v.getPopulation())
                .vulnerablePopulation(v.getVulnerablePopulation())
                .hazardType(v.getHazardType())
                .hazardRiskScore(v.getHazardRiskScore())
                .expectedReportVolume(v.getExpectedReportVolume())
                .actualReportCount(v.getActualReportCount())
                .connectivityQuality(v.getConnectivityQuality())
                .lastFieldVerification(v.getLastFieldVerification())
                .nearbyResources(v.getNearbyResources())
                .district(v.getDistrict())
                .simulated(v.getSimulated())
                .riskLevel(riskLevel)
                .dataDisclaimer(DATA_DISCLAIMER)
                .build();
    }

    private String computeRiskLevel(double hazardRiskScore) {
        if (hazardRiskScore >= 8.5) return "CRITICAL";
        if (hazardRiskScore >= 7.0) return "HIGH";
        if (hazardRiskScore >= 5.0) return "MEDIUM";
        return "LOW";
    }

    public Village findById(Long id) {
        return villageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Village not found with id: " + id));
    }
}
