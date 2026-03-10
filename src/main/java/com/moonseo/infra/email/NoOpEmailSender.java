package com.moonseo.infra.email;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 현재 단계에서는 실제 메일 발송 기능이 없으므로
 * 최소한의 로그만 남기고 인증코드는 노출하지 않는다.
 */
@Slf4j
@Component
@Profile("!local")
public class NoOpEmailSender implements EmailSender{
    @Override
    public void send(String to, String code, LocalDateTime expiresAt) {
        log.info("[EmailVerification] send to={} expiresAt={}", to, expiresAt);
    }
}
