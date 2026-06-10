package com.example.phase2.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.phase2.config.TimeProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

class AuthenticationEntryPointHandlerTest {

    @Test
    void commenceWritesCanonicalUnauthorizedJsonResponse() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        TimeProvider timeProvider = mock(TimeProvider.class);
        when(timeProvider.nowEpochMillis()).thenReturn(1712345678901L);

        AuthenticationEntryPointHandler authenticationEntryPointHandler =
                new AuthenticationEntryPointHandler(objectMapper, timeProvider);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/upload/sessions");
        request.setQueryString("token=secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authenticationEntryPointHandler.commence(
                request,
                response,
                new BadCredentialsException("raw details must not leak")
        );

        assertEquals(401, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals(
                "{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required\","
                        + "\"status\":401,\"timestamp\":1712345678901,"
                        + "\"path\":\"/api/upload/sessions\",\"fieldErrors\":[]}",
                response.getContentAsString()
        );
    }
}
