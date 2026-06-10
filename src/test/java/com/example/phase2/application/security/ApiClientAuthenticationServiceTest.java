package com.example.phase2.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.exception.UnauthorizedApiClientException;
import com.example.phase2.persistence.entity.ApiClientEntity;
import com.example.phase2.persistence.repository.ApiClientJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ApiClientAuthenticationServiceTest {

    private ApiClientJpaRepository apiClientJpaRepository;
    private PasswordEncoder passwordEncoder;
    private ApiClientAuthenticationService apiClientAuthenticationService;

    @BeforeEach
    void setUp() {
        apiClientJpaRepository = mock(ApiClientJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        apiClientAuthenticationService = new ApiClientAuthenticationService(apiClientJpaRepository, passwordEncoder);
    }

    @Test
    void authenticateReturnsAuthenticatedClientWhenCredentialsAndStateAreValid() {
        ApiClientEntity apiClientEntity = activeApiClientEntity("upload-client", "stored-hash");
        when(apiClientJpaRepository.findByClientId("upload-client")).thenReturn(Optional.of(apiClientEntity));
        when(passwordEncoder.matches("secret-value", "stored-hash")).thenReturn(true);

        AuthenticatedApiClient authenticatedApiClient =
                apiClientAuthenticationService.authenticate("upload-client", "secret-value");

        assertEquals("upload-client", authenticatedApiClient.getClientId());
        assertEquals("Upload Client", authenticatedApiClient.getPrincipalName());
        assertTrue(authenticatedApiClient.isActive());
        assertTrue(authenticatedApiClient.isUploadAllowed());
        verify(apiClientJpaRepository).findByClientId("upload-client");
        verify(passwordEncoder).matches("secret-value", "stored-hash");
    }

    @Test
    void authenticateRejectsBlankClientId() {
        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("   ", "secret-value")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        verify(apiClientJpaRepository, never()).findByClientId(org.mockito.ArgumentMatchers.anyString());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsBlankClientSecret() {
        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("upload-client", " ")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        verify(apiClientJpaRepository, never()).findByClientId(org.mockito.ArgumentMatchers.anyString());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsUnknownClientId() {
        when(apiClientJpaRepository.findByClientId("missing-client")).thenReturn(Optional.empty());

        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("missing-client", "secret-value")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        assertEquals("missing-client", exception.getClientId());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsClientWithMissingSecretHash() {
        ApiClientEntity apiClientEntity = activeApiClientEntity("upload-client", "stored-hash");
        apiClientEntity.setClientSecretHash(" ");
        when(apiClientJpaRepository.findByClientId("upload-client")).thenReturn(Optional.of(apiClientEntity));

        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("upload-client", "secret-value")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticateRejectsWhenPasswordHashDoesNotMatch() {
        ApiClientEntity apiClientEntity = activeApiClientEntity("upload-client", "stored-hash");
        when(apiClientJpaRepository.findByClientId("upload-client")).thenReturn(Optional.of(apiClientEntity));
        when(passwordEncoder.matches("wrong-secret", "stored-hash")).thenReturn(false);

        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("upload-client", "wrong-secret")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        verify(passwordEncoder).matches("wrong-secret", "stored-hash");
    }

    @Test
    void authenticateRejectsInactiveClientAfterCredentialVerification() {
        ApiClientEntity apiClientEntity = inactiveApiClientEntity("upload-client", "stored-hash");
        when(apiClientJpaRepository.findByClientId("upload-client")).thenReturn(Optional.of(apiClientEntity));
        when(passwordEncoder.matches("secret-value", "stored-hash")).thenReturn(true);

        UnauthorizedApiClientException exception = assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.authenticate("upload-client", "secret-value")
        );

        assertEquals("UNAUTHORIZED_API_CLIENT", exception.getErrorCode());
        assertEquals("upload-client", exception.getClientId());
    }

    @Test
    void validateActiveClientRejectsNullClient() {
        assertThrows(
                UnauthorizedApiClientException.class,
                () -> apiClientAuthenticationService.validateActiveClient(null)
        );
    }

    @Test
    void supportsUploadAccessReturnsFalseForNullOrIneligibleClient() {
        assertFalse(apiClientAuthenticationService.supportsUploadAccess(null));
        assertFalse(apiClientAuthenticationService.supportsUploadAccess(
                new AuthenticatedApiClient("client-a", false, true, "Client A")
        ));
        assertFalse(apiClientAuthenticationService.supportsUploadAccess(
                new AuthenticatedApiClient("client-b", true, false, "Client B")
        ));
        assertTrue(apiClientAuthenticationService.supportsUploadAccess(
                new AuthenticatedApiClient("client-c", true, true, "Client C")
        ));
    }

    private static ApiClientEntity activeApiClientEntity(String clientId, String clientSecretHash) {
        return new ApiClientEntity(
                "api-client-1",
                clientId,
                clientSecretHash,
                "Upload Client",
                true,
                100L,
                100L,
                null,
                null
        );
    }

    private static ApiClientEntity inactiveApiClientEntity(String clientId, String clientSecretHash) {
        return new ApiClientEntity(
                "api-client-2",
                clientId,
                clientSecretHash,
                "Upload Client",
                false,
                100L,
                100L,
                null,
                200L
        );
    }
}
