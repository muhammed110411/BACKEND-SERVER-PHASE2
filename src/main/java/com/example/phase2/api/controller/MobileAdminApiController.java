package com.example.phase2.api.controller;

import com.example.phase2.api.admin.response.mobile.MobileAdminDashboardResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminDriverDetailResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminDriverSummaryResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionDetailResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionSummaryResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminVehicleSummaryResponse;
import com.example.phase2.api.common.response.PagedResponse;
import com.example.phase2.application.query.MobileAdminQueryService;
import com.example.phase2.domain.enums.DriverAccountStatus;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import com.example.phase2.domain.enums.UploadProcessingStatus;
import com.example.phase2.domain.enums.VehicleStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/mobile")
public class MobileAdminApiController {

    private final MobileAdminQueryService mobileAdminQueryService;

    public MobileAdminApiController(MobileAdminQueryService mobileAdminQueryService) {
        this.mobileAdminQueryService = mobileAdminQueryService;
    }

    @GetMapping("/drivers")
    public ResponseEntity<PagedResponse<MobileAdminDriverSummaryResponse>> getDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) DriverAccountStatus accountStatus,
            @RequestParam(required = false) String searchTerm
    ) {
        return ResponseEntity.ok(mobileAdminQueryService.getDrivers(
                page,
                size,
                sortBy,
                sortDirection,
                accountStatus,
                searchTerm
        ));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<MobileAdminDriverDetailResponse> getDriverDetail(@PathVariable String driverId) {
        return ResponseEntity.ok(mobileAdminQueryService.getDriverDetail(driverId));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<PagedResponse<MobileAdminVehicleSummaryResponse>> getVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String searchTerm
    ) {
        return ResponseEntity.ok(mobileAdminQueryService.getVehicles(
                page,
                size,
                sortBy,
                sortDirection,
                status,
                searchTerm
        ));
    }

    @GetMapping("/sessions")
    public ResponseEntity<PagedResponse<MobileAdminSessionSummaryResponse>> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String driverId,
            @RequestParam(required = false) String vehicleId,
            @RequestParam(required = false) SessionEndStatus status,
            @RequestParam(required = false) SessionValidity validity,
            @RequestParam(required = false) UploadProcessingStatus uploadProcessingStatus
    ) {
        return ResponseEntity.ok(mobileAdminQueryService.getSessions(
                page,
                size,
                sortBy,
                sortDirection,
                driverId,
                vehicleId,
                status,
                validity,
                uploadProcessingStatus
        ));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<MobileAdminSessionDetailResponse> getSessionDetail(@PathVariable String sessionId) {
        return ResponseEntity.ok(mobileAdminQueryService.getSessionDetail(sessionId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<MobileAdminDashboardResponse> getDashboard(
            @RequestParam(required = false) Long fromTimestamp,
            @RequestParam(required = false) Long toTimestamp
    ) {
        return ResponseEntity.ok(mobileAdminQueryService.getDashboard(fromTimestamp, toTimestamp));
    }
}
