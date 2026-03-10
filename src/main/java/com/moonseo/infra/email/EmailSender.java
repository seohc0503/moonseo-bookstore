package com.moonseo.infra.email;

import java.time.LocalDateTime;

public interface EmailSender {

    /**
     * @param to        수신 이메일
     * @param code      인증 코드
     * @param expiresAt 만료 시각
     */
    void send(String to, String code, LocalDateTime expiresAt);
}
