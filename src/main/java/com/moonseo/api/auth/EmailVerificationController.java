package com.moonseo.api.auth;

import com.moonseo.application.auth.EmailVerificationService;
import com.moonseo.dto.auth.EmailVerificationConfirmRequest;
import com.moonseo.dto.auth.EmailVerificationConfirmResponse;
import com.moonseo.dto.auth.EmailVerificationSendRequest;
import com.moonseo.dto.auth.EmailVerificationSendResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email-verifications")
public class EmailVerificationController {

    private final EmailVerificationService service;

    @PostMapping("/send")
    public EmailVerificationSendResponse send(@RequestBody @Valid EmailVerificationSendRequest request) {
        return service.send(request.email());
    }

    @PostMapping("/confirm")
    public EmailVerificationConfirmResponse confirm(@RequestBody @Valid EmailVerificationConfirmRequest request) {
        return service.confirm(request.email(), request.code());
    }
}
