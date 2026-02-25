package com.moonseo.api.dev;

import com.moonseo.common.exception.ApiException;
import com.moonseo.common.exception.ErrorCode;
import com.moonseo.dto.dev.DemoValidationRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/_dev/errors")
public class ErrorDemoController {

    // 400 VALIDATION_ERROR 재현
    @PostMapping("/validation")
    public Map<String, Object> validation(@RequestBody @Valid DemoValidationRequest request) {
        return Map.of("ok", true);
    }

    // 404 NOT_FOUND 재현
    @GetMapping("/not-found/{id}")
    public Map<String, Object> notFound(@PathVariable Long id) {
        throw ErrorCode.NOT_FOUND.exception(
                "대상을 찾을 수 없습니다.",
                Map.of("resource", "Demo", "id", id));
    }

    // 409 STATE_INVALID 재현
    @PostMapping("/state-invalid")
    public Map<String, Object> stateInvalid(@RequestParam(defaultValue = "PAID") String current) {
        throw ErrorCode.STATE_INVALID.exception(
                "상태 전이가 불가능합니다.",
                Map.of("current", current, "expected", "PLACED"));
    }
}
