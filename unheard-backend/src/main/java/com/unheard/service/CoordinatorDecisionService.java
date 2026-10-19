package com.unheard.service;

import com.unheard.domain.CoordinatorDecision;
import com.unheard.domain.Recommendation;
import com.unheard.dto.CoordinatorDecisionDto;
import com.unheard.dto.CoordinatorDecisionRequest;
import com.unheard.dto.PagedResponse;
import com.unheard.repository.CoordinatorDecisionRepository;
import com.unheard.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CoordinatorDecisionService {

    private static final Logger log = LoggerFactory.getLogger(CoordinatorDecisionService.class);

    private final CoordinatorDecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;

    public CoordinatorDecisionService(CoordinatorDecisionRepository decisionRepository,
                                      RecommendationRepository recommendationRepository) {
        this.decisionRepository = decisionRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Transactional
    public CoordinatorDecisionDto recordDecision(Long recommendationId, CoordinatorDecisionRequest request) {
        Recommendation rec = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + recommendationId));

        // Check for existing decision - update if present
        CoordinatorDecision decision = decisionRepository.findByRecommendationId(recommendationId)
                .orElse(CoordinatorDecision.builder()
                        .recommendation(rec)
                        .originalScore(rec.getScore())
                        .originalRank(rec.getRank())
                        .build());

        decision.setDecision(request.getDecision().toUpperCase());
        decision.setReason(request.getReason());
        decision.setAssignedResourceId(request.getAssignedResourceId());
        decision.setOverriddenRank(request.getOverriddenRank());
        decision.setDecidedAt(LocalDateTime.now());
        decision.setDecidedBy(request.getDecidedBy() != null ? request.getDecidedBy() : "COORDINATOR");

        CoordinatorDecision saved = decisionRepository.save(decision);
        log.info("Decision recorded: rec={} decision={} by={}", recommendationId,
                decision.getDecision(), decision.getDecidedBy());
        return toDto(saved);
    }

    public PagedResponse<CoordinatorDecisionDto> getAuditLog(int page, int size) {
        Page<CoordinatorDecision> result = decisionRepository.findAllByOrderByDecidedAtDesc(
                PageRequest.of(page, size));
        return PagedResponse.<CoordinatorDecisionDto>builder()
                .content(result.getContent().stream().map(this::toDto).collect(Collectors.toList()))
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    public CoordinatorDecisionDto getDecisionForRecommendation(Long recommendationId) {
        return decisionRepository.findByRecommendationId(recommendationId)
                .map(this::toDto)
                .orElse(null);
    }

    private CoordinatorDecisionDto toDto(CoordinatorDecision d) {
        return CoordinatorDecisionDto.builder()
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
}
