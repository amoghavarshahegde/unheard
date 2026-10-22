package com.agentguard.privacy;

import com.agentguard.model.FindingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContextAwarePrivacyLensTest {

    private ContextAwarePrivacyLens privacyLens;

    @BeforeEach
    void setUp() {
        privacyLens = new ContextAwarePrivacyLens();
    }

    @Test
    @DisplayName("Helpline cue within 45 chars marks phone as PUBLIC and not redacted")
    void helplineCueMakesPhonePublic() {
        String text = "For emergency assistance, please call our 24x7 helpline: +91 9876543210 immediately.";
        List<FindingDto> findings = privacyLens.detect(text);

        assertFalse(findings.isEmpty());
        FindingDto phoneFinding = findings.stream().filter(f -> "PHONE".equals(f.getType())).findFirst().orElseThrow();
        assertFalse(phoneFinding.isSensitive(), "Phone near helpline cue must be marked public (not sensitive)");
        assertTrue(phoneFinding.getReason().contains("helpline cue"));

        // Redaction check on external egress
        String redacted = privacyLens.redact(text, true);
        assertEquals(text, redacted, "Public helpline must NOT be redacted even on external egress");
    }

    @Test
    @DisplayName("Other cues (support, office, reception, customer care, toll-free) also mark phone as public")
    void otherPublicCues() {
        List<String> samples = List.of(
                "Contact technical support at 9876543210 for billing.",
                "Call Bangalore office at +91 9876543210 for visits.",
                "Our toll-free number is 9876543210.",
                "Reach our front reception at 9876543210.",
                "Direct inquiries to customer care at 9876543210."
        );

        for (String sample : samples) {
            List<FindingDto> findings = privacyLens.detect(sample);
            FindingDto phone = findings.stream().filter(f -> "PHONE".equals(f.getType())).findFirst().orElseThrow();
            assertFalse(phone.isSensitive(), "Expected public finding for: " + sample);
            assertEquals(sample, privacyLens.redact(sample, true));
        }
    }

    @Test
    @DisplayName("Customer phone without public cue is sensitive and redacted on external egress")
    void customerPhoneRedactedOnExternalEgress() {
        String text = "Please call customer Alice at +91 9876543210 regarding invoice dispute.";
        List<FindingDto> findings = privacyLens.detect(text);

        FindingDto phoneFinding = findings.stream().filter(f -> "PHONE".equals(f.getType())).findFirst().orElseThrow();
        assertTrue(phoneFinding.isSensitive(), "Customer phone without cue must be sensitive");

        String redacted = privacyLens.redact(text, true);
        assertTrue(redacted.contains("[REDACTED:PHONE]"));
        assertFalse(redacted.contains("9876543210"));
    }

    @Test
    @DisplayName("Internal route is logged in findings but NOT redacted")
    void internalRouteNotRedacted() {
        String text = "Colleague phone is +91 9876543210.";
        List<FindingDto> findings = privacyLens.detect(text);
        assertTrue(findings.stream().anyMatch(FindingDto::isSensitive));

        String redacted = privacyLens.redact(text, false);
        assertEquals(text, redacted, "Internal route must NOT redact text");
    }

    @Test
    @DisplayName("Detects and redacts PAN on external egress")
    void detectsAndRedactsPan() {
        String text = "Tax verification PAN is ABCDE1234F.";
        List<FindingDto> findings = privacyLens.detect(text);

        FindingDto pan = findings.stream().filter(f -> "PAN".equals(f.getType())).findFirst().orElseThrow();
        assertTrue(pan.isSensitive());
        assertEquals("PAN card", pan.getReason());

        String redacted = privacyLens.redact(text, true);
        assertEquals("Tax verification PAN is [REDACTED:PAN].", redacted);
    }

    @Test
    @DisplayName("Detects and redacts Aadhaar (12 digits) on external egress")
    void detectsAndRedactsAadhaar() {
        String text = "Client KYC submission Aadhaar 1234 5678 9012.";
        List<FindingDto> findings = privacyLens.detect(text);

        FindingDto aadhaar = findings.stream().filter(f -> "AADHAAR".equals(f.getType())).findFirst().orElseThrow();
        assertTrue(aadhaar.isSensitive());

        String redacted = privacyLens.redact(text, true);
        assertEquals("Client KYC submission Aadhaar [REDACTED:AADHAAR].", redacted);
    }

    @Test
    @DisplayName("Detects and redacts Salary/CTC mentions on external egress")
    void detectsAndRedactsSalary() {
        String text = "Candidate has a current CTC of 25 LPA.";
        List<FindingDto> findings = privacyLens.detect(text);

        FindingDto salary = findings.stream().filter(f -> "SALARY".equals(f.getType())).findFirst().orElseThrow();
        assertTrue(salary.isSensitive());

        String redacted = privacyLens.redact(text, true);
        assertTrue(redacted.contains("[REDACTED:SALARY]"));
        assertFalse(redacted.contains("25 LPA"));
    }
}
