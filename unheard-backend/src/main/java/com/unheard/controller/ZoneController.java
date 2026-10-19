package com.unheard.controller;

import com.unheard.dto.VillageDto;
import com.unheard.service.VillageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@Tag(name = "Zones", description = "Monitored villages and zones")
public class ZoneController {

    private final VillageService villageService;

    public ZoneController(VillageService villageService) {
        this.villageService = villageService;
    }

    @GetMapping
    @Operation(summary = "Get all monitored villages/zones, optionally filtered by hazard type")
    public ResponseEntity<List<VillageDto>> getAllZones(@RequestParam(required = false) String hazardType) {
        return ResponseEntity.ok(villageService.getAllVillages(hazardType));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single village/zone by ID")
    public ResponseEntity<VillageDto> getZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(villageService.getVillageById(id));
    }
}
