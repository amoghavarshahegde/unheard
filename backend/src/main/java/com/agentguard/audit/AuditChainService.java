package com.agentguard.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AuditChainService implements AuditService {

    public static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    private final AuditRecordRepository repository;
    private final com.agentguard.audit.kafka.AuditEventPublisher eventPublisher;
    private final ReentrantLock chainLock = new ReentrantLock(true);

    public AuditChainService(AuditRecordRepository repository,
                             com.agentguard.audit.kafka.AuditEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Appends an audit record to the hash chain under a strict reentrant lock.
     */
    public AuditRecord appendRecord(String id, String agent, String tool, String verdict,
                                   int score, String redactedPayload, String reasons) {
        chainLock.lock();
        try {
            Optional<AuditRecord> latestOpt = repository.findLatest();
            long nextSeq = latestOpt.map(r -> r.getSequenceNumber() + 1).orElse(1L);
            String prevHash = latestOpt.map(AuditRecord::getHash).orElse(GENESIS_HASH);

            // Fixed ISO-8601 UTC timestamp truncated to microseconds
            String timestamp = Instant.now().truncatedTo(ChronoUnit.MICROS).toString();

            String canonical = buildCanonicalString(prevHash, id, agent, tool, verdict, score, timestamp);
            String currentHash = computeSha256(canonical);

            AuditRecord record = new AuditRecord(
                    id, nextSeq, prevHash, currentHash,
                    agent, tool, verdict, score,
                    timestamp, redactedPayload, reasons
            );

            AuditRecord saved = repository.save(record);
            eventPublisher.publish(saved);
            return saved;
        } finally {
            chainLock.unlock();
        }
    }

    public List<AuditRecord> getAllRecords() {
        return repository.findAllByOrderBySequenceNumberAsc();
    }

    public List<AuditRecord> getRecentRecords() {
        return repository.findAllByOrderBySequenceNumberDesc();
    }

    /**
     * Verifies the cryptographic integrity of the entire audit chain.
     * Detects edited fields, missing/deleted rows, and swapped order.
     */
    @Transactional(readOnly = true)
    public VerificationResult verifyChain() {
        List<AuditRecord> chain = repository.findAllByOrderBySequenceNumberAsc();
        if (chain.isEmpty()) {
            return new VerificationResult(true, null, "Audit chain is empty", 0);
        }

        String expectedPrevHash = GENESIS_HASH;
        long expectedSeq = 1L;

        for (int i = 0; i < chain.size(); i++) {
            AuditRecord record = chain.get(i);

            // 1. Check sequence ordering (detects missing or swapped rows)
            if (record.getSequenceNumber() == null || record.getSequenceNumber() != expectedSeq) {
                return new VerificationResult(
                        false,
                        record.getId(),
                        "Sequence broken at row " + record.getId() + ": expected seq " + expectedSeq + " but found " + record.getSequenceNumber(),
                        i
                );
            }

            // 2. Check previous hash link (detects deleted or swapped row)
            if (!expectedPrevHash.equalsIgnoreCase(record.getPrevHash())) {
                return new VerificationResult(
                        false,
                        record.getId(),
                        "Broken hash link at row " + record.getId() + ": expected prevHash " + expectedPrevHash + " but found " + record.getPrevHash(),
                        i
                );
            }

            // 3. Recompute hash (detects any modified / edited fields)
            String canonical = buildCanonicalString(
                    record.getPrevHash(),
                    record.getId(),
                    record.getAgent(),
                    record.getTool(),
                    record.getVerdict(),
                    record.getScore(),
                    record.getTimestamp()
            );
            String expectedCurrentHash = computeSha256(canonical);

            if (!expectedCurrentHash.equalsIgnoreCase(record.getHash())) {
                return new VerificationResult(
                        false,
                        record.getId(),
                        "Tampered payload detected at row " + record.getId() + ": calculated hash " + expectedCurrentHash + " does not match stored " + record.getHash(),
                        i
                );
            }

            expectedPrevHash = record.getHash();
            expectedSeq++;
        }

        return new VerificationResult(true, null, "Audit hash chain verified successfully", chain.size());
    }

    public static String buildCanonicalString(String prevHash, String id, String agent, String tool,
                                              String verdict, int score, String timestamp) {
        return prevHash + "|" + id + "|" + agent + "|" + tool + "|" + verdict + "|" + score + "|" + timestamp;
    }

    public static String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    public record VerificationResult(boolean valid, String brokenRowId, String reason, int checkedCount) {}
}
