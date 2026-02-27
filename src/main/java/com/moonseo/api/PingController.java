package com.moonseo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    // 공개 엔드포인트: permitAll
    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }

    // 인증 필요 엔드포인트: anyRequest().authenticated()
    // 토큰 없으면 401
    // 토큰 있으면 200
    @GetMapping("api/ping-auth")
    public String pingAuth() {
        return "pong-auth";
    }
}
