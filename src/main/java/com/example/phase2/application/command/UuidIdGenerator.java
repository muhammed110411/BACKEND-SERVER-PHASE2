package com.example.phase2.application.command;

import com.example.phase2.config.IdGenerator;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidIdGenerator implements IdGenerator {

    @Override
    public String newId() {
        return UUID.randomUUID().toString();
    }
}
