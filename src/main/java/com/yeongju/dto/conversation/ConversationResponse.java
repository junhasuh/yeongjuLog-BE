package com.yeongju.dto.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LLM 대화 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {

    private String response;
    private Boolean isFiltered;
    private String filterReason;
    private Integer bonusPoints;  // 주막에서 긍정 단어 감지 시 보너스 포인트

}
