package com.moonseo.infra.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Profile("local")
public class LogEmailSender implements EmailSender{
    @Override
    public void send(String to, String code, LocalDateTime expiresAt) {
        log.info("[EmailVerification] send to={} code={} expiresAt={}", to, code, expiresAt);
    }
}
