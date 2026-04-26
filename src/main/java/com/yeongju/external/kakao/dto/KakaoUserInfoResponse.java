package com.yeongju.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Kakao 사용자 정보 API 응답 (GET https://kapi.kakao.com/v2/user/me)
 * <p>
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info-response
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {

    /** 카카오 회원 고유 번호 */
    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("properties")
    private Properties properties;

    // -------- 편의 메서드 --------

    public String getNickname() {
        if (kakaoAccount != null && kakaoAccount.profile != null && kakaoAccount.profile.nickname != null) {
            return kakaoAccount.profile.nickname;
        }
        if (properties != null && properties.nickname != null) {
            return properties.nickname;
        }
        return null;
    }

    public String getProfileImageUrl() {
        if (kakaoAccount != null && kakaoAccount.profile != null && kakaoAccount.profile.profileImageUrl != null) {
            return kakaoAccount.profile.profileImageUrl;
        }
        if (properties != null && properties.profileImage != null) {
            return properties.profileImage;
        }
        return null;
    }

    public String getEmail() {
        if (kakaoAccount != null) return kakaoAccount.email;
        return null;
    }

    // -------- Inner classes --------

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        @JsonProperty("profile")
        private Profile profile;

        @JsonProperty("email")
        private String email;

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;

        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

}
