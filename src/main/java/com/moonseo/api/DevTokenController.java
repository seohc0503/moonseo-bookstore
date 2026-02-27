package com.moonseo.api;

import com.moonseo.security.jwt.JwtProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Profile("local")
@RestController
@RequestMapping("/api/dev")
public class DevTokenController {

    private final JwtProvider jwtProvider;

    public DevTokenController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    // local 전용 임시 토큰 발급
    @GetMapping("/token")
    public String token(@RequestParam long userId, @RequestParam String role) {
        return jwtProvider.createAccessToken(userId, Set.of(role));
    }
}
