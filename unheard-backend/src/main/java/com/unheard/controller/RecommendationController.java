package com.unheard.controller;

import com.unheard.dto.CoordinatorDecisionDto;
import com.unheard.dto.CoordinatorDecisionRequest;
import com.unheard.dto.RecommendationDto;
import com.unheard.service.AnalysisService;
import com.unheard.service.CoordinatorDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Scoring engine recommendations and coordinator decisions")
public class RecommendationController {

    private final AnalysisService analysisService;
    private final CoordinatorDecisionService decisionService;

    public RecommendationController(AnalysisService analysisService, CoordinatorDecisionService decisionService) {
        this.analysisService = analysisService;
        this.decisionService = decisionService;
    }

    @GetMapping
    @Operation(summary = "Get recommendations from the latest analysis run")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(analysisService.getLatestRecommendations(type));
    }

    @PostMapping("/{id}/decision")
    @Operation(summary = "Record a coordinator decision (approve, reject, or override) for a recommendation")
    public ResponseEntity<CoordinatorDecisionDto> recordDecision(
            @PathVariable Long id,
            @Valid @RequestBody CoordinatorDecisionRequest request) {
        return ResponseEntity.ok(decisionService.recordDecision(id, request));
    }
}
