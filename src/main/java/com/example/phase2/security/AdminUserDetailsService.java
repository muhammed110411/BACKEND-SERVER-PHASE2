package com.example.phase2.security;

import com.example.phase2.domain.enums.AdminAccountStatus;
import com.example.phase2.domain.enums.UserRole;
import com.example.phase2.domain.model.AdminUser;
import com.example.phase2.persistence.mapper.AdminUserPersistenceMapper;
import com.example.phase2.persistence.repository.AdminUserJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final AdminUserPersistenceMapper adminUserPersistenceMapper;

    public AdminUserDetailsService(
            AdminUserJpaRepository adminUserJpaRepository,
            AdminUserPersistenceMapper adminUserPersistenceMapper
    ) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.adminUserPersistenceMapper = adminUserPersistenceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserJpaRepository.findByUsername(username)
                .map(adminUserPersistenceMapper::toDomain)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found."));

        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new UsernameNotFoundException("Admin user not found.");
        }

        return toUserDetails(adminUser);
    }

    protected UserDetails toUserDetails(AdminUser adminUser) {
        Objects.requireNonNull(adminUser, "adminUser must not be null");

        AdminAccountStatus accountStatus = adminUser.getAccountStatus();

        return User.withUsername(adminUser.getUsername())
                .password(adminUser.getPasswordHash())
                .authorities(adminAuthorities(adminUser.getRole()))
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(accountStatus == AdminAccountStatus.SUSPENDED)
                .disabled(accountStatus == AdminAccountStatus.INACTIVE
                        || accountStatus == AdminAccountStatus.DEACTIVATED)
                .build();
    }

    private static Collection<? extends GrantedAuthority> adminAuthorities(UserRole role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
