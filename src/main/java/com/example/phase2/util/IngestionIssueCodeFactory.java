package com.example.phase2.util;

import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class IngestionIssueCodeFactory {

    private static final String PREFIX = "INGESTION";

    public IngestionIssueCodeFactory() {
    }

    public String createRequiredFieldCode(String fieldName) {
        return buildCode("REQUIRED", "FIELD", normalizeToken(fieldName, "fieldName"));
    }

    public String createInvalidFieldCode(String fieldName) {
        return buildCode("INVALID", "FIELD", normalizeToken(fieldName, "fieldName"));
    }

    public String createInconsistentFieldCode(String fieldName) {
        return buildCode("INCONSISTENT", "FIELD", normalizeToken(fieldName, "fieldName"));
    }

    public String createUnknownReferenceCode(String referenceType) {
        return buildCode("UNKNOWN", "REFERENCE", normalizeToken(referenceType, "referenceType"));
    }

    public String createInactiveReferenceCode(String referenceType) {
        return buildCode("INACTIVE", "REFERENCE", normalizeToken(referenceType, "referenceType"));
    }

    public String createDuplicateSessionCode() {
        return buildCode("DUPLICATE", "SESSION");
    }

    public String createDuplicateChildRecordCode(String childType) {
        return buildCode("DUPLICATE", "CHILD", "RECORD", normalizeToken(childType, "childType"));
    }

    public String createCountMismatchCode(String collectionName) {
        return buildCode("COUNT", "MISMATCH", normalizeToken(collectionName, "collectionName"));
    }

    public String createChronologyIssueCode(String fieldName) {
        return buildCode("CHRONOLOGY", "ISSUE", normalizeToken(fieldName, "fieldName"));
    }

    public String createProcessingWarningCode(String warningType) {
        return buildCode("PROCESSING", "WARNING", normalizeToken(warningType, "warningType"));
    }

    public String createProcessingBlockCode(String blockType) {
        return buildCode("PROCESSING", "BLOCK", normalizeToken(blockType, "blockType"));
    }

    private String buildCode(String... parts) {
        StringBuilder builder = new StringBuilder(PREFIX);
        for (String part : parts) {
            builder.append('_').append(part);
        }
        return builder.toString();
    }

    private String normalizeToken(String value, String argumentName) {
        Objects.requireNonNull(value, argumentName + " must not be null");

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(argumentName + " must not be blank");
        }

        String upperCased = trimmed.toUpperCase(Locale.ROOT);
        StringBuilder normalized = new StringBuilder(upperCased.length());
        boolean previousWasUnderscore = false;

        for (int index = 0; index < upperCased.length(); index++) {
            char current = upperCased.charAt(index);
            boolean alphaNumeric = current >= 'A' && current <= 'Z'
                    || current >= '0' && current <= '9';

            if (alphaNumeric) {
                normalized.append(current);
                previousWasUnderscore = false;
                continue;
            }

            if (!previousWasUnderscore) {
                normalized.append('_');
                previousWasUnderscore = true;
            }
        }

        int start = 0;
        int end = normalized.length();
        while (start < end && normalized.charAt(start) == '_') {
            start++;
        }
        while (end > start && normalized.charAt(end - 1) == '_') {
            end--;
        }

        if (start == end) {
            throw new IllegalArgumentException(argumentName + " must contain at least one letter or digit");
        }

        return normalized.substring(start, end);
    }
}
