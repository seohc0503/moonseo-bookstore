package com.moonseo.api;

import com.moonseo.common.exception.ApiException;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.security.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserGuardTestController {

    @GetMapping("/api/users/{userId}/profile")
    public Map<String, Object> getProfile(@PathVariable Long userId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        if (!authUser.userId().equals(userId)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
        return Map.of("userId", userId, "ok", true);
    }
}
