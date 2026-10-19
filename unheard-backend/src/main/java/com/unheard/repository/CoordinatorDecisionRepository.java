package com.unheard.repository;

import com.unheard.domain.CoordinatorDecision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoordinatorDecisionRepository extends JpaRepository<CoordinatorDecision, Long> {
    Optional<CoordinatorDecision> findByRecommendationId(Long recommendationId);
    Page<CoordinatorDecision> findAllByOrderByDecidedAtDesc(Pageable pageable);
}
