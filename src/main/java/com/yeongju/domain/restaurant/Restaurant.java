package com.yeongju.domain.restaurant;

import com.yeongju.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 맛집/식당 엔티티
 * - 4개 공공데이터 통합 저장
 * - 중복 등록된 경우 가중치로 상단 노출
 */
@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;  // 상호명

    @Column(length = 300)
    private String address;  // 주소

    @Column(length = 300)
    private String roadAddress;  // 도로명 주소

    @Column(length = 20)
    private String phoneNumber;  // 전화번호

    @Column(length = 100)
    private String category;  // 업종 (한식, 중식 등)

    @Column
    private Double latitude;  // 위도

    @Column
    private Double longitude;  // 경도

    // 공공데이터 출처별 등록 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean isGiftCertificateStore = false;  // 지역사랑상품권 가맹점 🏷️

    @Column(nullable = false)
    @Builder.Default
    private Boolean isYeongjuRestaurant = false;  // 영주 맛집 ⭐

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSafeRestaurant = false;  // 안심식당 🧼

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFairPriceStore = false;  // 착한가격업소 💰

    @Column(columnDefinition = "TEXT")
    private String menuInfo;  // 메뉴 정보 (착한가격업소의 경우)

    /**
     * 가중치 계산 (중복 등록 개수)
     */
    public int calculateWeight() {
        int weight = 0;
        if (isGiftCertificateStore) weight++;
        if (isYeongjuRestaurant) weight++;
        if (isSafeRestaurant) weight++;
        if (isFairPriceStore) weight++;
        return weight;
    }

    /**
     * 페르소나 강추 여부 (2개 이상 데이터에 등록된 경우)
     */
    public boolean isHighlyRecommended() {
        return calculateWeight() >= 2;
    }

}
