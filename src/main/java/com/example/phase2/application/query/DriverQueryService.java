package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.admin.response.DriverAnalyticsResponse;
import com.example.phase2.api.admin.response.SessionSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.model.Driver;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.DriverEntity;
import com.example.phase2.persistence.repository.DriverJpaRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DriverQueryService {

    private static final String DRIVER_RESOURCE_TYPE = "driver";
    private static final String ID_LOOKUP_FIELD = "id";
    private static final String DEFAULT_SORT_BY = "updatedAt";

    private final DriverJpaRepository driverJpaRepository;
    private final AdminResponseMapper adminResponseMapper;
    private final SessionQueryService sessionQueryService;
    private final AnalyticsQueryService analyticsQueryService;

    public DriverQueryService(
            DriverJpaRepository driverJpaRepository,
            AdminResponseMapper adminResponseMapper,
            SessionQueryService sessionQueryService,
            AnalyticsQueryService analyticsQueryService
    ) {
        this.driverJpaRepository = driverJpaRepository;
        this.adminResponseMapper = adminResponseMapper;
        this.sessionQueryService = sessionQueryService;
        this.analyticsQueryService = analyticsQueryService;
    }

    public PagedResponse<DriverCardResponse> getDrivers(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            DriverAccountStatus accountStatus,
            String searchTerm
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                buildSort(sortBy, sortDirection)
        );

        Page<DriverCardResponse> resultPage = driverJpaRepository.findAll(
                        buildDriverSpecification(accountStatus, searchTerm),
                        pageable
                )
                .map(this::toDriver)
                .map(adminResponseMapper::toDriverCardResponse);

        return new PagedResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.hasNext(),
                resultPage.hasPrevious()
        );
    }

    public DriverDetailResponse getDriverDetail(String driverId) {
        DriverEntity driverEntity = driverJpaRepository.findById(requireText(driverId, "driverId"))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver not found: " + driverId,
                        DRIVER_RESOURCE_TYPE,
                        driverId,
                        ID_LOOKUP_FIELD
                ));

        return adminResponseMapper.toDriverDetailResponse(toDriver(driverEntity));
    }

    public boolean existsByDriverId(String driverId) {
        return driverJpaRepository.existsById(requireText(driverId, "driverId"));
    }

    public PagedResponse<SessionSummaryResponse> getDriverSessions(
            String driverId,
            int page,
            int size,
            String sortBy,
            String sortDirection,
            SessionValidity validity,
            SessionEndStatus status,
            UploadProcessingStatus uploadProcessingStatus
    ) {
        return sessionQueryService.getSessionsByDriver(
                requireText(driverId, "driverId"),
                page,
                size,
                sortBy,
                sortDirection,
                validity,
                status,
                uploadProcessingStatus
        );
    }

    public DriverAnalyticsResponse getDriverAnalytics(
            String driverId,
            Long fromTimestamp,
            Long toTimestamp
    ) {
        return analyticsQueryService.getDriverAnalytics(
                requireText(driverId, "driverId"),
                fromTimestamp,
                toTimestamp
        );
    }

    private Specification<DriverEntity> buildDriverSpecification(
            DriverAccountStatus accountStatus,
            String searchTerm
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();

            if (accountStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("accountStatus"), accountStatus));
            }

            if (searchTerm != null && !searchTerm.isBlank()) {
                String normalizedSearch = "%" + searchTerm.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("id")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), normalizedSearch)
                ));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        String resolvedSortBy = resolveSortProperty(sortBy);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, resolvedSortBy);
    }

    private String resolveSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return DEFAULT_SORT_BY;
        }

        return switch (sortBy.trim()) {
            case "id", "name", "email", "accountStatus", "longTermReliabilityScore",
                    "totalSessions", "totalDistanceKm", "createdAt", "updatedAt", "deactivatedAt" -> sortBy.trim();
            case "driverId" -> "id";
            case "driverName" -> "name";
            default -> DEFAULT_SORT_BY;
        };
    }

    private Driver toDriver(DriverEntity entity) {
        return new Driver(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole() == null ? UserRole.DRIVER : entity.getRole(),
                entity.getAccountStatus(),
                entity.getLongTermReliabilityScore(),
                entity.getTotalSessions(),
                entity.getTotalDistanceKm(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeactivatedAt()
        );
    }

    private static String requireText(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
