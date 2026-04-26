package com.yeongju.domain.secretletter;

import com.yeongju.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 밀서 조각 엔티티
 * - 밀서 #1, #2, #3
 * - 획득 순서에 따라 자동 배정
 */
@Entity
@Table(name = "secret_letters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SecretLetter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer sequenceNumber;  // 순서 번호 (1, 2, 3)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 밀서 내용 (일부)

    @Column(length = 100)
    private String title;  // 밀서 제목

    @Column(columnDefinition = "TEXT")
    private String description;  // 설명

}
