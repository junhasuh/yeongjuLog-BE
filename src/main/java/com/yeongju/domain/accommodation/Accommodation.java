package com.yeongju.domain.accommodation;

import com.yeongju.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 숙박 시설 엔티티
 * - 농어촌민박 신고대장 API 데이터
 */
@Entity
@Table(name = "accommodations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Accommodation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;  // 민박 이름

    @Column(length = 300)
    private String roadAddress;  // 도로명 주소

    @Column
    private Integer roomCount;  // 객실 수

    @Column
    private LocalDate businessStartDate;  // 사업 개시일

    @Column
    private Double latitude;  // 위도

    @Column
    private Double longitude;  // 경도

    @Column(length = 20)
    private String phoneNumber;  // 전화번호 (있는 경우)

}
