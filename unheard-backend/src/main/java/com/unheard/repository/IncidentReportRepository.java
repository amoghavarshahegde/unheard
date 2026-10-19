package com.unheard.repository;

import com.unheard.domain.IncidentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentReportRepository extends JpaRepository<IncidentReport, Long> {
    Page<IncidentReport> findAll(Pageable pageable);

    List<IncidentReport> findByVillageId(Long villageId);

    @Query("SELECT r FROM IncidentReport r WHERE r.village.id = :villageId " +
           "AND r.incidentType = :incidentType " +
           "AND r.reportTimestamp >= :since " +
           "AND r.flaggedDuplicate = false")
    List<IncidentReport> findSimilarReports(Long villageId, String incidentType, LocalDateTime since);

    long countByVillageIdAndReportTimestampAfter(Long villageId, LocalDateTime after);
}
