package com.yeongju.domain.location;

import com.yeongju.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 장소 엔티티
 * - 소수서원, 소수박물관, 선비촌, 금성대군 신단, 주막 등
 */
@Entity
@Table(name = "locations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Location extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;  // 장소명 (예: "소수서원", "소수박물관")

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;  // 장소 타입

    @Column(columnDefinition = "TEXT")
    private String description;  // 장소 설명

    @Column
    private Double latitude;  // 위도

    @Column
    private Double longitude;  // 경도

    @Column(nullable = false)
    @Builder.Default
    private Boolean isHidden = false;  // 히든 장소 여부 (무섬마을, 부석사)

    @Column(nullable = false)
    @Builder.Default
    private Boolean requiresNightTime = false;  // 야간 전용 여부 (순흥향교)

}
