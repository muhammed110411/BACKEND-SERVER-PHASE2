package com.example.phase2.api.admin.response.mobile;

public record MobileAdminSessionPenaltiesResponse(
        double continuousPenalty,
        double eventPenalty,
        double escalationPenalty
) {
}
