package com.moonseo.dto.auth;

import java.time.LocalDateTime;

public record EmailVerificationConfirmResponse(
        String email,
        boolean confirmed,
        boolean alreadyConfirmed,
        LocalDateTime confirmedAt
) {}
