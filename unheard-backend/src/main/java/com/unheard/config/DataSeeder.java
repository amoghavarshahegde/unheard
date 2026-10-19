package com.unheard.config;

import com.unheard.domain.*;
import com.unheard.repository.*;
import com.unheard.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds fictional village, resource, and incident data on first startup.
 *
 * ALL DATA IS SIMULATED. This is a hackathon MVP using fictional scenarios.
 * No real villages, emergencies, or people are represented.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final VillageRepository villageRepo;
    private final RescueResourceRepository resourceRepo;
    private final IncidentReportRepository incidentRepo;
    private final AnalysisService analysisService;

    public DataSeeder(VillageRepository villageRepo, RescueResourceRepository resourceRepo,
                      IncidentReportRepository incidentRepo, AnalysisService analysisService) {
        this.villageRepo = villageRepo;
        this.resourceRepo = resourceRepo;
        this.incidentRepo = incidentRepo;
        this.analysisService = analysisService;
    }

    @Override
    public void run(String... args) {
        if (villageRepo.count() > 0) {
            log.info("Seed data already present — skipping seeder.");
            return;
        }

        log.info("=== SEEDING SIMULATED DATA (fictional, not real emergencies) ===");
        seedVillages();
        seedResources();
        seedIncidents();

        try {
            analysisService.runAnalysis("SEED");
            log.info("Initial analysis run complete.");
        } catch (Exception e) {
            log.warn("Initial analysis run failed (non-fatal): {}", e.getMessage());
        }
        log.info("=== Seed data complete ===");
    }

    private void seedVillages() {
        LocalDateTime nineHoursAgo = LocalDateTime.now().minusHours(9);
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        List<Village> villages = List.of(
            // --- SILENT HIGH-RISK ZONES (UNHEARD core showcase) ---
            Village.builder().name("Kalyanpur").latitude(25.5941).longitude(85.1376)
                .population(3200).vulnerablePopulation(1100)
                .hazardType("FLOOD").hazardRiskScore(8.7)
                .expectedReportVolume(12).actualReportCount(0)
                .connectivityQuality("NO_SIGNAL").lastFieldVerification(threeDaysAgo)
                .nearbyResources("Nearest rescue team 45km away").district("Patna")
                .simulated(true).build(),

            Village.builder().name("Dhanauli Khas").latitude(25.6200).longitude(85.0900)
                .population(2100).vulnerablePopulation(890)
                .hazardType("FLOOD").hazardRiskScore(8.2)
                .expectedReportVolume(8).actualReportCount(1)
                .connectivityQuality("LOW").lastFieldVerification(twoDaysAgo)
                .nearbyResources("One boat stationed 30km away").district("Patna")
                .simulated(true).build(),

            Village.builder().name("Majhaulia Tola").latitude(25.7100).longitude(84.9500)
                .population(4500).vulnerablePopulation(1800)
                .hazardType("FLOOD").hazardRiskScore(9.1)
                .expectedReportVolume(0).actualReportCount(0)  // NO BASELINE
                .connectivityQuality("NO_SIGNAL").lastFieldVerification(null)  // NEVER VERIFIED
                .nearbyResources("No resources mapped").district("Saran")
                .simulated(true).build(),

            Village.builder().name("Gopalpur Diara").latitude(25.5500).longitude(85.2200)
                .population(1850).vulnerablePopulation(650)
                .hazardType("CYCLONE").hazardRiskScore(7.9)
                .expectedReportVolume(6).actualReportCount(0)
                .connectivityQuality("LOW").lastFieldVerification(twoDaysAgo)
                .nearbyResources("Medical unit at Patna city, 60km").district("Vaishali")
                .simulated(true).build(),

            Village.builder().name("Bhagwatipur").latitude(25.4800).longitude(85.3100)
                .population(2700).vulnerablePopulation(950)
                .hazardType("EARTHQUAKE").hazardRiskScore(7.5)
                .expectedReportVolume(5).actualReportCount(1)
                .connectivityQuality("LOW").lastFieldVerification(nineHoursAgo)
                .nearbyResources("Rescue team at district HQ, 40km").district("Nalanda")
                .simulated(true).build(),

            // --- MODERATE RISK, MODERATE REPORTING ---
            Village.builder().name("Rampur Haat").latitude(25.6800).longitude(85.4200)
                .population(5200).vulnerablePopulation(1400)
                .hazardType("FLOOD").hazardRiskScore(6.3)
                .expectedReportVolume(15).actualReportCount(8)
                .connectivityQuality("MEDIUM").lastFieldVerification(oneHourAgo)
                .nearbyResources("Two rescue teams nearby").district("Muzaffarpur")
                .simulated(true).build(),

            Village.builder().name("Samastipur East").latitude(25.8600).longitude(85.7800)
                .population(7800).vulnerablePopulation(2100)
                .hazardType("FLOOD").hazardRiskScore(6.8)
                .expectedReportVolume(20).actualReportCount(14)
                .connectivityQuality("HIGH").lastFieldVerification(oneHourAgo)
                .nearbyResources("District emergency center 5km").district("Samastipur")
                .simulated(true).build(),

            Village.builder().name("Darbhanga North").latitude(26.1542).longitude(85.8918)
                .population(9200).vulnerablePopulation(2800)
                .hazardType("FLOOD").hazardRiskScore(7.1)
                .expectedReportVolume(25).actualReportCount(18)
                .connectivityQuality("HIGH").lastFieldVerification(oneHourAgo)
                .nearbyResources("Full emergency infrastructure").district("Darbhanga")
                .simulated(true).build(),

            // --- LOW RISK, WELL-CONNECTED ---
            Village.builder().name("Hajipur Central").latitude(25.6876).longitude(85.2095)
                .population(12000).vulnerablePopulation(2200)
                .hazardType("FLOOD").hazardRiskScore(4.2)
                .expectedReportVolume(30).actualReportCount(28)
                .connectivityQuality("HIGH").lastFieldVerification(oneHourAgo)
                .nearbyResources("Multiple teams and medical units").district("Vaishali")
                .simulated(true).build(),

            Village.builder().name("Muzaffarpur City").latitude(26.1197).longitude(85.3910)
                .population(18000).vulnerablePopulation(3500)
                .hazardType("FLOOD").hazardRiskScore(3.8)
                .expectedReportVolume(45).actualReportCount(42)
                .connectivityQuality("HIGH").lastFieldVerification(oneHourAgo)
                .nearbyResources("City emergency ops fully staffed").district("Muzaffarpur")
                .simulated(true).build(),

            // --- CYCLONE / EARTHQUAKE ZONES ---
            Village.builder().name("Sitamarhi Outer").latitude(26.5902).longitude(85.4852)
                .population(3600).vulnerablePopulation(1200)
                .hazardType("CYCLONE").hazardRiskScore(8.0)
                .expectedReportVolume(10).actualReportCount(2)
                .connectivityQuality("LOW").lastFieldVerification(twoDaysAgo)
                .nearbyResources("State emergency team, 50km").district("Sitamarhi")
                .simulated(true).build(),

            Village.builder().name("Madhubani Rural").latitude(26.3530).longitude(86.0722)
                .population(2900).vulnerablePopulation(1050)
                .hazardType("CYCLONE").hazardRiskScore(6.5)
                .expectedReportVolume(8).actualReportCount(5)
                .connectivityQuality("MEDIUM").lastFieldVerification(nineHoursAgo)
                .nearbyResources("One boat, medical unit 25km").district("Madhubani")
                .simulated(true).build(),

            Village.builder().name("Supaul Kosi Bank").latitude(26.1200).longitude(86.6000)
                .population(5100).vulnerablePopulation(1900)
                .hazardType("FLOOD").hazardRiskScore(9.3)
                .expectedReportVolume(15).actualReportCount(3)
                .connectivityQuality("LOW").lastFieldVerification(twoDaysAgo)
                .nearbyResources("Kosi flood relief team, 35km").district("Supaul")
                .simulated(true).build(),

            Village.builder().name("Araria West").latitude(26.1478).longitude(87.4297)
                .population(4200).vulnerablePopulation(1600)
                .hazardType("EARTHQUAKE").hazardRiskScore(8.5)
                .expectedReportVolume(0).actualReportCount(0)  // NO BASELINE
                .connectivityQuality("NO_SIGNAL").lastFieldVerification(null)  // NEVER VERIFIED
                .nearbyResources("No resources mapped in this zone").district("Araria")
                .simulated(true).build(),

            Village.builder().name("Kishanganj Border").latitude(26.1050).longitude(87.9400)
                .population(6200).vulnerablePopulation(2400)
                .hazardType("EARTHQUAKE").hazardRiskScore(7.8)
                .expectedReportVolume(12).actualReportCount(1)
                .connectivityQuality("LOW").lastFieldVerification(threeDaysAgo)
                .nearbyResources("Border patrol unit, non-rescue").district("Kishanganj")
                .simulated(true).build(),

            Village.builder().name("Purnia East Block").latitude(25.7771).longitude(87.4754)
                .population(8500).vulnerablePopulation(2800)
                .hazardType("FLOOD").hazardRiskScore(5.5)
                .expectedReportVolume(20).actualReportCount(16)
                .connectivityQuality("MEDIUM").lastFieldVerification(nineHoursAgo)
                .nearbyResources("District flood control room").district("Purnia")
                .simulated(true).build(),

            Village.builder().name("Katihar Ganga Side").latitude(25.5356).longitude(87.5731)
                .population(4700).vulnerablePopulation(1700)
                .hazardType("FLOOD").hazardRiskScore(6.9)
                .expectedReportVolume(14).actualReportCount(9)
                .connectivityQuality("MEDIUM").lastFieldVerification(nineHoursAgo)
                .nearbyResources("River patrol and one rescue boat").district("Katihar")
                .simulated(true).build()
        );

        villageRepo.saveAll(villages);
        log.info("Seeded {} villages (all simulated)", villages.size());
    }

    private void seedResources() {
        List<RescueResource> resources = List.of(
            RescueResource.builder().resourceType("RESCUE_TEAM").name("Team Alpha")
                .latitude(25.5941).longitude(85.1376).availabilityStatus("AVAILABLE")
                .capacity(20).currentAssignment(null).notes("NDRF unit, 20 personnel").simulated(true).build(),
            RescueResource.builder().resourceType("RESCUE_TEAM").name("Team Bravo")
                .latitude(25.8600).longitude(85.7800).availabilityStatus("DEPLOYED")
                .capacity(15).currentAssignment("Samastipur East").notes("State team, deployed").simulated(true).build(),
            RescueResource.builder().resourceType("RESCUE_TEAM").name("Team Charlie")
                .latitude(26.1197).longitude(85.3910).availabilityStatus("AVAILABLE")
                .capacity(25).currentAssignment(null).notes("District rapid response").simulated(true).build(),
            RescueResource.builder().resourceType("RESCUE_TEAM").name("Team Delta")
                .latitude(26.3530).longitude(86.0722).availabilityStatus("AVAILABLE")
                .capacity(12).currentAssignment(null).notes("Volunteer rescue team").simulated(true).build(),
            RescueResource.builder().resourceType("BOAT").name("Boat-01")
                .latitude(25.6200).longitude(85.0900).availabilityStatus("AVAILABLE")
                .capacity(30).currentAssignment(null).notes("Motor boat, 30 capacity").simulated(true).build(),
            RescueResource.builder().resourceType("BOAT").name("Boat-02")
                .latitude(25.7100).longitude(84.9500).availabilityStatus("AVAILABLE")
                .capacity(25).currentAssignment(null).notes("Inflatable rescue raft").simulated(true).build(),
            RescueResource.builder().resourceType("BOAT").name("Boat-03")
                .latitude(26.1200).longitude(86.6000).availabilityStatus("DEPLOYED")
                .capacity(20).currentAssignment("Supaul Kosi Bank").notes("Kosi river patrol boat").simulated(true).build(),
            RescueResource.builder().resourceType("MEDICAL_UNIT").name("Med Unit 1")
                .latitude(25.6876).longitude(85.2095).availabilityStatus("AVAILABLE")
                .capacity(50).currentAssignment(null).notes("Mobile hospital, 50-bed").simulated(true).build(),
            RescueResource.builder().resourceType("MEDICAL_UNIT").name("Med Unit 2")
                .latitude(26.5902).longitude(85.4852).availabilityStatus("AVAILABLE")
                .capacity(30).currentAssignment(null).notes("Field medical team").simulated(true).build(),
            RescueResource.builder().resourceType("SUPPLY_TRUCK").name("Supply-01")
                .latitude(26.1542).longitude(85.8918).availabilityStatus("AVAILABLE")
                .capacity(100).currentAssignment(null).notes("Food & water supplies").simulated(true).build()
        );
        resourceRepo.saveAll(resources);
        log.info("Seeded {} rescue resources (all simulated)", resources.size());
    }

    private void seedIncidents() {
        List<Village> villages = villageRepo.findAll();
        Village samastipur = villages.stream().filter(v -> v.getName().contains("Samastipur")).findFirst().orElse(null);
        Village darbhanga = villages.stream().filter(v -> v.getName().contains("Darbhanga")).findFirst().orElse(null);
        Village supaul = villages.stream().filter(v -> v.getName().contains("Supaul")).findFirst().orElse(null);
        Village sitamarhi = villages.stream().filter(v -> v.getName().contains("Sitamarhi")).findFirst().orElse(null);
        Village madhubani = villages.stream().filter(v -> v.getName().contains("Madhubani")).findFirst().orElse(null);
        Village rampur = villages.stream().filter(v -> v.getName().contains("Rampur")).findFirst().orElse(null);

        List<IncidentReport> incidents = new ArrayList<>();

        if (samastipur != null) {
            incidents.add(IncidentReport.builder().village(samastipur)
                .incidentType("FLOOD_INUNDATION").severity(4).description("Water level rising, 3 wards submerged. Residents stranded on rooftops.")
                .peopleAffected(1200).reportTimestamp(LocalDateTime.now().minusHours(2))
                .sourceType("FIELD_OFFICER").verificationStatus("VERIFIED").flaggedDuplicate(false).build());
            incidents.add(IncidentReport.builder().village(samastipur)
                .incidentType("FLOOD_INUNDATION").severity(4).description("Further reports of water rising.")
                .peopleAffected(900).reportTimestamp(LocalDateTime.now().minusHours(1))
                .sourceType("COMMUNITY_MEMBER").verificationStatus("PENDING").flaggedDuplicate(true).originalReportId(1L).build());
        }
        if (darbhanga != null) {
            incidents.add(IncidentReport.builder().village(darbhanga)
                .incidentType("INFRASTRUCTURE_DAMAGE").severity(3)
                .description("Road breach, highway NH-57 blocked. Supply routes cut.")
                .peopleAffected(5000).reportTimestamp(LocalDateTime.now().minusHours(5))
                .sourceType("FIELD_OFFICER").verificationStatus("VERIFIED").flaggedDuplicate(false).build());
        }
        if (supaul != null) {
            incidents.add(IncidentReport.builder().village(supaul)
                .incidentType("FLOOD_INUNDATION").severity(5)
                .description("Kosi river breach. Entire lower ward flooded. Evacuation needed urgently.")
                .peopleAffected(3200).reportTimestamp(LocalDateTime.now().minusHours(3))
                .sourceType("SATELLITE").verificationStatus("PENDING").flaggedDuplicate(false).build());
        }
        if (sitamarhi != null) {
            incidents.add(IncidentReport.builder().village(sitamarhi)
                .incidentType("STRONG_WINDS").severity(4)
                .description("Cyclone wind damage, 120+ homes destroyed. Medical assistance needed.")
                .peopleAffected(800).reportTimestamp(LocalDateTime.now().minusHours(6))
                .sourceType("SOCIAL_MEDIA").verificationStatus("PENDING").flaggedDuplicate(false).build());
        }
        if (madhubani != null) {
            incidents.add(IncidentReport.builder().village(madhubani)
                .incidentType("DISPLACEMENT").severity(3)
                .description("600 families displaced from low-lying areas due to cyclone threat.")
                .peopleAffected(2400).reportTimestamp(LocalDateTime.now().minusHours(4))
                .sourceType("FIELD_OFFICER").verificationStatus("VERIFIED").flaggedDuplicate(false).build());
        }
        if (rampur != null) {
            incidents.add(IncidentReport.builder().village(rampur)
                .incidentType("FLOOD_INUNDATION").severity(3)
                .description("Flood water entering agricultural land. Livestock at risk.")
                .peopleAffected(400).reportTimestamp(LocalDateTime.now().minusHours(8))
                .sourceType("COMMUNITY_MEMBER").verificationStatus("PENDING").flaggedDuplicate(false).build());
        }

        if (!incidents.isEmpty()) {
            incidentRepo.saveAll(incidents);
            log.info("Seeded {} incidents (all simulated)", incidents.size());
        }
    }
}
