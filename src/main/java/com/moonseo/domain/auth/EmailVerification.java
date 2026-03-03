package com.moonseo.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "email_verifications",
        uniqueConstraints = @UniqueConstraint(name = "uk_email_verifications_email", columnNames = "email"))
@Getter
@NoArgsConstructor(access = PROTECTED)
public class EmailVerification {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 최초 발급용 팩토리 메서드
     */
    public static EmailVerification issue(String email, String code, LocalDateTime expiresAt) {
        EmailVerification ev = new EmailVerification();
        ev.email = email;
        ev.code = code;
        ev.expiresAt = expiresAt;
        ev.usedAt = null;
        return ev;
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return !this.expiresAt.isAfter(now);
    }

    /**
     * 만료 후 재발송: 새 코드로 갱신(renew)
     */
    public void renew(String newCode, LocalDateTime newExpiresAt, LocalDateTime now) {
        this.code = newCode;
        this.expiresAt = newExpiresAt;
        this.usedAt = null;
        this.updatedAt = now;
    }

    /**
     * 만료 전 재발송: 기존 코드 유지
     * resend 케이스도 DB에 기록이 남도록 updateAt 수정
     */
    public void resend(LocalDateTime now) {
        this.updatedAt = now;
    }

    /**
     * confirm 성공 처리
     */
    public void confirm(LocalDateTime now) {
        this.usedAt = now;
        this.updatedAt = now;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
    }
}
