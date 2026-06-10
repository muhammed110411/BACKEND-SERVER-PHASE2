package com.example.phase2.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SessionDetailRecord {

    private final DrivingSessionRecord session;
    private final List<SafetyEventRecord> events;
    private final List<EscalationRecord> escalations;
    private final List<ScorePointRecord> scoreHistory;
    private final List<IngestionAnomalyFlag> ingestionAnomalyFlags;

    public SessionDetailRecord(
            DrivingSessionRecord session,
            List<SafetyEventRecord> events,
            List<EscalationRecord> escalations,
            List<ScorePointRecord> scoreHistory,
            List<IngestionAnomalyFlag> ingestionAnomalyFlags
    ) {
        this.session = Objects.requireNonNull(session, "session must not be null");
        this.events = Collections.unmodifiableList(requireSessionScopedList(events, session.getId(), "events"));
        this.escalations = Collections.unmodifiableList(requireSessionScopedList(escalations, session.getId(), "escalations"));
        this.scoreHistory = Collections.unmodifiableList(requireSessionScopedList(scoreHistory, session.getId(), "scoreHistory"));
        this.ingestionAnomalyFlags = Collections.unmodifiableList(requireAnomalyFlags(ingestionAnomalyFlags));
    }

    public DrivingSessionRecord getSession() {
        return session;
    }

    public List<SafetyEventRecord> getEvents() {
        return events;
    }

    public List<EscalationRecord> getEscalations() {
        return escalations;
    }

    public List<ScorePointRecord> getScoreHistory() {
        return scoreHistory;
    }

    public List<IngestionAnomalyFlag> getIngestionAnomalyFlags() {
        return ingestionAnomalyFlags;
    }

    public boolean hasAnomalies() {
        return !ingestionAnomalyFlags.isEmpty();
    }

    public boolean isProcessedSuccessfully() {
        return session.getUploadProcessingStatus().isSuccessful() && !hasAnomalies();
    }

    public int eventCount() {
        return events.size();
    }

    public int escalationCount() {
        return escalations.size();
    }

    public int scorePointCount() {
        return scoreHistory.size();
    }

    private static <T> List<T> requireSessionScopedList(List<T> values, String sessionId, String fieldName) {
        Objects.requireNonNull(values, fieldName + " must not be null");
        String owningSessionId = requireText(sessionId, "sessionId");
        List<T> copy = new ArrayList<>(values.size());
        for (T value : values) {
            Objects.requireNonNull(value, fieldName + " must not contain null elements");
            requireBelongsToSession(value, owningSessionId, fieldName);
            copy.add(value);
        }
        return copy;
    }

    private static List<IngestionAnomalyFlag> requireAnomalyFlags(List<IngestionAnomalyFlag> values) {
        Objects.requireNonNull(values, "ingestionAnomalyFlags must not be null");
        List<IngestionAnomalyFlag> copy = new ArrayList<>(values.size());
        for (IngestionAnomalyFlag value : values) {
            Objects.requireNonNull(value, "ingestionAnomalyFlags must not contain null elements");
            copy.add(value);
        }
        return copy;
    }

    private static void requireBelongsToSession(Object value, String sessionId, String fieldName) {
        if (value instanceof SafetyEventRecord) {
            if (!((SafetyEventRecord) value).belongsToSession(sessionId)) {
                throw new IllegalArgumentException(fieldName + " must belong to sessionId " + sessionId);
            }
            return;
        }
        if (value instanceof EscalationRecord) {
            if (!((EscalationRecord) value).belongsToSession(sessionId)) {
                throw new IllegalArgumentException(fieldName + " must belong to sessionId " + sessionId);
            }
            return;
        }
        if (value instanceof ScorePointRecord) {
            if (!((ScorePointRecord) value).belongsToSession(sessionId)) {
                throw new IllegalArgumentException(fieldName + " must belong to sessionId " + sessionId);
            }
            return;
        }
        throw new IllegalArgumentException(fieldName + " contains an unsupported record type");
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
