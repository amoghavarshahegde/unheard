package com.unheard.controller;

import com.unheard.dto.BaselineComparisonDto;
import com.unheard.service.BaselineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/baseline")
@Tag(name = "Baseline", description = "Comparison between naive report-volume baseline and UNHEARD scoring")
public class BaselineController {

    private final BaselineService baselineService;

    public BaselineController(BaselineService baselineService) {
        this.baselineService = baselineService;
    }

    @GetMapping("/compare")
    @Operation(summary = "Compare baseline report-volume ranking against UNHEARD verification ranking")
    public ResponseEntity<BaselineComparisonDto> compare() {
        return ResponseEntity.ok(baselineService.compare());
    }
}
