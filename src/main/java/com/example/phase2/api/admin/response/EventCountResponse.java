package com.example.phase2.api.admin.response;

import com.example.phase2.domain.enums.EventType;
import java.io.Serializable;

public class EventCountResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private EventType eventType;
    private int count;

    public EventCountResponse() {
    }

    public EventCountResponse(EventType eventType, int count) {
        this.eventType = eventType;
        this.count = count;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
