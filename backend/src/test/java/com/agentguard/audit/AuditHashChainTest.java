package com.agentguard.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditHashChainTest {

    @Autowired
    private AuditChainService auditChainService;

    @Autowired
    private AuditRecordRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Empty chain verifies successfully")
    void emptyChainVerifies() {
        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertTrue(result.valid());
        assertEquals(0, result.checkedCount());
    }

    @Test
    @DisplayName("Valid appended chain verifies with correct hashes")
    void validChainVerifies() {
        auditChainService.appendRecord("id-1", "DocBot", "summarize", "ALLOW", 0, "Clean summary", "No violations");
        auditChainService.appendRecord("id-2", "DocBot", "send_email", "ALLOW", 5, "Email sent", "Internal note");
        auditChainService.appendRecord("id-3", "DocBot", "read_document", "ALLOW", 0, "Read invoice", "Normal");

        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertTrue(result.valid());
        assertEquals(3, result.checkedCount());
        assertNull(result.brokenRowId());
    }

    @Test
    @DisplayName("Case A: Detects edited field (tampered score) and returns broken row id")
    void detectsEditedFieldTampering() {
        auditChainService.appendRecord("id-1", "DocBot", "read_document", "ALLOW", 0, "Clean", "OK");
        AuditRecord record2 = auditChainService.appendRecord("id-2", "DocBot", "delete_records", "BLOCK", 80, "Delete 5000", "Tier 3");
        auditChainService.appendRecord("id-3", "DocBot", "summarize", "ALLOW", 0, "Summary", "OK");

        // Tamper record 2 by changing score to 0 without updating hash
        record2.setScore(0);
        repository.save(record2);

        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertFalse(result.valid());
        assertEquals("id-2", result.brokenRowId());
        assertTrue(result.reason().contains("Tampered payload detected"));
    }

    @Test
    @DisplayName("Case B: Detects deleted row and returns broken link")
    void detectsDeletedRowTampering() {
        auditChainService.appendRecord("id-1", "DocBot", "read_document", "ALLOW", 0, "Clean", "OK");
        AuditRecord record2 = auditChainService.appendRecord("id-2", "DocBot", "summarize", "ALLOW", 0, "Summary", "OK");
        auditChainService.appendRecord("id-3", "DocBot", "send_email", "ALLOW", 0, "Email", "OK");

        // Delete middle row (id-2)
        repository.delete(record2);

        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertFalse(result.valid());
        assertEquals("id-3", result.brokenRowId());
        assertTrue(result.reason().contains("Sequence broken") || result.reason().contains("Broken hash link"));
    }

    @Test
    @DisplayName("Case C: Detects swapped row order")
    void detectsSwappedRowsTampering() {
        auditChainService.appendRecord("id-1", "DocBot", "read_document", "ALLOW", 0, "Clean", "OK");
        AuditRecord record2 = auditChainService.appendRecord("id-2", "DocBot", "summarize", "ALLOW", 0, "Summary", "OK");
        AuditRecord record3 = auditChainService.appendRecord("id-3", "DocBot", "send_email", "ALLOW", 0, "Email", "OK");

        // Swap sequence numbers of record 2 and record 3
        Long seq2 = record2.getSequenceNumber();
        Long seq3 = record3.getSequenceNumber();
        record2.setSequenceNumber(seq3);
        record3.setSequenceNumber(seq2);
        repository.save(record2);
        repository.save(record3);

        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertFalse(result.valid());
    }

    @Test
    @DisplayName("Concurrency: 50 parallel actions serialize correctly and produce an unbroken valid chain")
    void concurrencyFiftyParallelWrites() throws InterruptedException, ExecutionException {
        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<AuditRecord>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            tasks.add(() -> auditChainService.appendRecord(
                    "concurrent-" + idx + "-" + UUID.randomUUID(),
                    "DocBot",
                    "summarize",
                    "ALLOW",
                    0,
                    "Payload " + idx,
                    "Parallel test"
            ));
        }

        List<Future<AuditRecord>> futures = executor.invokeAll(tasks);
        for (Future<AuditRecord> f : futures) {
            assertNotNull(f.get());
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Verify that all 50 records were written in an unbroken chain
        List<AuditRecord> all = repository.findAllByOrderBySequenceNumberAsc();
        assertEquals(threads, all.size());

        AuditChainService.VerificationResult result = auditChainService.verifyChain();
        assertTrue(result.valid(), "Concurrent chain verification failed: " + result.reason());
        assertEquals(threads, result.checkedCount());
    }
}
