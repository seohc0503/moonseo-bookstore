package com.moonseo.api.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminPingController {

    // hasRole("ADMIN")
    // USER 토큰: 403
    // ADMIN 토큰: 200
    @GetMapping("/api/admin/ping")
    public String adminPing() {
        return "admin-pong";
    }
}
