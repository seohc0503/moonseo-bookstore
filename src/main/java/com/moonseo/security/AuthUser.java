package com.moonseo.security;

import java.util.Set;

public record AuthUser(
        Long userId,
        Set<String> roles
) {
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}