package com.yeongju.service;

import com.yeongju.config.JwtTokenProvider;
import com.yeongju.domain.user.AuthProvider;
import com.yeongju.domain.user.User;
import com.yeongju.dto.auth.KakaoLoginRequest;
import com.yeongju.dto.auth.LoginResponse;
import com.yeongju.dto.user.UserResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.external.kakao.KakaoOAuthClient;
import com.yeongju.external.kakao.dto.KakaoTokenResponse;
import com.yeongju.external.kakao.dto.KakaoUserInfoResponse;
import com.yeongju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증/로그인 서비스
 * - 카카오 OAuth: authorization code -> access token -> 사용자 정보 -> (upsert) -> JWT 발급
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인/회원가입 (upsert)
     */
    @Transactional
    public LoginResponse kakaoLogin(KakaoLoginRequest request) {
        // 1) code -> token 교환
        KakaoTokenResponse tokenResponse = kakaoOAuthClient.exchangeToken(
                request.getCode(),
                request.getRedirectUri()
        );

        // 2) access token -> 사용자 프로필 조회
        KakaoUserInfoResponse kakaoUser = kakaoOAuthClient.getUserInfo(tokenResponse.getAccessToken());

        if (kakaoUser.getId() == null) {
            throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED);
        }

        // 3) kakaoId 로 기존 회원 조회, 없으면 신규 가입
        User user = userRepository.findByKakaoId(kakaoUser.getId())
                .orElse(null);

        boolean isNewUser = (user == null);
        if (isNewUser) {
            user = createKakaoUser(kakaoUser);
            log.info("카카오 신규 가입: kakaoId={}, nickname={}", kakaoUser.getId(), user.getNickname());
        } else {
            // 프로필 이미지/이메일 최신화
            user.syncKakaoProfile(kakaoUser.getProfileImageUrl(), kakaoUser.getEmail());
            log.info("카카오 로그인: userId={}, kakaoId={}", user.getId(), kakaoUser.getId());
        }

        // 4) JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getNickname(),
                user.getProvider().name()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresInMs(jwtTokenProvider.getAccessTokenExpirationMs())
                .isNewUser(isNewUser)
                .user(UserResponse.from(user))
                .build();
    }

    /**
     * 토큰으로 현재 사용자 조회 (인증 확인용)
     */
    public UserResponse me(String bearerToken) {
        String token = JwtTokenProvider.resolveBearerToken(bearerToken);
        if (token == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        Long userId = jwtTokenProvider.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    // ============================================================
    //  내부 헬퍼
    // ============================================================

    private User createKakaoUser(KakaoUserInfoResponse kakaoUser) {
        String nickname = resolveUniqueNickname(kakaoUser.getNickname(), kakaoUser.getId());

        User user = User.builder()
                .nickname(nickname)
                .points(0)
                .secretLetterCount(0)
                .isGoldShrineUnlocked(false)
                .provider(AuthProvider.KAKAO)
                .kakaoId(kakaoUser.getId())
                .profileImageUrl(kakaoUser.getProfileImageUrl())
                .email(kakaoUser.getEmail())
                .build();

        return userRepository.save(user);
    }

    /**
     * 닉네임 충돌 시 숫자 suffix 를 붙여 유니크하게 만듭니다.
     * 카카오 닉네임이 비어있으면 "kakao_{kakaoId}" 를 기본값으로.
     */
    private String resolveUniqueNickname(String base, Long kakaoId) {
        String candidate = (base != null && !base.isBlank()) ? base.trim() : ("kakao_" + kakaoId);

        // 닉네임 길이 제한(20) 보호
        if (candidate.length() > 18) {
            candidate = candidate.substring(0, 18);
        }

        if (!userRepository.existsByNickname(candidate)) return candidate;

        // 1~99 suffix 시도
        for (int i = 1; i < 100; i++) {
            String suffixed = candidate + i;
            if (!userRepository.existsByNickname(suffixed)) return suffixed;
        }
        // 그래도 충돌하면 kakaoId 붙임
        return (candidate + "_" + kakaoId).substring(0, Math.min(20, (candidate + "_" + kakaoId).length()));
    }

}
