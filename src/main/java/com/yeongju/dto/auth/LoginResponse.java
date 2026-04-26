package com.yeongju.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    /** Bearer 액세스 토큰 */
    private String accessToken;

    /** Refresh 토큰 (선택적으로 발급) */
    private String refreshToken;

    /** accessToken 만료까지 남은 시간(ms) */
    private Long expiresInMs;

    /** 신규 가입이면 true (프론트에서 온보딩 분기 가능) */
    private Boolean isNewUser;

    /** 사용자 정보 */
    private UserResponse user;

}
