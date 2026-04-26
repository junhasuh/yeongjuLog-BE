package com.yeongju.dto.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 미션 답안 제출 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissionSubmitRequest {

    @NotNull(message = "유저 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "미션 ID는 필수입니다.")
    private Long missionId;

    @NotBlank(message = "답안은 필수입니다.")
    private String answer;

}
