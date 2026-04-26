package com.yeongju.controller;

import com.yeongju.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 헬스체크 API
 */
@Tag(name = "Health", description = "헬스체크 API")
@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @Operation(summary = "서버 상태 확인", description = "서버의 정상 작동 여부를 확인합니다")
    @GetMapping
    public ApiResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Yeongju Welcome Guide API");
        health.put("version", "v1.0.0");

        return ApiResponse.success(health);
    }

}
