package com.moonseo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.common.exception.ErrorDetails;
import com.moonseo.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Map;

// 401
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    public JwtAuthenticationEntryPoint(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode ec = ErrorCode.AUTH_REQUIRED;

        response.setStatus(ec.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.put("reason", "Authorization 헤더가 없거나 토큰이 유효하지 않습니다.");
                ErrorResponse body = new ErrorResponse(ec.getCode(), ec.getDefaultMessage(), details);
        om.writeValue(response.getWriter(), body);
    }
}
