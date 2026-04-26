package com.yeongju.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 로그인 요청 DTO
 * - 프론트가 카카오 인가 페이지에서 받은 authorization code 를 그대로 전달
 * - redirectUri 는 카카오 콘솔에 등록된 값과 정확히 일치해야 함 (null 이면 서버 기본값 사용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginRequest {

    @NotBlank(message = "카카오 authorization code는 필수입니다.")
    private String code;

    /** 선택: 여러 환경(웹/앱/로컬) 지원용. null 이면 application.yml 기본값 사용. */
    private String redirectUri;

}
