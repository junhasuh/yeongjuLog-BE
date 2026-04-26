package com.yeongju.domain.secretletter;

import com.yeongju.domain.common.BaseTimeEntity;
import com.yeongju.domain.location.Location;
import com.yeongju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자가 수집한 밀서 조각
 * - 어느 장소에서 몇 번째 밀서를 얻었는지 기록
 */
@Entity
@Table(name = "user_secret_letters",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "secret_letter_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserSecretLetter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secret_letter_id", nullable = false)
    private SecretLetter secretLetter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;  // 어느 장소에서 획득했는지

    @Column(nullable = false)
    private Integer collectionOrder;  // 수집 순서 (1, 2, 3)

}
