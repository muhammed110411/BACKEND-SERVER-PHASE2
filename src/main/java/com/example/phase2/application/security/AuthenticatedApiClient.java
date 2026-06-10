package com.example.phase2.application.security;

import java.util.Objects;

public class AuthenticatedApiClient {

    private final String clientId;
    private final boolean active;
    private final boolean uploadAllowed;
    private final String principalName;

    public AuthenticatedApiClient(
            String clientId,
            boolean active,
            boolean uploadAllowed,
            String principalName
    ) {
        this.clientId = Objects.requireNonNull(clientId, "clientId must not be null");
        this.active = active;
        this.uploadAllowed = uploadAllowed;
        this.principalName = Objects.requireNonNull(principalName, "principalName must not be null");
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isUploadAllowed() {
        return uploadAllowed;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public boolean supportsUploadAccess() {
        return active && uploadAllowed;
    }
}
