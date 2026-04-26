package com.yeongju.controller;

import com.yeongju.domain.location.LocationType;
import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.mission.MissionResponse;
import com.yeongju.dto.mission.MissionSubmitRequest;
import com.yeongju.dto.mission.MissionSubmitResponse;
import com.yeongju.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 미션 API
 */
@Tag(name = "Mission", description = "미션 API")
@RestController
@RequestMapping("/v1/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "장소별 미션 조회", description = "특정 장소의 미션 목록을 조회합니다")
    @GetMapping("/location/{locationType}")
    public ApiResponse<List<MissionResponse>> getMissionsByLocation(
            @PathVariable LocationType locationType,
            @RequestParam Long userId) {
        
        List<MissionResponse> response = missionService.getMissionsByLocation(userId, locationType);
        return ApiResponse.success(response);
    }

    @Operation(summary = "미션 답안 제출", description = "미션 답안을 제출하고 검증합니다")
    @PostMapping("/submit")
    public ApiResponse<MissionSubmitResponse> submitMission(@Valid @RequestBody MissionSubmitRequest request) {
        MissionSubmitResponse response = missionService.submitMission(request);
        
        if (response.getIsCorrect()) {
            return ApiResponse.success("정답입니다! 미션을 완료했습니다.", response);
        } else {
            return ApiResponse.success("틀렸습니다. 다시 시도해보세요.", response);
        }
    }

}
