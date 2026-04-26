package com.yeongju.dto.mission;

import com.yeongju.domain.mission.Mission;
import com.yeongju.domain.mission.MissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 미션 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionResponse {

    private Long id;
    private String locationName;
    private String title;
    private String question;
    private Integer rewardPoints;
    private MissionType type;
    private Boolean isCompleted;  // 유저가 완료했는지 여부

    public static MissionResponse from(Mission mission, Boolean isCompleted) {
        return MissionResponse.builder()
                .id(mission.getId())
                .locationName(mission.getLocation().getName())
                .title(mission.getTitle())
                .question(mission.getQuestion())
                .rewardPoints(mission.getRewardPoints())
                .type(mission.getType())
                .isCompleted(isCompleted)
                .build();
    }

}
