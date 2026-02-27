package com.moonseo.security;

import lombok.Getter;
import java.util.Set;

@Getter
public class AuthUser {
    private final Long userId;
    private final Set<String> roles;

    public AuthUser(Long userId, Set<String> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
