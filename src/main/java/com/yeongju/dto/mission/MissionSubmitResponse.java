package com.yeongju.dto.mission;

import com.yeongju.dto.secretletter.SecretLetterResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 미션 제출 결과 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionSubmitResponse {

    private Boolean isCorrect;
    private String message;
    private Integer rewardPoints;
    private Integer totalPoints;
    private SecretLetterResponse secretLetter;  // 획득한 밀서 조각
    private Boolean isGoldShrineUnlocked;  // 금성대군 신단 해금 여부

}
