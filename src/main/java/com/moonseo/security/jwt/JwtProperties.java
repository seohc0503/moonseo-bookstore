package com.moonseo.security.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "moonseo.jwt")
public class JwtProperties {
    @NotBlank
    private String accessSecret;

    @NotBlank
    private String refreshSecret;

    @Min(1)
    private int accessMinutes;

    @Min(1)
    private int refreshMinutes;
}
