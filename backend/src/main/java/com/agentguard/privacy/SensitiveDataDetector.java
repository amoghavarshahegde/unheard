package com.agentguard.privacy;

import com.agentguard.model.FindingDto;
import java.util.List;

public interface SensitiveDataDetector {
    /**
     * Inspects text and returns detected privacy findings.
     * @param text The input text to analyze
     * @return List of findings (both sensitive and public cues)
     */
    List<FindingDto> detect(String text);

    /**
     * Checks if any detected item in the text is deemed sensitive.
     */
    default boolean hasSensitiveData(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return detect(text).stream().anyMatch(FindingDto::isSensitive);
    }
}
