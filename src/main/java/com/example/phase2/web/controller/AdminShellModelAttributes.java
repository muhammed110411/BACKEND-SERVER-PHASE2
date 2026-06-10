package com.example.phase2.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {
        AdminPageController.class,
        AdminShellPageController.class,
        DriverPageController.class,
        SessionPageController.class,
        VehiclePageController.class
})
public class AdminShellModelAttributes {

    @ModelAttribute("adminUsername")
    public String adminUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return "Admin";
        }
        return authentication.getName();
    }
}
