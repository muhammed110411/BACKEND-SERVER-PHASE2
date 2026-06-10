package com.example.phase2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.application.security.AuthenticatedApiClient;
import com.example.phase2.exception.UnauthorizedApiClientException;
import jakarta.servlet.FilterChain;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class ApiClientAuthFilterTest {

    private ApiClientAuthenticationService apiClientAuthenticationService;
    private AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private ApiClientAuthFilter apiClientAuthFilter;

    @BeforeEach
    void setUp() {
        apiClientAuthenticationService = mock(ApiClientAuthenticationService.class);
        authenticationEntryPointHandler = mock(AuthenticationEntryPointHandler.class);
        apiClientAuthFilter = new ApiClientAuthFilter(
                apiClientAuthenticationService,
                authenticationEntryPointHandler
        );
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilterReturnsTrueForNonUploadRoutes() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/drivers");

        assertEquals(true, apiClientAuthFilter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternalAuthenticatesValidBasicAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", basicAuthorization("upload-client", "secret-value"));

        AuthenticatedApiClient authenticatedApiClient =
                new AuthenticatedApiClient("upload-client", true, true, "Upload Client");
        when(apiClientAuthenticationService.authenticate("upload-client", "secret-value"))
                .thenReturn(authenticatedApiClient);

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService).authenticate("upload-client", "secret-value");
        verify(filterChain).doFilter(request, response);
        verify(authenticationEntryPointHandler, never()).commence(any(), any(), any());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(authenticatedApiClient, authentication.getPrincipal());
        assertEquals("ROLE_API_CLIENT", authentication.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void doFilterInternalRejectsMissingAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService, never()).authenticate(any(), any());
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRejectsNonBasicAuthorizationScheme() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", "Bearer token-value");

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService, never()).authenticate(any(), any());
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRejectsMalformedBasicCredentials() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", "Basic not-base64");

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService, never()).authenticate(any(), any());
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRejectsCredentialsWithMultipleDelimiters() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", basicAuthorization("client", "secret:extra"));

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService, never()).authenticate(any(), any());
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalRejectsBlankCredentialParts() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", basicAuthorization(" ", "secret-value"));

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService, never()).authenticate(any(), any());
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalDelegatesInvalidCredentialsToEntryPoint() throws Exception {
        MockHttpServletRequest request = uploadRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("Authorization", basicAuthorization("upload-client", "wrong-secret"));

        when(apiClientAuthenticationService.authenticate("upload-client", "wrong-secret"))
                .thenThrow(new UnauthorizedApiClientException(
                        "Invalid API client credentials.",
                        "upload-client",
                        "UNAUTHORIZED_API_CLIENT"
                ));

        apiClientAuthFilter.doFilterInternal(request, response, filterChain);

        verify(apiClientAuthenticationService).authenticate("upload-client", "wrong-secret");
        verify(authenticationEntryPointHandler).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private static MockHttpServletRequest uploadRequest() {
        return new MockHttpServletRequest("POST", "/api/upload/sessions");
    }

    private static String basicAuthorization(String clientId, String clientSecret) {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encodedCredentials;
    }
}
