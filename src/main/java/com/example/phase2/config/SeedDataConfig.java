package com.example.phase2.config;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class SeedDataConfig {

    private static final String DEFAULT_ADMIN_USERNAME = "ops-admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "Password1";
    private static final String LOCAL_DEV_ADMIN_USERNAME = "admin";
    private static final String LOCAL_DEV_ADMIN_PASSWORD = "admin123";
    private static final List<String> BASELINE_VEHICLE_DISPLAY_NAMES = List.of("Admin Car");

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final VehicleJpaRepository vehicleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final TimeProvider timeProvider;
    private final IdGenerator idGenerator;
    private final Environment environment;
    private final DemoFleetSeedService demoFleetSeedService;

    public SeedDataConfig(
            AdminUserJpaRepository adminUserJpaRepository,
            VehicleJpaRepository vehicleJpaRepository,
            PasswordEncoder passwordEncoder,
            TimeProvider timeProvider,
            IdGenerator idGenerator,
            Environment environment,
            DemoFleetSeedService demoFleetSeedService
    ) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.timeProvider = timeProvider;
        this.idGenerator = idGenerator;
        this.environment = environment;
        this.demoFleetSeedService = demoFleetSeedService;
    }

    @Bean
    public CommandLineRunner seedDataRunner() {
        return args -> {
            seedDefaultAdminUser();
            seedBaselineVehicles();
            demoFleetSeedService.seedDemoFleetWhenEnabled(isDemoSeedEnabled());
        };
    }

    @Transactional
    private void seedDefaultAdminUser() {
        SeededAdminCredentials seededAdminCredentials = resolveSeededAdminCredentials();
        if (adminUserJpaRepository.existsByUsername(seededAdminCredentials.username())) {
            return;
        }

        long seededAt = timeProvider.nowEpochMillis();
        AdminUserEntity adminUserEntity = new AdminUserEntity(
                requireGeneratedId(idGenerator.newId(), "adminUserId"),
                seededAdminCredentials.username(),
                passwordEncoder.encode(seededAdminCredentials.rawPassword()),
                AdminAccountStatus.ACTIVE,
                UserRole.ADMIN,
                seededAt,
                seededAt,
                null,
                null
        );
        adminUserJpaRepository.save(adminUserEntity);
    }

    @Transactional
    private void seedBaselineVehicles() {
        for (String displayName : BASELINE_VEHICLE_DISPLAY_NAMES) {
            if (vehicleJpaRepository.existsByDisplayName(displayName)) {
                continue;
            }

            long seededAt = timeProvider.nowEpochMillis();
            VehicleEntity vehicleEntity = new VehicleEntity(
                    requireGeneratedId(idGenerator.newId(), "vehicleId"),
                    displayName,
                    VehicleStatus.AVAILABLE,
                    seededAt,
                    seededAt,
                    null
            );
            vehicleJpaRepository.save(vehicleEntity);
        }
    }

    private static String requireGeneratedId(String generatedId, String fieldName) {
        if (generatedId == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (generatedId.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return generatedId;
    }

    private SeededAdminCredentials resolveSeededAdminCredentials() {
        if (environment.acceptsProfiles(Profiles.of("local", "dev"))) {
            return new SeededAdminCredentials(LOCAL_DEV_ADMIN_USERNAME, LOCAL_DEV_ADMIN_PASSWORD);
        }
        return new SeededAdminCredentials(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
    }

    private boolean isDemoSeedEnabled() {
        return Boolean.parseBoolean(environment.getProperty("app.seed.demo.enabled", "false"));
    }

    private record SeededAdminCredentials(String username, String rawPassword) {
    }
}
