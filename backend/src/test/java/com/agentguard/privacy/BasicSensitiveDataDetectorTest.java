package com.agentguard.privacy;

import com.agentguard.model.FindingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicSensitiveDataDetectorTest {

    private BasicSensitiveDataDetector detector;

    @BeforeEach
    void setUp() {
        detector = new BasicSensitiveDataDetector();
    }

    @Test
    @DisplayName("Detects Indian PAN card numbers")
    void detectsPan() {
        String text = "Please update customer record with PAN ABCDE1234F for verification.";
        List<FindingDto> findings = detector.detect(text);
        assertFalse(findings.isEmpty());
        assertTrue(findings.stream().anyMatch(f -> "PAN".equals(f.getType()) && "ABCDE1234F".equals(f.getValue())));
        assertTrue(detector.hasSensitiveData(text));
    }

    @Test
    @DisplayName("Detects 12-digit Aadhaar numbers")
    void detectsAadhaar() {
        String text = "Customer aadhaar is 1234 5678 9012 submitted for KYC.";
        List<FindingDto> findings = detector.detect(text);
        assertTrue(findings.stream().anyMatch(f -> "AADHAAR".equals(f.getType())));
        assertTrue(detector.hasSensitiveData(text));
    }

    @Test
    @DisplayName("Detects standard phone numbers")
    void detectsPhone() {
        String text = "Call client at +91 9876543210 for account details.";
        List<FindingDto> findings = detector.detect(text);
        assertTrue(findings.stream().anyMatch(f -> "PHONE".equals(f.getType())));
        assertTrue(detector.hasSensitiveData(text));
    }

    @Test
    @DisplayName("Detects salary and CTC mentions")
    void detectsSalary() {
        String text = "Offered CTC of 25 LPA with 10% annual bonus.";
        List<FindingDto> findings = detector.detect(text);
        assertTrue(findings.stream().anyMatch(f -> "SALARY".equals(f.getType())));
        assertTrue(detector.hasSensitiveData(text));
    }

    @Test
    @DisplayName("Returns empty findings on clean text")
    void cleanTextReturnsEmpty() {
        String text = "Summarizing quarterly report with team accomplishments.";
        List<FindingDto> findings = detector.detect(text);
        assertTrue(findings.isEmpty());
        assertFalse(detector.hasSensitiveData(text));
    }
}
