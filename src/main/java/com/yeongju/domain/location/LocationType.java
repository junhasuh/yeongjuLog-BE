package com.yeongju.domain.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 장소 타입 Enum
 */
@Getter
@RequiredArgsConstructor
public enum LocationType {
    
    // 메인 미션 장소 (밀서 조각 획득 가능)
    SOSU_SEOWON("소수서원", true, false),
    SOSU_MUSEUM("소수박물관", true, false),
    SOSU_VILLAGE("소수 선비촌", true, false),
    
    // 메인 엔딩 장소
    GOLD_SHRINE("금성대군 신단", false, false),
    
    // 주막 (항상 이용 가능)
    TAVERN("주막", false, false),
    
    // 히든 장소 (금성대군 신단 클리어 후 해금)
    MUSEOM_VILLAGE("무섬마을", false, true),
    BUSEOK_TEMPLE("부석사", false, true),
    
    // 야간 히든 장소 (20시 이후)
    SUNHEUNG_HYANGGYO("순흥향교", false, true);

    private final String displayName;
    private final boolean providesSecretLetter;  // 밀서 조각 제공 여부
    private final boolean isHidden;  // 히든 장소 여부

}
