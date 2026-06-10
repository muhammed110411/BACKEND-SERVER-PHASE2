package com.example.phase2.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;
    private final String lookupField;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
        this.lookupField = null;
    }

    public ResourceNotFoundException(String message, String resourceType, String resourceId, String lookupField) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.lookupField = lookupField;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getLookupField() {
        return lookupField;
    }
}
