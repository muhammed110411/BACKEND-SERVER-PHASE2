package com.example.phase2.api.controller;

import com.example.phase2.api.admin.request.CreateVehicleRequest;
import com.example.phase2.api.admin.request.UpdateVehicleRequest;
import com.example.phase2.api.admin.response.VehicleResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.command.VehicleManagementService;
import com.example.phase2.application.query.VehicleQueryService;
import com.example.phase2.domain.enums.VehicleStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class VehicleAdminApiController {

    private final VehicleQueryService vehicleQueryService;
    private final VehicleManagementService vehicleManagementService;

    public VehicleAdminApiController(
            VehicleQueryService vehicleQueryService,
            VehicleManagementService vehicleManagementService
    ) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleManagementService = vehicleManagementService;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<PagedResponse<VehicleResponse>> getVehicles(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam String sortDirection,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String searchTerm
    ) {
        return ResponseEntity.ok(vehicleQueryService.getVehicles(
                page,
                size,
                sortBy,
                sortDirection,
                status,
                searchTerm
        ));
    }

    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable String vehicleId) {
        return ResponseEntity.ok(vehicleQueryService.getVehicleById(vehicleId));
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        String vehicleId = vehicleManagementService.createVehicle(
                request.getDisplayName(),
                request.getStatus()
        ).getId();
        return ResponseEntity.ok(vehicleQueryService.getVehicleById(vehicleId));
    }

    @PutMapping("/vehicles/{vehicleId}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable String vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        vehicleManagementService.updateVehicle(vehicleId, request.getDisplayName(), request.getStatus());
        return ResponseEntity.ok(vehicleQueryService.getVehicleById(vehicleId));
    }
}
