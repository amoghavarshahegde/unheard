package com.unheard.repository;

import com.unheard.domain.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    List<Village> findByHazardType(String hazardType);

    @Query("SELECT v FROM Village v WHERE v.hazardRiskScore >= :minRisk ORDER BY v.hazardRiskScore DESC")
    List<Village> findHighRisk(double minRisk);

    @Query("SELECT v FROM Village v WHERE v.actualReportCount = 0 AND v.hazardRiskScore >= :minRisk")
    List<Village> findSilentHighRisk(double minRisk);
}
