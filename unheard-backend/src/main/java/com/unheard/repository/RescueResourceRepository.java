package com.unheard.repository;

import com.unheard.domain.RescueResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RescueResourceRepository extends JpaRepository<RescueResource, Long> {
    List<RescueResource> findByAvailabilityStatus(String status);
    List<RescueResource> findByResourceType(String type);
    List<RescueResource> findByAvailabilityStatusAndResourceType(String status, String type);
}
