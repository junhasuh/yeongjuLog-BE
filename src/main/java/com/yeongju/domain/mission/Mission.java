package com.yeongju.domain.mission;

import com.yeongju.domain.common.BaseTimeEntity;
import com.yeongju.domain.location.Location;
import jakarta.persistence.*;
import lombok.*;

/**
 * 미션 엔티티
 * - 각 장소별 퀴즈/미션 정보
 */
@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mission extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;  // 미션 장소

    @Column(nullable = false, length = 200)
    private String title;  // 미션 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;  // 미션 질문

    @Column(nullable = false, length = 100)
    private String correctAnswer;  // 정답 (예: "숙수사", "일편단심")

    @Column(nullable = false)
    @Builder.Default
    private Integer rewardPoints = 100;  // 보상 엽전

    @Column(columnDefinition = "TEXT")
    private String successMessage;  // 성공 시 메시지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;  // 미션 타입

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;  // 표시 순서

}
