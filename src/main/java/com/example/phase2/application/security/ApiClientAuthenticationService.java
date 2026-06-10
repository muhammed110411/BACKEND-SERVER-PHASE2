package com.example.phase2.application.security;

import com.example.phase2.exception.UnauthorizedApiClientException;
import com.example.phase2.persistence.entity.ApiClientEntity;
import com.example.phase2.persistence.repository.ApiClientJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiClientAuthenticationService {

    private static final String UNAUTHORIZED_CLIENT_ERROR_CODE = "UNAUTHORIZED_API_CLIENT";

    private final ApiClientJpaRepository apiClientJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiClientAuthenticationService(
            ApiClientJpaRepository apiClientJpaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.apiClientJpaRepository = apiClientJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AuthenticatedApiClient authenticate(String clientId, String clientSecret) {
        String requiredClientId = requireCredential(clientId, "clientId");
        String requiredClientSecret = requireCredential(clientSecret, "clientSecret");

        ApiClientEntity apiClientEntity = apiClientJpaRepository.findByClientId(requiredClientId)
                .orElseThrow(() -> unauthorized(requiredClientId, "Invalid API client credentials."));

        String clientSecretHash = apiClientEntity.getClientSecretHash();
        if (clientSecretHash == null || clientSecretHash.isBlank()) {
            throw unauthorized(requiredClientId, "Invalid API client credentials.");
        }

        if (!passwordEncoder.matches(requiredClientSecret, clientSecretHash)) {
            throw unauthorized(requiredClientId, "Invalid API client credentials.");
        }

        AuthenticatedApiClient authenticatedApiClient = toAuthenticatedApiClient(apiClientEntity);
        validateActiveClient(authenticatedApiClient);
        return authenticatedApiClient;
    }

    public void validateActiveClient(AuthenticatedApiClient client) {
        if (!supportsUploadAccess(client)) {
            String clientId = client == null ? null : client.getClientId();
            throw unauthorized(clientId, "API client is not allowed to upload.");
        }
    }

    public boolean supportsUploadAccess(AuthenticatedApiClient client) {
        return client != null && client.isActive() && client.isUploadAllowed();
    }

    private AuthenticatedApiClient toAuthenticatedApiClient(ApiClientEntity apiClientEntity) {
        return new AuthenticatedApiClient(
                apiClientEntity.getClientId(),
                apiClientEntity.isActive(),
                apiClientEntity.isActive(),
                apiClientEntity.getDisplayName()
        );
    }

    private static String requireCredential(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw unauthorized(null, fieldName + " must not be blank");
        }
        return value;
    }

    private static UnauthorizedApiClientException unauthorized(String clientId, String message) {
        return new UnauthorizedApiClientException(message, clientId, UNAUTHORIZED_CLIENT_ERROR_CODE);
    }
}
