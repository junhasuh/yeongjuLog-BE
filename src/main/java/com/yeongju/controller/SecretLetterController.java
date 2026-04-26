package com.yeongju.controller;

import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.secretletter.UserSecretLetterResponse;
import com.yeongju.service.SecretLetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * 밀서 조각 API
 */

@Tag(name = "SecretLetter", description = "밀서 조각 API")
@RestController
@RequestMapping("/v1/secret-letters")
@RequiredArgsConstructor
public class SecretLetterController {

    private final SecretLetterService secretLetterService;

    @Operation(summary = "내가 수집한 밀서 조각 조회", description = "유저가 수집한 밀서 조각 목록을 조회합니다")
    @GetMapping("/my")
    public ApiResponse<List<UserSecretLetterResponse>> getMySecretLetters(@RequestParam Long userId) {
        List<UserSecretLetterResponse> response = secretLetterService.getUserSecretLetters(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "밀서 완성 여부 확인", description = "3개의 밀서 조각을 모두 모았는지 확인합니다")
    @GetMapping("/completed")
    public ApiResponse<Boolean> isSecretLetterCompleted(@RequestParam Long userId) {
        boolean isCompleted = secretLetterService.isSecretLetterCompleted(userId);
        return ApiResponse.success(isCompleted);
    }

}
