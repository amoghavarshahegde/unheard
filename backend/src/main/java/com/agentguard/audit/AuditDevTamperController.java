package com.agentguard.audit;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/audit")
@Profile("dev")
public class AuditDevTamperController {

    private final AuditRecordRepository repository;

    public AuditDevTamperController(AuditRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Dev-only tamper endpoint to modify an existing audit record's score/tool
     * without updating the cryptographic hash, to prove tamper detection in demos.
     */
    @PostMapping("/tamper/{id}")
    public ResponseEntity<?> tamperRecord(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
        Optional<AuditRecord> recordOpt = repository.findById(id);
        if (recordOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AuditRecord record = recordOpt.get();
        if (body != null && body.containsKey("tool")) {
            record.setTool((String) body.get("tool"));
        } else {
            // Default tamper: change tool to "tampered_tool" and score to 0
            record.setTool("tampered_tool");
            record.setScore(0);
        }

        AuditRecord saved = repository.save(record);
        return ResponseEntity.ok(Map.of(
                "message", "Audit record tampered successfully (hash not updated)",
                "tamperedId", saved.getId(),
                "tamperedTool", saved.getTool(),
                "tamperedScore", saved.getScore()
        ));
    }

    /**
     * Dev-only endpoint to delete a record, proving deleted-row detection.
     */
    @DeleteMapping("/tamper/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "message", "Audit record deleted to test break in chain",
                "deletedId", id
        ));
    }
}
