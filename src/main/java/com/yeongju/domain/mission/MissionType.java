package com.yeongju.domain.mission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 미션 타입 Enum
 */
@Getter
@RequiredArgsConstructor
public enum MissionType {
    
    TEXT_INPUT("텍스트 입력형", "정답 단어를 직접 입력하는 미션"),
    QUIZ("퀴즈형", "객관식 또는 단답형 퀴즈"),
    CONVERSATION("대화형", "LLM과의 대화를 통한 미션 (주막 등)"),
    DATA_SEARCH("데이터 검색형", "공공데이터에서 정보를 찾는 미션 (박물관 유물 번호 등)");

    private final String displayName;
    private final String description;

}
