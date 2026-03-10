package com.moonseo.application.auth;

import com.moonseo.common.exception.ApiException;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.domain.auth.EmailVerification;
import com.moonseo.domain.auth.EmailVerificationRepository;
import com.moonseo.dto.auth.EmailVerificationConfirmResponse;
import com.moonseo.dto.auth.EmailVerificationSendResponse;
import com.moonseo.infra.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository repo;
    private final EmailSender emailSender;

    private static final int EXPIRE_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public EmailVerificationSendResponse send(String email) {
        EmailVerification ev = repo.findByEmail(email).orElse(null);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // (1) 최초 발송: row 자체가 없음
        if (ev == null) {
            String code = generateCode();
            LocalDateTime expiresAt = now.plusMinutes(EXPIRE_MINUTES);

            ev = EmailVerification.issue(email, code, expiresAt);
            repo.save(ev);

            emailSender.send(email, code, expiresAt);

            return new EmailVerificationSendResponse(
                    email,
                    true,
                    false,
                    false,
                    expiresAt,
                    null
            );
        }

        // (2) 이미 인증 완료: 재발송 안 함(alreadyVerified=true로 알려주고 200)
        if (ev.isUsed()) {
            return new EmailVerificationSendResponse(
                    email,
                    false,
                    true,
                    false,
                    ev.getExpiresAt(),
                    ev.getUsedAt()
            );
        }

        // (3) 만료 후 재발송: 새 코드 발급
        if (ev.isExpired(now)) {
            LocalDateTime newExpiresAt = now.plusMinutes(EXPIRE_MINUTES);
            String newCode = generateCode();

            ev.renew(newCode, newExpiresAt, now);

            emailSender.send(email, newCode, newExpiresAt);

            return new EmailVerificationSendResponse(
                    email,
                    true,
                    false,
                    true,
                    newExpiresAt,
                    null
            );
        }
        // (4) 만료 전 재발송: 동일 코드 유지
        // code/expiresAt 그대로
        // updatedAt만 바꿔서 재발송 흔적 남김
        ev.resend(now);
        emailSender.send(email, ev.getCode(), ev.getExpiresAt());

        return new EmailVerificationSendResponse(
                email,
                true,
                false,
                true,
                ev.getExpiresAt(),
                ev.getUsedAt()
        );
    }

    @Transactional
    public EmailVerificationConfirmResponse confirm(String email, String code) {
        EmailVerification ev = repo.findByEmail(email).orElseThrow(() -> new ApiException(
                ErrorCode.STATE_INVALID,
                Map.of("reason", "인증 요청 내역이 존재하지 않습니다.", "email", email)
        ));
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiresAt = ev.getExpiresAt();

        // 이미 인증 완료
        if (ev.isUsed()) {
            // 같은 코드 -> 200 멱등
            if (ev.getCode().equals(code)) {
                return new EmailVerificationConfirmResponse(
                        email,
                        true,
                        true,
                        ev.getUsedAt()
                );
            }
            // 다른 코드 -> 정책상 409
            throw new ApiException(
                    ErrorCode.STATE_INVALID,
                    Map.of("reason", "이미 인증이 완료된 내역입니다.", "email", email)
            );
        }

        // 미사용인데 만료
        if (ev.isExpired(now)) {
            throw new ApiException(
                    ErrorCode.STATE_INVALID,
                    Map.of("reason", "인증 유효 시간이 만료되었습니다.",
                            "email", email,
                            "expiresAt", expiresAt)
            );
        }

        // 미사용인데 코드 불일치
        if (!ev.getCode().equals(code)) {
            throw new ApiException(
                    ErrorCode.STATE_INVALID,
                    Map.of("reason", "인증 번호가 일치하지 않습니다.")
            );
        }

        // 성공: used_at 기록 -> 재사용 불가
        ev.confirm(now);
        return new EmailVerificationConfirmResponse(
                email,
                true,
                false,
                now);
    }

    /**
     * 가입 선행 조건 메서드
     * 목표:
     * - signup 시작 시 이 메서드를 먼저 호출
     * - confirm 없이 signup 시도하면 막히도록 강제
     * 정책:
     * - row 없으면 409
     * - row 있는데 used_at 없으면 409
     * - used_at 있으면 OK(no-op)
     */
    @Transactional(readOnly = true)
    public void assertVerifiedForSignUp(String email) {
        EmailVerification ev = repo.findByEmail(email).orElseThrow(() -> new ApiException(
                ErrorCode.STATE_INVALID,
                Map.of("reason", "인증 요청 내역이 존재하지 않습니다.", "email", email)
        ));

        if (!ev.isUsed()) {
            throw new ApiException(
                    ErrorCode.STATE_INVALID,
                    Map.of("reason", "인증이 완료되지 않았습니다.", "email", email)
            );
        }
    }

    private static String generateCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
