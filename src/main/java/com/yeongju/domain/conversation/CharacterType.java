package com.yeongju.domain.conversation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * LLM 캐릭터 페르소나 타입
 */
@Getter
@RequiredArgsConstructor
public enum CharacterType {
    
    GOLD_PRINCE("금성대군 (도깨비불)", "엄격하면서도 인자한 사극톤 선비 말투"),
    GOLD_PRINCE_TRANSCENDED("금성대군 (성불)", "성불 후 반투명 연출, 위엄있는 말투"),
    TAVERN_OWNER("주막 주모", "영주 사투리를 쓰는 정 많은 욕쟁이 할머니"),
    SCHOLAR_PARK("유생 박해운", "근대 지사의 차분하고 강인한 말투"),
    MONK_BUBHAE("노스님 법해", "부석사 승려, 차분하고 위로하는 말투");

    private final String displayName;
    private final String personaDescription;

}
