package com.example.phase2.security;

import com.example.phase2.application.security.ApiClientAuthenticationService;
import com.example.phase2.application.security.AuthenticatedApiClient;
import com.example.phase2.exception.UnauthorizedApiClientException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class ApiClientAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_SCHEME_PREFIX = "Basic ";
    private static final String API_CLIENT_ROLE = "ROLE_API_CLIENT";

    private final ApiClientAuthenticationService apiClientAuthenticationService;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;

    public ApiClientAuthFilter(
            ApiClientAuthenticationService apiClientAuthenticationService,
            AuthenticationEntryPointHandler authenticationEntryPointHandler
    ) {
        this.apiClientAuthenticationService = apiClientAuthenticationService;
        this.authenticationEntryPointHandler = authenticationEntryPointHandler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isUploadRoute(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            ClientCredentials clientCredentials = parseBasicAuthorizationHeader(request);
            AuthenticatedApiClient authenticatedApiClient = apiClientAuthenticationService.authenticate(
                    clientCredentials.clientId(),
                    clientCredentials.clientSecret()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            authenticatedApiClient,
                            null,
                            List.of(new SimpleGrantedAuthority(API_CLIENT_ROLE))
                    );
            authentication.setDetails(authenticatedApiClient);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (UnauthorizedApiClientException exception) {
            SecurityContextHolder.clearContext();
            authenticationEntryPointHandler.commence(
                    request,
                    response,
                    new BadCredentialsException(exception.getMessage(), exception)
            );
        }
    }

    private static ClientCredentials parseBasicAuthorizationHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw unauthorized("Missing Authorization header.");
        }
        if (!authorizationHeader.startsWith(BASIC_SCHEME_PREFIX)) {
            throw unauthorized("Authorization scheme must be Basic.");
        }

        String encodedCredentials = authorizationHeader.substring(BASIC_SCHEME_PREFIX.length());
        if (encodedCredentials.isBlank()) {
            throw unauthorized("Basic credentials are malformed.");
        }

        String decodedCredentials;
        try {
            decodedCredentials = new String(
                    Base64.getDecoder().decode(encodedCredentials),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException exception) {
            throw unauthorized("Basic credentials are malformed.");
        }

        int delimiterIndex = decodedCredentials.indexOf(':');
        if (delimiterIndex < 0 || delimiterIndex != decodedCredentials.lastIndexOf(':')) {
            throw unauthorized("Basic credentials are malformed.");
        }

        String clientId = decodedCredentials.substring(0, delimiterIndex);
        String clientSecret = decodedCredentials.substring(delimiterIndex + 1);
        if (clientId.isBlank()) {
            throw unauthorized("clientId must not be blank");
        }
        if (clientSecret.isBlank()) {
            throw unauthorized("clientSecret must not be blank");
        }

        return new ClientCredentials(clientId, clientSecret);
    }

    private static boolean isUploadRoute(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.isBlank()) {
            return servletPath.startsWith("/api/upload/");
        }

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (requestUri == null || requestUri.isBlank()) {
            return false;
        }
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length()).startsWith("/api/upload/");
        }
        return requestUri.startsWith("/api/upload/");
    }

    private static UnauthorizedApiClientException unauthorized(String message) {
        return new UnauthorizedApiClientException(message, null, "UNAUTHORIZED_API_CLIENT");
    }

    private record ClientCredentials(String clientId, String clientSecret) {
    }
}
