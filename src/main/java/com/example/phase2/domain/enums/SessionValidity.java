package com.example.phase2.domain.enums;

public enum SessionValidity {
    VALID,
    INVALID;

    public boolean isValid() {
        return this == VALID;
    }
}
