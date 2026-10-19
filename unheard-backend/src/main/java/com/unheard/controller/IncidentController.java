package com.unheard.controller;

import com.unheard.dto.IncidentReportDto;
import com.unheard.dto.IncidentReportRequest;
import com.unheard.dto.PagedResponse;
import com.unheard.service.IncidentReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Disaster incident reporting and duplicate checking")
public class IncidentController {

    private final IncidentReportService incidentReportService;

    public IncidentController(IncidentReportService incidentReportService) {
        this.incidentReportService = incidentReportService;
    }

    @GetMapping
    @Operation(summary = "Get paginated incident reports")
    public ResponseEntity<PagedResponse<IncidentReportDto>> getIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentReportService.getAllReports(page, size));
    }

    @PostMapping
    @Operation(summary = "Submit a new incident report")
    public ResponseEntity<IncidentReportDto> submitIncident(
            @Valid @RequestBody IncidentReportRequest request) {
        IncidentReportDto created = incidentReportService.submitReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
