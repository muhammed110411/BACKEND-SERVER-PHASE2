package com.example.phase2.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.persistence.entity.AdminUserEntity;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

class SeedDataConfigTest {

    private AdminUserJpaRepository adminUserJpaRepository;
    private VehicleJpaRepository vehicleJpaRepository;
    private PasswordEncoder passwordEncoder;
    private TimeProvider timeProvider;
    private IdGenerator idGenerator;
    private Environment environment;
    private DemoFleetSeedService demoFleetSeedService;
    private SeedDataConfig seedDataConfig;

    @BeforeEach
    void setUp() {
        adminUserJpaRepository = org.mockito.Mockito.mock(AdminUserJpaRepository.class);
        vehicleJpaRepository = org.mockito.Mockito.mock(VehicleJpaRepository.class);
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        timeProvider = org.mockito.Mockito.mock(TimeProvider.class);
        idGenerator = org.mockito.Mockito.mock(IdGenerator.class);
        environment = org.mockito.Mockito.mock(Environment.class);
        demoFleetSeedService = org.mockito.Mockito.mock(DemoFleetSeedService.class);
        seedDataConfig = new SeedDataConfig(
                adminUserJpaRepository,
                vehicleJpaRepository,
                passwordEncoder,
                timeProvider,
                idGenerator,
                environment,
                demoFleetSeedService
        );
    }

    @Test
    void seedDataRunnerCreatesApprovedBootstrapRecordsWhenMissing() throws Exception {
        when(environment.acceptsProfiles(org.mockito.ArgumentMatchers.any(org.springframework.core.env.Profiles.class)))
                .thenReturn(false);
        when(environment.getProperty("app.seed.demo.enabled", "false")).thenReturn("false");
        when(adminUserJpaRepository.existsByUsername("ops-admin")).thenReturn(false);
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded-admin-password");
        when(timeProvider.nowEpochMillis()).thenReturn(1700001234567L, 1700001234568L);
        when(idGenerator.newId()).thenReturn("admin-seed-id", "vehicle-seed-id");
        when(adminUserJpaRepository.save(any(AdminUserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleJpaRepository.save(any(VehicleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = seedDataConfig.seedDataRunner();
        runner.run();

        org.mockito.ArgumentCaptor<AdminUserEntity> adminCaptor =
                org.mockito.ArgumentCaptor.forClass(AdminUserEntity.class);
        org.mockito.ArgumentCaptor<VehicleEntity> vehicleCaptor =
                org.mockito.ArgumentCaptor.forClass(VehicleEntity.class);

        verify(adminUserJpaRepository).save(adminCaptor.capture());
        verify(vehicleJpaRepository).save(vehicleCaptor.capture());
        verify(passwordEncoder).encode("Password1");
        verify(demoFleetSeedService).seedDemoFleetWhenEnabled(false);

        AdminUserEntity adminUserEntity = adminCaptor.getValue();
        assertEquals("admin-seed-id", adminUserEntity.getId());
        assertEquals("ops-admin", adminUserEntity.getUsername());
        assertEquals("encoded-admin-password", adminUserEntity.getPasswordHash());
        assertEquals(AdminAccountStatus.ACTIVE, adminUserEntity.getAccountStatus());
        assertEquals(UserRole.ADMIN, adminUserEntity.getRole());
        assertEquals(1700001234567L, adminUserEntity.getCreatedAt());
        assertEquals(1700001234567L, adminUserEntity.getUpdatedAt());
        assertNull(adminUserEntity.getLastLoginAt());
        assertNull(adminUserEntity.getDeactivatedAt());

        VehicleEntity vehicleEntity = vehicleCaptor.getValue();
        assertEquals("vehicle-seed-id", vehicleEntity.getId());
        assertEquals("Admin Car", vehicleEntity.getDisplayName());
        assertEquals(VehicleStatus.AVAILABLE, vehicleEntity.getStatus());
        assertEquals(1700001234568L, vehicleEntity.getCreatedAt());
        assertEquals(1700001234568L, vehicleEntity.getUpdatedAt());
        assertNull(vehicleEntity.getDeactivatedAt());
    }

    @Test
    void seedDataRunnerSkipsBootstrapRecordsWhenTheyAlreadyExist() throws Exception {
        when(environment.acceptsProfiles(org.mockito.ArgumentMatchers.any(org.springframework.core.env.Profiles.class)))
                .thenReturn(false);
        when(environment.getProperty("app.seed.demo.enabled", "false")).thenReturn("false");
        when(adminUserJpaRepository.existsByUsername("ops-admin")).thenReturn(true);
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(true);

        CommandLineRunner runner = seedDataConfig.seedDataRunner();
        runner.run();

        verify(adminUserJpaRepository, never()).save(any(AdminUserEntity.class));
        verify(vehicleJpaRepository, never()).save(any(VehicleEntity.class));
        verify(passwordEncoder, never()).encode(any());
        verify(timeProvider, never()).nowEpochMillis();
        verify(idGenerator, never()).newId();
        verify(demoFleetSeedService).seedDemoFleetWhenEnabled(false);
    }

    @Test
    void seedDataRunnerRemainsIdempotentAcrossRepeatedStartupInvocations() throws Exception {
        when(environment.acceptsProfiles(org.mockito.ArgumentMatchers.any(org.springframework.core.env.Profiles.class)))
                .thenReturn(false);
        when(environment.getProperty("app.seed.demo.enabled", "false")).thenReturn("false");
        when(adminUserJpaRepository.existsByUsername("ops-admin")).thenReturn(false, true);
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(false, true);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded-admin-password");
        when(timeProvider.nowEpochMillis()).thenReturn(1700001234567L, 1700001234568L);
        when(idGenerator.newId()).thenReturn("admin-seed-id", "vehicle-seed-id");
        when(adminUserJpaRepository.save(any(AdminUserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleJpaRepository.save(any(VehicleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = seedDataConfig.seedDataRunner();
        runner.run();
        runner.run();

        verify(adminUserJpaRepository, times(2)).existsByUsername("ops-admin");
        verify(vehicleJpaRepository, times(2)).existsByDisplayName("Admin Car");
        verify(adminUserJpaRepository, times(1)).save(any(AdminUserEntity.class));
        verify(vehicleJpaRepository, times(1)).save(any(VehicleEntity.class));
        verify(passwordEncoder, times(1)).encode("Password1");
        verify(timeProvider, times(2)).nowEpochMillis();
        verify(idGenerator, times(2)).newId();
        verify(demoFleetSeedService, times(2)).seedDemoFleetWhenEnabled(false);
    }

    @Test
    void seedDataRunnerUsesKnownDevAdminCredentialsForLocalProfile() throws Exception {
        when(environment.acceptsProfiles(org.mockito.ArgumentMatchers.any(org.springframework.core.env.Profiles.class)))
                .thenReturn(true);
        when(environment.getProperty("app.seed.demo.enabled", "false")).thenReturn("true");
        when(adminUserJpaRepository.existsByUsername("admin")).thenReturn(false);
        when(vehicleJpaRepository.existsByDisplayName("Admin Car")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-local-admin-password");
        when(timeProvider.nowEpochMillis()).thenReturn(1700001234567L, 1700001234568L);
        when(idGenerator.newId()).thenReturn("admin-seed-id", "vehicle-seed-id");
        when(adminUserJpaRepository.save(any(AdminUserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleJpaRepository.save(any(VehicleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandLineRunner runner = seedDataConfig.seedDataRunner();
        runner.run();

        org.mockito.ArgumentCaptor<AdminUserEntity> adminCaptor =
                org.mockito.ArgumentCaptor.forClass(AdminUserEntity.class);

        verify(adminUserJpaRepository).save(adminCaptor.capture());
        verify(passwordEncoder).encode("admin123");
        verify(demoFleetSeedService).seedDemoFleetWhenEnabled(true);

        AdminUserEntity adminUserEntity = adminCaptor.getValue();
        assertEquals("admin", adminUserEntity.getUsername());
        assertEquals("encoded-local-admin-password", adminUserEntity.getPasswordHash());
        assertEquals(AdminAccountStatus.ACTIVE, adminUserEntity.getAccountStatus());
        assertEquals(UserRole.ADMIN, adminUserEntity.getRole());
    }
}
