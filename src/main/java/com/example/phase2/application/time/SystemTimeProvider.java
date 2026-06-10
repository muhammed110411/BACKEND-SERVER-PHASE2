package com.example.phase2.application.time;

import com.example.phase2.config.TimeProvider;

import java.time.Clock;

import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider {

    private final Clock clock;

    public SystemTimeProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public long nowEpochMillis() {
        return clock.millis();
    }
}
