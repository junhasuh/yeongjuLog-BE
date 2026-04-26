package com.yeongju.domain.user;

import com.yeongju.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * - 닉네임, 엽전(포인트) 관리
 * - 밀서 조각 수집 상태 추적
 * - 인증 제공자 (LOCAL / KAKAO)
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_kakao_id", columnNames = {"kakao_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;  // 엽전 포인트

    @Column(nullable = false)
    @Builder.Default
    private Integer secretLetterCount = 0;  // 수집한 밀서 조각 개수 (0~3)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isGoldShrineUnlocked = false;  // 금성대군 신단 미션 해금 여부

    // ============================================================
    // 인증/소셜 로그인 관련
    // ============================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'LOCAL'")
    @Builder.Default
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "kakao_id")
    private Long kakaoId;   // 카카오 고유 ID (nullable, LOCAL 가입자는 null)

    @Column(length = 500)
    private String profileImageUrl;  // 카카오 프로필 이미지 URL

    @Column(length = 100)
    private String email;  // 카카오 계정 이메일 (선택 동의)

    /**
     * 엽전 포인트 추가
     */
    public void addPoints(Integer amount) {
        if (amount != null && amount > 0) {
            this.points += amount;
        }
    }

    /**
     * 밀서 조각 개수 증가
     */
    public void incrementSecretLetterCount() {
        if (this.secretLetterCount < 3) {
            this.secretLetterCount++;

            // 밀서 3개 모두 수집 시 금성대군 신단 해금
            if (this.secretLetterCount == 3) {
                this.isGoldShrineUnlocked = true;
            }
        }
    }

    /**
     * 카카오 프로필 동기화 (재로그인 시 이미지/이메일 최신화)
     */
    public void syncKakaoProfile(String profileImageUrl, String email) {
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        if (email != null) {
            this.email = email;
        }
    }

}
