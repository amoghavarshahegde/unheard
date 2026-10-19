package com.unheard.controller;

import com.unheard.dto.RescueResourceDto;
import com.unheard.service.RescueResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Emergency response teams and vehicle assets")
public class ResourceController {

    private final RescueResourceService resourceService;

    public ResourceController(RescueResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    @Operation(summary = "Get all rescue teams and equipment resources")
    public ResponseEntity<List<RescueResourceDto>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/nearby")
    @Operation(summary = "Get available resources ordered by straight-line Haversine distance")
    public ResponseEntity<List<RescueResourceDto>> getNearbyResources(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(resourceService.getNearestResources(lat, lon, type));
    }
}
