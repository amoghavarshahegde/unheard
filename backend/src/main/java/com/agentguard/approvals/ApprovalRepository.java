package com.agentguard.approvals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalRecord, String> {
    List<ApprovalRecord> findAllByOrderByCreatedAtDesc();
    List<ApprovalRecord> findByStatusOrderByCreatedAtDesc(String status);
}
