package com.example.phase2.api.controller;

import com.example.phase2.api.admin.request.CreateDriverRequest;
import com.example.phase2.api.admin.request.DeactivateDriverRequest;
import com.example.phase2.api.admin.response.DriverCardResponse;
import com.example.phase2.api.admin.response.DriverDetailResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.DriverLifecycleService;
import com.example.phase2.application.query.DriverQueryService;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class DriverAdminApiController {

    private final DriverQueryService driverQueryService;
    private final DriverLifecycleService driverLifecycleService;

    public DriverAdminApiController(
            DriverQueryService driverQueryService,
            DriverLifecycleService driverLifecycleService
    ) {
        this.driverQueryService = driverQueryService;
        this.driverLifecycleService = driverLifecycleService;
    }

    @GetMapping("/drivers")
    public ResponseEntity<PagedResponse<DriverCardResponse>> getDrivers(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) DriverAccountStatus accountStatus,
            @RequestParam(required = false) String searchTerm
    ) {
        return ResponseEntity.ok(driverQueryService.getDrivers(
                page,
                size,
                sortBy,
                sortDirection,
                accountStatus,
                searchTerm
        ));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<DriverDetailResponse> getDriverDetail(@PathVariable String driverId) {
        return ResponseEntity.ok(driverQueryService.getDriverDetail(driverId));
    }

    @PostMapping("/drivers")
    public ResponseEntity<DriverDetailResponse> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        try {
            String driverId = driverLifecycleService.createDriver(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    UserRole.DRIVER
            ).getId();
            return ResponseEntity.ok(driverQueryService.getDriverDetail(driverId));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/drivers/{driverId}/deactivation")
    public ResponseEntity<DriverDetailResponse> deactivateDriver(
            @PathVariable String driverId,
            @Valid @RequestBody DeactivateDriverRequest request
    ) {
        driverLifecycleService.deactivateDriver(driverId, request.getReason());
        return ResponseEntity.ok(driverQueryService.getDriverDetail(driverId));
    }
}
