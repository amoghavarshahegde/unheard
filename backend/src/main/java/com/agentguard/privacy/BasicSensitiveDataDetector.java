package com.agentguard.privacy;

import com.agentguard.model.FindingDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BasicSensitiveDataDetector implements SensitiveDataDetector {

    private static final Pattern PAN_PATTERN =
            Pattern.compile("\\b[A-Z]{5}[0-9]{4}[A-Z]\\b");

    private static final Pattern AADHAAR_PATTERN =
            Pattern.compile("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("(?:\\+?91[\\s-]?)?[6-9]\\d{9}|\\b\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}\\b|\\b\\d{10}\\b");

    private static final Pattern SALARY_PATTERN =
            Pattern.compile("(?i)\\b(?:ctc|salary|stipend|compensation)\\b[:\\s]*[\\$₹€Rs.]*\\s*[\\d,]+(?:\\s*(?:lpa|k|lac|lakh|per\\s+month|per\\s+year|pm|pa))?|\\b\\d+\\s*(?:lpa|k|lakh)\\b|\\$\\s*\\d+(?:,\\d{3})*(?:k)?");

    @Override
    public List<FindingDto> detect(String text) {
        List<FindingDto> findings = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return findings;
        }

        // PAN detection
        Matcher panMatcher = PAN_PATTERN.matcher(text);
        while (panMatcher.find()) {
            findings.add(new FindingDto("PAN", panMatcher.group(), "PAN pattern detected", true, panMatcher.start(), panMatcher.end()));
        }

        // Aadhaar detection
        Matcher aadhaarMatcher = AADHAAR_PATTERN.matcher(text);
        while (aadhaarMatcher.find()) {
            findings.add(new FindingDto("AADHAAR", aadhaarMatcher.group(), "Aadhaar number detected (12 digits)", true, aadhaarMatcher.start(), aadhaarMatcher.end()));
        }

        // Salary / CTC detection
        Matcher salaryMatcher = SALARY_PATTERN.matcher(text);
        while (salaryMatcher.find()) {
            findings.add(new FindingDto("SALARY", salaryMatcher.group(), "Salary or CTC mention detected", true, salaryMatcher.start(), salaryMatcher.end()));
        }

        // Phone detection
        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        while (phoneMatcher.find()) {
            // Avoid overlapping if it was caught as part of Aadhaar
            int start = phoneMatcher.start();
            int end = phoneMatcher.end();
            boolean overlaps = findings.stream().anyMatch(f ->
                    (start >= f.getStart() && start < f.getEnd()) ||
                    (end > f.getStart() && end <= f.getEnd()));
            if (!overlaps) {
                findings.add(new FindingDto("PHONE", phoneMatcher.group(), "Customer phone number", true, start, end));
            }
        }

        return findings;
    }
}
