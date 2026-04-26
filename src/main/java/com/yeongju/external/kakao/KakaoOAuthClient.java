package com.yeongju.external.kakao;

import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.external.kakao.dto.KakaoTokenResponse;
import com.yeongju.external.kakao.dto.KakaoUserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Kakao OAuth REST API 호출 클라이언트
 * <p>
 * 1) exchangeToken(code) : authorization code -> access token
 * 2) getUserInfo(accessToken) : access token -> 사용자 프로필 조회
 */
@Slf4j
@Component
public class KakaoOAuthClient {

    @Value("${app.oauth.kakao.client-id}")
    private String clientId;

    @Value("${app.oauth.kakao.client-secret:}")
    private String clientSecret;

    @Value("${app.oauth.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${app.oauth.kakao.token-uri}")
    private String tokenUri;

    @Value("${app.oauth.kakao.user-info-uri}")
    private String userInfoUri;

    private final WebClient webClient;

    public KakaoOAuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * authorization code -> access token 교환
     *
     * @param code        프론트가 받은 authorization code
     * @param redirectUri null이면 설정 기본값 사용, 다중 환경 지원을 위해 override 가능
     */
    public KakaoTokenResponse exchangeToken(String code, String redirectUri) {
        String finalRedirectUri = (redirectUri != null && !redirectUri.isBlank()) ? redirectUri : this.redirectUri;

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("redirect_uri", finalRedirectUri);
        form.add("code", code);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }

        try {
            KakaoTokenResponse response = webClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .block();

            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
            }
            return response;
        } catch (WebClientResponseException e) {
            log.warn("Kakao token 교환 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Kakao token 교환 중 오류", e);
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    /**
     * access token -> 사용자 프로필 조회
     */
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        try {
            KakaoUserInfoResponse response = webClient.get()
                    .uri(userInfoUri)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                    .retrieve()
                    .bodyToMono(KakaoUserInfoResponse.class)
                    .block();

            if (response == null || response.getId() == null) {
                throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED);
            }
            return response;
        } catch (WebClientResponseException e) {
            log.warn("Kakao 사용자 정보 조회 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Kakao 사용자 정보 조회 중 오류", e);
            throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED);
        }
    }

}
