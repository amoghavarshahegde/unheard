package com.agentguard.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, String> {

    @Query("SELECT a FROM AuditRecord a ORDER BY a.sequenceNumber DESC LIMIT 1")
    Optional<AuditRecord> findLatest();

    List<AuditRecord> findAllByOrderBySequenceNumberAsc();

    List<AuditRecord> findAllByOrderBySequenceNumberDesc();
}
