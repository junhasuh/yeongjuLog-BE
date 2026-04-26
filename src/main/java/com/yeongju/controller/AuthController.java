package com.yeongju.controller;

import com.yeongju.dto.auth.KakaoLoginRequest;
import com.yeongju.dto.auth.LoginResponse;
import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.user.UserResponse;
import com.yeongju.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 인증(로그인/토큰) API
 */
@Tag(name = "Auth", description = "인증/로그인 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 authorization code를 받아 회원가입 또는 로그인 처리 후 JWT를 발급합니다. " +
                    "최초 로그인이면 자동으로 회원가입되며 isNewUser=true 로 반환됩니다."
    )
    @PostMapping("/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        LoginResponse response = authService.kakaoLogin(request);
        return ApiResponse.success(
                response.getIsNewUser() ? "카카오 회원가입이 완료되었습니다." : "로그인되었습니다.",
                response
        );
    }

    @Operation(
            summary = "현재 사용자 조회",
            description = "Authorization 헤더의 Bearer 토큰으로 현재 로그인한 사용자 정보를 반환합니다."
    )
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        return ApiResponse.success(authService.me(authorizationHeader));
    }

}
