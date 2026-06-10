package com.example.phase2.application.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.phase2.api.upload.request.UploadEscalationRequest;
import com.example.phase2.api.upload.request.UploadEventRequest;
import com.example.phase2.api.upload.request.UploadScorePointRequest;
import com.example.phase2.api.upload.request.UploadSessionRequest;
import com.example.phase2.config.TimeProvider;
import com.example.phase2.domain.model.IngestionAnomalyFlag;
import com.example.phase2.util.IngestionIssueCodeFactory;

@Component
public class UploadSoftValidationAnalyzer {

    private static final String WARNING = "WARNING";

    private final IngestionIssueCodeFactory ingestionIssueCodeFactory;
    private final TimeProvider timeProvider;

    public UploadSoftValidationAnalyzer(
            IngestionIssueCodeFactory ingestionIssueCodeFactory,
            TimeProvider timeProvider
    ) {
        this.ingestionIssueCodeFactory = ingestionIssueCodeFactory;
        this.timeProvider = timeProvider;
    }

    public List<IngestionAnomalyFlag> analyze(UploadSessionRequest request) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();
        findings.addAll(analyzeSessionSummary(request));
        findings.addAll(analyzeChronology(request));
        findings.addAll(analyzeEventPatterns(safeList(request.getEvents())));
        findings.addAll(analyzeEscalationPatterns(safeList(request.getEscalations())));
        findings.addAll(analyzeScoreHistory(safeList(request.getScorePoints())));
        findings.addAll(analyzeCountConsistency(request));
        findings.addAll(analyzePenaltyConsistency(request));
        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeSessionSummary(UploadSessionRequest request) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();

        if (safeList(request.getEvents()).isEmpty() && request.getTotalEventCount() != null && request.getTotalEventCount() > 0) {
            findings.add(buildWarning(
                    "Event summary count is positive but no event details were uploaded.",
                    "events"
            ));
        }

        if (safeList(request.getEscalations()).isEmpty()
                && request.getTotalEscalationCount() != null
                && request.getTotalEscalationCount() > 0) {
            findings.add(buildWarning(
                    "Escalation summary count is positive but no escalation details were uploaded.",
                    "escalations"
            ));
        }

        if (safeList(request.getScorePoints()).isEmpty()) {
            findings.add(buildWarning(
                    "Score history is missing, which may limit later audit and review.",
                    "scorePoints"
            ));
        }

        if (!safeList(request.getEvents()).isEmpty()
                && request.getTotalDurationSeconds() != null
                && request.getTotalDurationSeconds() == 0L) {
            findings.add(buildWarning(
                    "Session duration is zero while event details exist.",
                    "totalDurationSeconds"
            ));
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeChronology(UploadSessionRequest request) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();
        Long startTimestamp = request.getStartTimestamp();
        Long endTimestamp = request.getEndTimestamp();

        if (startTimestamp == null || endTimestamp == null) {
            return findings;
        }

        long now = nowEpochMillis();
        if (startTimestamp > now || endTimestamp > now) {
            findings.add(buildWarning(
                    "Session timestamps are in the future relative to ingestion analysis time.",
                    "startTimestamp"
            ));
        }

        for (UploadEventRequest event : safeList(request.getEvents())) {
            Long timestamp = event.getTimestamp();
            if (timestamp != null && (timestamp < startTimestamp || timestamp > endTimestamp)) {
                findings.add(buildWarning(
                        "An event timestamp falls outside the uploaded session window.",
                        "events.timestamp"
                ));
            }
        }

        for (UploadEscalationRequest escalation : safeList(request.getEscalations())) {
            Long triggeredAt = escalation.getTriggeredAt();
            if (triggeredAt != null && (triggeredAt < startTimestamp || triggeredAt > endTimestamp)) {
                findings.add(buildWarning(
                        "An escalation timestamp falls outside the uploaded session window.",
                        "escalations.triggeredAt"
                ));
            }
        }

        for (UploadScorePointRequest scorePoint : safeList(request.getScorePoints())) {
            Long timestamp = scorePoint.getTimestamp();
            if (timestamp != null && (timestamp < startTimestamp || timestamp > endTimestamp)) {
                findings.add(buildWarning(
                        "A score history timestamp falls outside the uploaded session window.",
                        "scorePoints.timestamp"
                ));
            }
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeEventPatterns(List<UploadEventRequest> events) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();
        Long previousTimestamp = null;

        for (UploadEventRequest event : events) {
            if (previousTimestamp != null
                    && event.getTimestamp() != null
                    && event.getTimestamp() < previousTimestamp) {
                findings.add(buildWarning(
                        "Event timestamps are not in non-decreasing order.",
                        "events.timestamp"
                ));
                break;
            }
            if (event.getTimestamp() != null) {
                previousTimestamp = event.getTimestamp();
            }
        }

        if (events.size() >= 3) {
            int nullSeverityCount = 0;
            for (UploadEventRequest event : events) {
                if (event.getSeverity() == null || event.getSeverity() == 0D) {
                    nullSeverityCount++;
                }
            }
            if (nullSeverityCount == events.size()) {
                findings.add(buildWarning(
                        "All uploaded events have zero or missing severity values.",
                        "events.severity"
                ));
            }
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeEscalationPatterns(List<UploadEscalationRequest> escalations) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();
        Long previousTriggeredAt = null;

        for (UploadEscalationRequest escalation : escalations) {
            if (previousTriggeredAt != null
                    && escalation.getTriggeredAt() != null
                    && escalation.getTriggeredAt() < previousTriggeredAt) {
                findings.add(buildWarning(
                        "Escalation timestamps are not in non-decreasing order.",
                        "escalations.triggeredAt"
                ));
                break;
            }
            if (escalation.getTriggeredAt() != null) {
                previousTriggeredAt = escalation.getTriggeredAt();
            }

            if (!safeList(escalation.getRelatedEventTypes()).isEmpty()
                    && (escalation.getSeverityMultiplier() == null || escalation.getSeverityMultiplier() <= 1D)) {
                findings.add(buildWarning(
                        "An escalation references related event types but has no meaningful severity multiplier.",
                        "escalations.severityMultiplier"
                ));
            }
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeScoreHistory(List<UploadScorePointRequest> scorePoints) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();
        Long previousTimestamp = null;
        Double previousScore = null;

        for (UploadScorePointRequest scorePoint : scorePoints) {
            if (previousTimestamp != null
                    && scorePoint.getTimestamp() != null
                    && scorePoint.getTimestamp() < previousTimestamp) {
                findings.add(buildWarning(
                        "Score history timestamps are not in non-decreasing order.",
                        "scorePoints.timestamp"
                ));
                break;
            }

            if (previousScore != null
                    && scorePoint.getScoreValue() != null
                    && Math.abs(scorePoint.getScoreValue() - previousScore) > 25D) {
                findings.add(buildWarning(
                        "Score history contains an abrupt jump between adjacent points.",
                        "scorePoints.scoreValue"
                ));
                break;
            }

            if (scorePoint.getTimestamp() != null) {
                previousTimestamp = scorePoint.getTimestamp();
            }
            if (scorePoint.getScoreValue() != null) {
                previousScore = scorePoint.getScoreValue();
            }
        }

        if (scorePoints.size() == 1) {
            findings.add(buildWarning(
                    "Score history contains only a single point.",
                    "scorePoints"
            ));
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzeCountConsistency(UploadSessionRequest request) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();

        if (request.getTotalEventCount() != null
                && request.getTotalEventCount() != safeList(request.getEvents()).size()) {
            findings.add(buildWarning(
                    "Uploaded totalEventCount does not match the number of uploaded events.",
                    "totalEventCount"
            ));
        }

        if (request.getTotalEscalationCount() != null
                && request.getTotalEscalationCount() != safeList(request.getEscalations()).size()) {
            findings.add(buildWarning(
                    "Uploaded totalEscalationCount does not match the number of uploaded escalations.",
                    "totalEscalationCount"
            ));
        }

        return findings;
    }

    private List<IngestionAnomalyFlag> analyzePenaltyConsistency(UploadSessionRequest request) {
        List<IngestionAnomalyFlag> findings = new ArrayList<>();

        if (request.getTotalEventPenalty() != null
                && request.getTotalEventPenalty() > 0D
                && safeList(request.getEvents()).isEmpty()) {
            findings.add(buildWarning(
                    "Event penalty is positive while no event details were uploaded.",
                    "totalEventPenalty"
            ));
        }

        if (request.getTotalEscalationPenalty() != null
                && request.getTotalEscalationPenalty() > 0D
                && safeList(request.getEscalations()).isEmpty()) {
            findings.add(buildWarning(
                    "Escalation penalty is positive while no escalation details were uploaded.",
                    "totalEscalationPenalty"
            ));
        }

        if (request.getTotalContinuousPenalty() != null
                && request.getTotalContinuousPenalty() > 0D
                && safeList(request.getScorePoints()).isEmpty()) {
            findings.add(buildWarning(
                    "Continuous penalty is positive while score history is missing.",
                    "totalContinuousPenalty"
            ));
        }

        return findings;
    }

    private IngestionAnomalyFlag buildWarning(String message, String fieldName) {
        String code = fieldName != null && (
                fieldName.contains("timestamp")
                        || fieldName.contains("triggeredAt")
                        || fieldName.contains("startTimestamp")
        )
                ? ingestionIssueCodeFactory.createChronologyIssueCode(fieldName)
                : ingestionIssueCodeFactory.createProcessingWarningCode(fieldName == null ? "session" : fieldName);

        return new IngestionAnomalyFlag(
                code,
                message,
                WARNING,
                fieldName,
                nowEpochMillis(),
                false
        );
    }

    private long nowEpochMillis() {
        return timeProvider.nowEpochMillis();
    }

    private <T> List<T> safeList(List<T> items) {
        return items == null ? Collections.emptyList() : items;
    }
}
