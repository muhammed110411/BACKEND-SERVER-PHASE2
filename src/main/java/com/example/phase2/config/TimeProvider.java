package com.example.phase2.config;

/**
 * Provides the canonical backend current time as UTC epoch milliseconds.
 */
public interface TimeProvider {

    long nowEpochMillis();
}
