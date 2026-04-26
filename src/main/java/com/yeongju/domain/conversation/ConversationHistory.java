package com.yeongju.domain.conversation;

import com.yeongju.domain.common.BaseTimeEntity;
import com.yeongju.domain.location.Location;
import com.yeongju.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * LLM 대화 기록 엔티티
 * - 도깨비불(금성대군), 주모, 유생 박해운 등과의 대화 이력
 */
@Entity
@Table(name = "conversation_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConversationHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;  // 대화가 이루어진 장소 (nullable)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharacterType characterType;  // 대화 상대 캐릭터

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userMessage;  // 사용자 메시지

    @Column(nullable = false, columnDefinition = "TEXT")
    private String aiResponse;  // AI 응답

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFiltered = false;  // 무례한 발언 필터링 여부

    @Column(length = 200)
    private String filterReason;  // 필터링 사유

}
