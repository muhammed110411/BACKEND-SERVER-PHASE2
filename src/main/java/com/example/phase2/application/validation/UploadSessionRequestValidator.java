package com.example.phase2.application.validation;

import org.springframework.stereotype.Component;

import com.example.phase2.api.upload.request.UploadSessionRequest;

@Component
public class UploadSessionRequestValidator {

    private final UploadStructuralValidator uploadStructuralValidator;

    public UploadSessionRequestValidator(UploadStructuralValidator uploadStructuralValidator) {
        this.uploadStructuralValidator = uploadStructuralValidator;
    }

    public void validate(UploadSessionRequest request) {
        uploadStructuralValidator.validateNonEmptySessionPayload(request);
        uploadStructuralValidator.validateSessionIdentity(
                request.getSessionId(),
                request.getDriverId(),
                request.getVehicleId()
        );
        uploadStructuralValidator.validateSessionTimestamps(
                request.getStartTimestamp(),
                request.getEndTimestamp()
        );
        uploadStructuralValidator.validateSessionEnums(
                request.getSessionEndStatus(),
                request.getSessionValidity()
        );
        uploadStructuralValidator.validateSessionTotals(
                request.getFinalScore(),
                request.getTotalContinuousPenalty(),
                request.getTotalEventPenalty(),
                request.getTotalEscalationPenalty(),
                request.getTotalDurationSeconds(),
                request.getTotalDistanceKm(),
                request.getTotalEventCount(),
                request.getTotalEscalationCount()
        );
        uploadStructuralValidator.validateEventRequests(request.getEvents());
        uploadStructuralValidator.validateEscalationRequests(request.getEscalations());
        uploadStructuralValidator.validateScorePointRequests(request.getScorePoints());
        uploadStructuralValidator.validateDeclaredCountsMatchPayload(request);
        uploadStructuralValidator.validateUniqueChildIdentifiers(request);
    }
}
