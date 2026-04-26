package com.yeongju.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 인증 제공자 타입
 * - LOCAL: 기존 닉네임 직접 가입 (게스트/개발용)
 * - KAKAO: 카카오 OAuth 로그인
 */
@Getter
@RequiredArgsConstructor
public enum AuthProvider {

    LOCAL("로컬 (닉네임)"),
    KAKAO("카카오");

    private final String displayName;

}
