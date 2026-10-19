package com.unheard.service;

import com.unheard.domain.RescueResource;
import com.unheard.dto.RescueResourceDto;
import com.unheard.repository.RescueResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RescueResourceService {

    private static final String DISTANCE_DISCLAIMER =
            "Distance is straight-line (Haversine) estimate only. Actual travel time depends on " +
            "road conditions, terrain, and flood extent. All data is simulated.";

    private final RescueResourceRepository resourceRepository;

    public RescueResourceService(RescueResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<RescueResourceDto> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(r -> toDto(r, null, null))
                .collect(Collectors.toList());
    }

    public List<RescueResourceDto> getAvailableResources() {
        return resourceRepository.findByAvailabilityStatus("AVAILABLE").stream()
                .map(r -> toDto(r, null, null))
                .collect(Collectors.toList());
    }

    public List<RescueResourceDto> getNearestResources(double lat, double lon, String type) {
        List<RescueResource> resources = type != null
                ? resourceRepository.findByAvailabilityStatusAndResourceType("AVAILABLE", type)
                : resourceRepository.findByAvailabilityStatus("AVAILABLE");

        return resources.stream()
                .map(r -> toDto(r, lat, lon))
                .sorted((a, b) -> {
                    if (a.getDistanceKmEstimate() == null) return 1;
                    if (b.getDistanceKmEstimate() == null) return -1;
                    return Double.compare(a.getDistanceKmEstimate(), b.getDistanceKmEstimate());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public RescueResourceDto updateStatus(Long id, String status, String assignment) {
        RescueResource r = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + id));
        r.setAvailabilityStatus(status);
        r.setCurrentAssignment(assignment);
        return toDto(resourceRepository.save(r), null, null);
    }

    /**
     * Haversine formula for straight-line distance in km.
     * DISCLAIMER: Not actual travel distance or time.
     */
    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public RescueResourceDto toDto(RescueResource r, Double refLat, Double refLon) {
        Double distKm = null;
        if (refLat != null && refLon != null) {
            distKm = Math.round(haversineKm(refLat, refLon, r.getLatitude(), r.getLongitude()) * 10.0) / 10.0;
        }
        return RescueResourceDto.builder()
                .id(r.getId())
                .resourceType(r.getResourceType())
                .name(r.getName())
                .latitude(r.getLatitude())
                .longitude(r.getLongitude())
                .availabilityStatus(r.getAvailabilityStatus())
                .capacity(r.getCapacity())
                .currentAssignment(r.getCurrentAssignment())
                .notes(r.getNotes())
                .simulated(r.getSimulated())
                .distanceKmEstimate(distKm)
                .distanceDisclaimer(distKm != null ? DISTANCE_DISCLAIMER : null)
                .build();
    }
}
