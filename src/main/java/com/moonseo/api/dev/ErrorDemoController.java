package com.moonseo.api.dev;

import com.moonseo.common.exception.ApiException;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.common.exception.ErrorDetails;
import com.moonseo.dto.dev.DemoValidationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dev/errors")
public class ErrorDemoController {

    // 400 VALIDATION_ERROR 재현
    @PostMapping("/validation")
    public Map<String, Object> validation(@RequestBody @Valid DemoValidationRequest request) {
        return Map.of("ok", true);
    }

    // 404 NOT_FOUND 재현
    @GetMapping("/not-found/{id}")
    public Map<String, Object> notFound(@PathVariable Long id) {
        throw new ApiException(
                ErrorCode.NOT_FOUND,
                Map.of("resource", "Demo", "id", id));
    }

    // 409 STATE_INVALID 재현
    @PostMapping("/state-invalid")
    public Map<String, Object> stateInvalid(@RequestParam(defaultValue = "PAID") String current) {
        throw new ApiException(
                ErrorCode.STATE_INVALID,
                Map.of("current", current, "expected", "PLACED"));
    }
}
