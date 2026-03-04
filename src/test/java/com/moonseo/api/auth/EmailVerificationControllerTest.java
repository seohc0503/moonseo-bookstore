package com.moonseo.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonseo.application.auth.EmailVerificationService;
import com.moonseo.common.exception.ApiException;
import com.moonseo.domain.auth.EmailVerification;
import com.moonseo.domain.auth.EmailVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@SpringBootTest
@AutoConfigureMockMvc
class EmailVerificationControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @Autowired
    EmailVerificationService service;
    @Autowired
    EmailVerificationRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    @Test
    @DisplayName("send 후 올바른 코드로 confirm을 호출하면, used_at이 저장되고 confirmed=true를 반환한다")
    void should_MarkUsedAt_When_ConfirmWithValidCode() throws Exception {
        // Given: 최초 send 인증 요청 완료
        String email = "user1@test.com";

        mvc.perform(post("/auth/email-verifications/send")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.sent").value(true));

        EmailVerification ev = repo.findByEmail(email).orElseThrow();
        String code = ev.getCode();
        LocalDateTime now = LocalDateTime.now(UTC);

        // When: 올바른 code로 confirm을 호출
        mvc.perform(post("/auth/email-verifications/confirm")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email, "code", code))))
                // Then: 200 OK + confirmed=true + alreadyConfirmed=false
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmed").value(true))
                .andExpect(jsonPath("$.alreadyConfirmed").value(false));

        // Then: DB에 used_at이 채워져 있어야 함(재사용 불가 근거)
        EmailVerification after = repo.findByEmail(email).orElseThrow();
        assertThat(after.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("만료 전 send를 반복 호출하면, 동일 코드가 유지된다(동일 코드 재발송 정책)")
    void should_KeepSameCode_When_SendAgainBeforeExpire() throws Exception {
        // Given: 최초 send 인증 요청 완료
        String email = "user2@test.com";

        mvc.perform(post("/auth/email-verifications/send")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk());

        String code1 = repo.findByEmail(email).orElseThrow().getCode();

        // When: 바로(만료 전) send를 한 번 더 호출
        mvc.perform(post("/auth/email-verifications/send")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                // Then: 200 + resent=true(재발송 케이스)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resent").value(true));

        // Then: 동일한 코드 유지
        String code2 = repo.findByEmail(email).orElseThrow().getCode();
        assertThat(code2).isEqualTo(code1);
    }

    @Test
    @DisplayName("틀린 코드로 confirm 호출 시, 409 STATE_INVALID로 실패한다(상태 오류는 409로 통일)")
    void should_409StateInvalid_When_ConfirmWithWrongCode() throws Exception {
        // Given: 최초 send 인증 요청 완료
        String email = "user3@test.com";

        mvc.perform(post("/auth/email-verifications/send")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk());

        // When: 틀린 코드로 Confirm 호출
        mvc.perform(post("/auth/email-verifications/confirm")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email, "code", "000000"))))
                // Then: 409 code=STATE_INVALID (공통 에러 포맷)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STATE_INVALID"));
    }

    @Test
    @DisplayName("confirm 성공한 후 같은 코드로 confirm을 다시 호출하면 200 + alreadyConfirmed=true")
    void should_AlreadyConfirmedTrue_When_ConfirmWithSameCodeAfterSuccess() throws Exception {
        // Given: 최초 send 인증 요청 완료
        String email = "user4@test.com";

        mvc.perform(post("/auth/email-verifications/send")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isOk());

        String code = repo.findByEmail(email).orElseThrow().getCode();

        mvc.perform(post("/auth/email-verifications/confirm")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email, "code", code))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alreadyConfirmed").value(false));

        // When: 같은 코드로 다시 Confirm 호출
        mvc.perform(post("/auth/email-verifications/confirm")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("email", email, "code", code))))
                // Then: 200 + alreadyConfirmed=true
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmed").value(true))
                .andExpect(jsonPath("$.alreadyConfirmed").value(true));
    }

    @Test
    @DisplayName("confirm 없이 signup 선행 검증(assertVerifiedForSignup)을 호출하면 ApiException으로 막힌다(가입 선행 흐름 고정)")
    void should_BlockSignupPrerequisite_When_NotConfirmedYet() throws Exception {
        // Given: 최초 send 인증 요청 완료 + confirmed=false
        String email = "user5@test.com";
        service.send(email);

        // When + Then: signup 선행 검증 호출 시 ApiException 예외
        assertThatThrownBy(() -> service.assertVerifiedForSignUp(email))
                .isInstanceOf(ApiException.class);

    }
}