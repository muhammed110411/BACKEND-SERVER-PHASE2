package com.example.phase2.api.admin.request;

import jakarta.validation.constraints.NotBlank;

public class DeactivateDriverRequest {

    @NotBlank
    private String reason;

    public DeactivateDriverRequest() {
    }

    public DeactivateDriverRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
