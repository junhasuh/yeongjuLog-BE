package com.yeongju.domain.mission;

import com.yeongju.domain.common.BaseTimeEntity;
import com.yeongju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 미션 진행상황 엔티티
 * - 유저별 미션 클리어 여부 추적
 */
@Entity
@Table(name = "mission_progress", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MissionProgress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;  // 완료 여부

    @Column
    private LocalDateTime completedAt;  // 완료 시간

    @Column
    private String userAnswer;  // 사용자가 입력한 답

    @Column
    @Builder.Default
    private Integer attemptCount = 0;  // 시도 횟수

    /**
     * 미션 완료 처리
     */
    public void complete(String userAnswer) {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.userAnswer = userAnswer;
    }

    /**
     * 시도 횟수 증가
     */
    public void incrementAttemptCount() {
        this.attemptCount++;
    }

}
