package com.yeongju.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.domain.user.AuthProvider;
import com.yeongju.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String nickname;
    private Integer points;
    private Integer secretLetterCount;
    private Boolean isGoldShrineUnlocked;

    // 인증/프로필 관련 (카카오 로그인 시 포함)
    private AuthProvider provider;
    private String profileImageUrl;
    private String email;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .points(user.getPoints())
                .secretLetterCount(user.getSecretLetterCount())
                .isGoldShrineUnlocked(user.getIsGoldShrineUnlocked())
                .provider(user.getProvider())
                .profileImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .build();
    }

}
