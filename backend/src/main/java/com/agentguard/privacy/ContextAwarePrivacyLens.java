package com.agentguard.privacy;

import com.agentguard.model.FindingDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Primary
public class ContextAwarePrivacyLens implements SensitiveDataDetector {

    private static final Pattern PAN_PATTERN =
            Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]\\b");

    private static final Pattern AADHAAR_PATTERN =
            Pattern.compile("\\b(?:\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}|\\d{12})\\b");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(?:\\+?91[\\s-]?)?[6-9]\\d{9}|\\b\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}\\b|\\b\\d{10}\\b");

    private static final Pattern SALARY_PATTERN =
            Pattern.compile("(?i)\\b(?:ctc|salary|stipend|compensation)\\b[:\\s]*[\\$₹€Rs.]*\\s*[\\d,]+(?:\\s*(?:lpa|k|lac|lakh|per\\s+month|per\\s+year|pm|pa))?|\\b\\d+\\s*(?:lpa|k|lakh)\\b|\\$\\s*\\d+(?:,\\d{3})*(?:k)?");

    private static final List<String> PUBLIC_CUES = List.of(
            "helpline",
            "toll-free",
            "toll free",
            "support",
            "reception",
            "customer care",
            "office"
    );

    private static final int CONTEXT_WINDOW = 45;

    @Override
    public List<FindingDto> detect(String text) {
        List<FindingDto> findings = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return findings;
        }

        // 1. PAN detection
        Matcher panMatcher = PAN_PATTERN.matcher(text);
        while (panMatcher.find()) {
            findings.add(new FindingDto(
                    "PAN",
                    panMatcher.group(),
                    "PAN card",
                    true,
                    panMatcher.start(),
                    panMatcher.end()
            ));
        }

        // 2. Aadhaar detection
        Matcher aadhaarMatcher = AADHAAR_PATTERN.matcher(text);
        while (aadhaarMatcher.find()) {
            findings.add(new FindingDto(
                    "AADHAAR",
                    aadhaarMatcher.group(),
                    "Aadhaar card (12 digits)",
                    true,
                    aadhaarMatcher.start(),
                    aadhaarMatcher.end()
            ));
        }

        // 3. Salary / CTC detection
        Matcher salaryMatcher = SALARY_PATTERN.matcher(text);
        while (salaryMatcher.find()) {
            findings.add(new FindingDto(
                    "SALARY",
                    salaryMatcher.group(),
                    "Salary or compensation detail",
                    true,
                    salaryMatcher.start(),
                    salaryMatcher.end()
            ));
        }

        // 4. Phone detection with context-awareness
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        while (phoneMatcher.find()) {
            int start = phoneMatcher.start();
            int end = phoneMatcher.end();
            String matchedPhone = phoneMatcher.group();

            // Ignore if it's already an exact subset of an Aadhaar match
            boolean isSubsumedByAadhaar = findings.stream()
                    .filter(f -> "AADHAAR".equals(f.getType()))
                    .anyMatch(f -> start >= f.getStart() && end <= f.getEnd());
            if (isSubsumedByAadhaar) {
                continue;
            }

            // Check context window for public cues within 45 chars before or after
            String cueFound = findPublicCue(text, start, end);
            if (cueFound != null) {
                findings.add(new FindingDto(
                        "PHONE",
                        matchedPhone,
                        "public contact (" + cueFound + " cue)",
                        false,
                        start,
                        end
                ));
            } else {
                findings.add(new FindingDto(
                        "PHONE",
                        matchedPhone,
                        "Customer phone number",
                        true,
                        start,
                        end
                ));
            }
        }

        // Sort findings by start position
        findings.sort(Comparator.comparingInt(FindingDto::getStart));
        return findings;
    }

    private String findPublicCue(String text, int start, int end) {
        int windowStart = Math.max(0, start - CONTEXT_WINDOW);
        int windowEnd = Math.min(text.length(), end + CONTEXT_WINDOW);

        String before = text.substring(windowStart, start).toLowerCase();
        String after = text.substring(end, windowEnd).toLowerCase();

        for (String cue : PUBLIC_CUES) {
            if (before.contains(cue) || after.contains(cue)) {
                return cue;
            }
        }
        return null;
    }

    /**
     * Redacts sensitive values as [REDACTED:TYPE] on external egress.
     * Public findings and internal destinations are NOT redacted.
     */
    public String redact(String text, boolean isExternal) {
        if (text == null || text.isBlank() || !isExternal) {
            return text;
        }

        List<FindingDto> findings = detect(text);
        // Filter only sensitive items
        List<FindingDto> sensitiveFindings = findings.stream()
                .filter(FindingDto::isSensitive)
                .sorted((a, b) -> Integer.compare(b.getStart(), a.getStart())) // Replace from back to front
                .toList();

        StringBuilder sb = new StringBuilder(text);
        for (FindingDto f : sensitiveFindings) {
            String replacement = "[REDACTED:" + f.getType() + "]";
            if (f.getStart() >= 0 && f.getEnd() <= sb.length()) {
                sb.replace(f.getStart(), f.getEnd(), replacement);
            }
        }
        return sb.toString();
    }
}
