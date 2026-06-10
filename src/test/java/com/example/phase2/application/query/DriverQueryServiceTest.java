package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DriverQueryServiceTest {

    private DriverJpaRepository driverJpaRepository;
    private SessionQueryService sessionQueryService;
    private AnalyticsQueryService analyticsQueryService;
    private DriverQueryService driverQueryService;

    @BeforeEach
    void setUp() {
        driverJpaRepository = mock(DriverJpaRepository.class);
        sessionQueryService = mock(SessionQueryService.class);
        analyticsQueryService = mock(AnalyticsQueryService.class);
        driverQueryService = new DriverQueryService(
                driverJpaRepository,
                new AdminResponseMapper(),
                sessionQueryService,
                analyticsQueryService
        );
    }

    @Test
    void getDriversReturnsPagedDriverCards() {
        DriverEntity driverEntity = buildDriverEntity("driver-1", "Aylin Driver", DriverAccountStatus.ACTIVE);
        Page<DriverEntity> driverPage = new PageImpl<>(List.of(driverEntity), PageRequest.of(0, 10), 1);

        when(driverJpaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(driverPage);

        PagedResponse<DriverCardResponse> response = driverQueryService.getDrivers(
                0,
                10,
                "driverName",
                "asc",
                DriverAccountStatus.ACTIVE,
                "aylin"
        );

        assertEquals(1, response.getItems().size());
        assertEquals("driver-1", response.getItems().get(0).getDriverId());
        assertEquals("Aylin Driver", response.getItems().get(0).getDriverName());
        assertEquals(UserRole.DRIVER, response.getItems().get(0).getRole());
        assertEquals(DriverAccountStatus.ACTIVE, response.getItems().get(0).getAccountStatus());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalItems());
        assertEquals(1, response.getTotalPages());
        assertFalse(response.hasNext());
        assertFalse(response.hasPrevious());
    }

    @Test
    void getDriverDetailReturnsAdministrativeDetailView() {
        DriverEntity driverEntity = buildDriverEntity("driver-1", "Aylin Driver", DriverAccountStatus.SUSPENDED);
        when(driverJpaRepository.findById("driver-1")).thenReturn(Optional.of(driverEntity));

        DriverDetailResponse response = driverQueryService.getDriverDetail("driver-1");

        assertEquals("driver-1", response.getDriverId());
        assertEquals("Aylin Driver", response.getDriverName());
        assertEquals("aylin@example.com", response.getEmail());
        assertEquals(DriverAccountStatus.SUSPENDED, response.getAccountStatus());
        assertEquals(UserRole.DRIVER, response.getRole());
    }

    @Test
    void getDriverDetailFailsThroughNotFoundPathWhenDriverDoesNotExist() {
        when(driverJpaRepository.findById("missing-driver")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverQueryService.getDriverDetail("missing-driver"));
    }

    @Test
    void existsByDriverIdDelegatesToRepository() {
        when(driverJpaRepository.existsById("driver-1")).thenReturn(true);

        boolean exists = driverQueryService.existsByDriverId("driver-1");

        assertTrue(exists);
        verify(driverJpaRepository).existsById("driver-1");
    }

    private DriverEntity buildDriverEntity(String id, String name, DriverAccountStatus status) {
        return new DriverEntity(
                id,
                name,
                "aylin@example.com",
                "$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M9G6N7B2Lr7z7k0GQzQ0jrISFRCW",
                UserRole.DRIVER,
                status,
                97.5d,
                48,
                1250.25d,
                1699990000000L,
                1699995000000L,
                status == DriverAccountStatus.DEACTIVATED ? 1699996000000L : null
        );
    }
}
