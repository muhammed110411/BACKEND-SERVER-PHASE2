package com.example.phase2.api.controller;

import com.example.phase2.api.admin.response.mobile.MobileAdminDashboardResponse;
import com.example.phase2.api.admin.response.mobile.MobileAdminSessionSummaryResponse;
import com.example.phase2.application.query.MobileAdminQueryService;
import com.example.phase2.domain.enums.SessionEndStatus;
import com.example.phase2.domain.enums.SessionValidity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MobileAdminApiControllerTest {

    private MobileAdminQueryService mobileAdminQueryService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mobileAdminQueryService = mock(MobileAdminQueryService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MobileAdminApiController(mobileAdminQueryService)).build();
    }

    @Test
    void getDashboardReturnsMobileSummaryShape() throws Exception {
        MobileAdminDashboardResponse response = new MobileAdminDashboardResponse(
                3,
                2,
                4,
                8,
                87.5d,
                List.of(new MobileAdminSessionSummaryResponse(
                        "session-1",
                        "driver-1",
                        "Driver One",
                        "vehicle-1",
                        "TRK-201 Ford Transit Cargo",
                        91.2d,
                        SessionValidity.VALID,
                        SessionEndStatus.COMPLETED,
                        100L,
                        200L,
                        300L,
                        4,
                        1
                ))
        );
        when(mobileAdminQueryService.getDashboard(eq(null), eq(null))).thenReturn(response);

        mockMvc.perform(get("/api/admin/mobile/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDrivers").value(3))
                .andExpect(jsonPath("$.activeDrivers").value(2))
                .andExpect(jsonPath("$.recentSessions[0].vehicleName").value("TRK-201 Ford Transit Cargo"))
                .andExpect(jsonPath("$.recentSessions[0].status").value("COMPLETED"));

        verify(mobileAdminQueryService).getDashboard(null, null);
    }
}
