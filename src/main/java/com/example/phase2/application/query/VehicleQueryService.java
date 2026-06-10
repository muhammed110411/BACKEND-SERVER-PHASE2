package com.example.phase2.application.query;

import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.mapper.AdminResponseMapper;
import com.example.phase2.domain.enums.VehicleStatus;
import com.example.phase2.domain.model.Vehicle;
import com.example.phase2.exception.ResourceNotFoundException;
import com.example.phase2.persistence.entity.VehicleEntity;
import com.example.phase2.persistence.repository.VehicleJpaRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
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
public class VehicleQueryService {

    private static final String VEHICLE_RESOURCE_TYPE = "vehicle";
    private static final String ID_LOOKUP_FIELD = "id";
    private static final String DEFAULT_SORT_BY = "updatedAt";

    private final VehicleJpaRepository vehicleJpaRepository;
    private final AdminResponseMapper adminResponseMapper;

    public VehicleQueryService(
            VehicleJpaRepository vehicleJpaRepository,
            AdminResponseMapper adminResponseMapper
    ) {
        this.vehicleJpaRepository = vehicleJpaRepository;
        this.adminResponseMapper = adminResponseMapper;
    }

    public PagedResponse<VehicleResponse> getVehicles(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            VehicleStatus status,
            String searchTerm
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                buildSort(sortBy, sortDirection)
        );

        Page<VehicleResponse> resultPage = vehicleJpaRepository.findAll(
                        buildVehicleSpecification(status, searchTerm),
                        pageable
                )
                .map(this::toVehicle)
                .map(adminResponseMapper::toVehicleResponse);

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

    public VehicleResponse getVehicleById(String vehicleId) {
        String requiredVehicleId = requireText(vehicleId, "vehicleId");
        VehicleEntity vehicleEntity = vehicleJpaRepository.findById(requiredVehicleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found: " + requiredVehicleId,
                        VEHICLE_RESOURCE_TYPE,
                        requiredVehicleId,
                        ID_LOOKUP_FIELD
                ));

        return adminResponseMapper.toVehicleResponse(toVehicle(vehicleEntity));
    }

    public boolean existsByVehicleId(String vehicleId) {
        return vehicleJpaRepository.existsById(requireText(vehicleId, "vehicleId"));
    }

    private Specification<VehicleEntity> buildVehicleSpecification(
            VehicleStatus status,
            String searchTerm
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (searchTerm != null && !searchTerm.isBlank()) {
                String normalizedSearch = "%" + searchTerm.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("id")), normalizedSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("displayName")), normalizedSearch)
                ));
            }

            return predicates.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, resolveSortProperty(sortBy));
    }

    private String resolveSortProperty(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return DEFAULT_SORT_BY;
        }

        return switch (sortBy.trim()) {
            case "id", "displayName", "status", "createdAt", "updatedAt", "deactivatedAt" -> sortBy.trim();
            case "vehicleId" -> "id";
            default -> DEFAULT_SORT_BY;
        };
    }

    private Vehicle toVehicle(VehicleEntity entity) {
        return new Vehicle(
                entity.getId(),
                entity.getDisplayName(),
                entity.getStatus(),
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
