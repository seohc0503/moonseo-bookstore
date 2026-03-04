package com.moonseo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.common.exception.ErrorDetails;
import com.moonseo.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper om;

    public JwtAccessDeniedHandler(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ErrorCode ec = ErrorCode.FORBIDDEN;

        response.setStatus(ec.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.put("reason", "요청한 리소스에 접근할 권한이 없습니다.");

        om.writeValue(response.getWriter(), new ErrorResponse(ec, details));
    }
}
