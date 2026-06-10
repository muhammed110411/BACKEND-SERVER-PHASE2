package com.example.phase2.web.controller;

import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminShellPageController {

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final AdminUserPersistenceMapper adminUserPersistenceMapper;

    public AdminShellPageController(
            AdminUserJpaRepository adminUserJpaRepository,
            AdminUserPersistenceMapper adminUserPersistenceMapper
    ) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.adminUserPersistenceMapper = adminUserPersistenceMapper;
    }

    @GetMapping("/settings")
    public String getSettingsPage() {
        return "admin/settings";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : null;

        if (username != null) {
            adminUserJpaRepository.findByUsername(username)
                    .map(adminUserPersistenceMapper::toDomain)
                    .ifPresent(adminUser -> addProfileAttributes(model, adminUser));
        }

        return "admin/profile";
    }

    private void addProfileAttributes(Model model, AdminUser adminUser) {
        model.addAttribute("adminId", adminUser.getId());
        model.addAttribute("adminRole", adminUser.getRole());
        model.addAttribute("adminAccountStatus", adminUser.getAccountStatus());
        model.addAttribute("adminCreatedAt", adminUser.getCreatedAt());
        model.addAttribute("adminUpdatedAt", adminUser.getUpdatedAt());
        model.addAttribute("adminLastLoginAt", adminUser.getLastLoginAt());
        model.addAttribute("adminDeactivatedAt", adminUser.getDeactivatedAt());
    }
}
