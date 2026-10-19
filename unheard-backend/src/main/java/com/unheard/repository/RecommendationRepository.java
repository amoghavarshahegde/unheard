package com.unheard.repository;

import com.unheard.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByAnalysisRunIdOrderByRankAsc(Long runId);
    List<Recommendation> findByAnalysisRunIdAndRecommendationTypeOrderByRankAsc(Long runId, String type);
    List<Recommendation> findTop20ByOrderByAnalysisRunRunAtDescScoreDesc();
}
