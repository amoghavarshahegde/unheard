package com.unheard.repository;

import com.unheard.domain.AnalysisRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRunRepository extends JpaRepository<AnalysisRun, Long> {
    Optional<AnalysisRun> findTopByOrderByRunAtDesc();
}
