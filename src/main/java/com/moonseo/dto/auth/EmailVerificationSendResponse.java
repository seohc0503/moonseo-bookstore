package com.moonseo.dto.auth;

import java.time.LocalDateTime;

/**
 * alreadyVerified=true:
 * - sent=false (200 응답만)
 * - resent=false
 * resent:
 * - 만료 전 동일코드 재발송, 만료 후 새코드 재발급 둘 다 resent=true
 */
public record EmailVerificationSendResponse(
        String email,
        boolean sent,
        boolean alreadyVerified,
        boolean resent,
        LocalDateTime expiresAt,
        LocalDateTime verifiedAt
) {}
