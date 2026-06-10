package com.example.phase2.api.driver.response;

public class DriverSessionPenaltiesResponse {

    private double continuous;
    private double event;
    private double escalation;

    public DriverSessionPenaltiesResponse() {
    }

    public DriverSessionPenaltiesResponse(double continuous, double event, double escalation) {
        this.continuous = continuous;
        this.event = event;
        this.escalation = escalation;
    }

    public double getContinuous() {
        return continuous;
    }

    public void setContinuous(double continuous) {
        this.continuous = continuous;
    }

    public double getEvent() {
        return event;
    }

    public void setEvent(double event) {
        this.event = event;
    }

    public double getEscalation() {
        return escalation;
    }

    public void setEscalation(double escalation) {
        this.escalation = escalation;
    }
}
