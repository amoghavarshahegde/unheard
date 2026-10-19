package com.unheard.controller;

import com.unheard.dto.AnalysisRunDto;
import com.unheard.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@Tag(name = "Analysis", description = "Decision engine analysis runs")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/run")
    @Operation(summary = "Trigger a new analysis run of the UNHEARD scoring engine")
    public ResponseEntity<AnalysisRunDto> runAnalysis() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(analysisService.runAnalysis("MANUAL"));
    }

    @GetMapping("/results")
    @Operation(summary = "Get recent analysis runs")
    public ResponseEntity<List<AnalysisRunDto>> getResults() {
        return ResponseEntity.ok(analysisService.getRecentRuns());
    }
}
