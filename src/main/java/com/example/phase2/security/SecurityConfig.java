package com.example.phase2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";

    private static final String ADMIN_LOGIN_PAGE_PATH = "/admin/login";
    private static final String ADMIN_LOGIN_PROCESSING_PATH = "/admin/login";
    private static final String ADMIN_LOGOUT_PATH = "/admin/logout";
    private static final String ADMIN_LOGOUT_SUCCESS_PATH = "/admin/login?logout";
    private static final String ADMIN_API_LOGIN_PATH = "/api/admin/auth/login";
    private static final String ADMIN_API_LOGOUT_PATH = "/api/admin/auth/logout";

    private static final String ADMIN_PAGE_ROUTE_PATTERN = "/admin/**";
    private static final String ADMIN_API_ROUTE_PATTERN = "/api/admin/**";
    private static final String DRIVER_API_ROUTE_PATTERN = "/api/driver/**";
    private static final String DRIVER_LOGIN_PATH = "/api/driver/auth/login";
    private static final String MACHINE_UPLOAD_ROUTE_PATTERN = "/api/upload/**";
    private static final String ACTUATOR_HEALTH_PATH = "/actuator/health";
    private static final String ACTUATOR_INFO_PATH = "/actuator/info";
    private static final String ACTUATOR_PROMETHEUS_PATH = "/actuator/prometheus";

    private final AdminUserDetailsService adminUserDetailsService;
    private final ApiClientAuthFilter apiClientAuthFilter;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(
            AdminUserDetailsService adminUserDetailsService,
            ApiClientAuthFilter apiClientAuthFilter,
            AuthenticationEntryPointHandler authenticationEntryPointHandler,
            PasswordEncoder passwordEncoder
    ) {
        this.adminUserDetailsService = adminUserDetailsService;
        this.apiClientAuthFilter = apiClientAuthFilter;
        this.authenticationEntryPointHandler = authenticationEntryPointHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestMatcher adminApiLoginRoute =
                request -> matchesPathAndMethod(request, ADMIN_API_LOGIN_PATH, HttpMethod.POST);
        RequestMatcher adminApiLogoutRoute =
                request -> matchesPathAndMethod(request, ADMIN_API_LOGOUT_PATH, HttpMethod.POST);
        RequestMatcher driverLoginRoute =
                request -> matchesPathAndMethod(request, DRIVER_LOGIN_PATH, HttpMethod.POST);
        RequestMatcher adminApiRoutes = request -> matchesPrefix(request, "/api/admin/");
        RequestMatcher driverApiRoutes = request -> matchesPrefix(request, "/api/driver/");
        RequestMatcher uploadRoutes = request -> matchesPrefix(request, "/api/upload/");
        RequestMatcher apiRoutes = new OrRequestMatcher(adminApiRoutes, driverApiRoutes, uploadRoutes);

        http
                .authenticationProvider(adminAuthenticationProvider())
                .addFilterBefore(apiClientAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        uploadRoutes,
                        driverLoginRoute,
                        adminApiLoginRoute,
                        adminApiLogoutRoute,
                        driverApiRoutes
                ))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sessionFixation -> sessionFixation.migrateSession())
                )
                .requestCache(requestCache -> requestCache.requestCache(new NullRequestCache()))
                .securityContext(Customizer.withDefaults())
                .rememberMe(AbstractHttpConfigurer::disable)
                .formLogin(formLogin -> formLogin
                        .loginPage(ADMIN_LOGIN_PAGE_PATH)
                        .loginProcessingUrl(ADMIN_LOGIN_PROCESSING_PATH)
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl(ADMIN_LOGIN_PAGE_PATH + "?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(ADMIN_LOGOUT_PATH)
                        .logoutSuccessUrl(ADMIN_LOGOUT_SUCCESS_PATH)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(authenticationEntryPointHandler, apiRoutes)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                ADMIN_LOGIN_PAGE_PATH,
                                ADMIN_LOGIN_PROCESSING_PATH,
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/favicon.ico",
                                ACTUATOR_HEALTH_PATH,
                                ACTUATOR_INFO_PATH,
                                ACTUATOR_PROMETHEUS_PATH
                        ).permitAll()
                        .requestMatchers(adminApiLoginRoute, driverLoginRoute).permitAll()
                        .requestMatchers(MACHINE_UPLOAD_ROUTE_PATTERN).hasAuthority("ROLE_API_CLIENT")
                        .requestMatchers(DRIVER_API_ROUTE_PATTERN).hasRole("DRIVER")
                        .requestMatchers(ADMIN_API_ROUTE_PATTERN).hasRole(ADMIN_ROLE)
                        .requestMatchers(ADMIN_PAGE_ROUTE_PATTERN).hasRole(ADMIN_ROLE)
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(adminUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private static boolean matchesPrefix(jakarta.servlet.http.HttpServletRequest request, String prefix) {
        String requestPath = resolveRequestPath(request);
        return requestPath != null && requestPath.startsWith(prefix);
    }

    private static boolean matchesPathAndMethod(
            jakarta.servlet.http.HttpServletRequest request,
            String path,
            HttpMethod method
    ) {
        return method.matches(request.getMethod()) && path.equals(resolveRequestPath(request));
    }

    private static String resolveRequestPath(jakarta.servlet.http.HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.isBlank()) {
            return servletPath;
        }

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (requestUri == null || requestUri.isBlank()) {
            return requestUri;
        }
        if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }
}
