package com.yeongju.controller;

import com.yeongju.domain.location.LocationType;
import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.location.LocationResponse;
import com.yeongju.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 장소 API
 */
@Tag(name = "Location", description = "장소 API")
@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "모든 장소 조회", description = "모든 장소를 조회합니다 (해금 상태 포함)")
    @GetMapping
    public ApiResponse<List<LocationResponse>> getAllLocations(@RequestParam Long userId) {
        List<LocationResponse> response = locationService.getAllLocations(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "일반 장소 조회", description = "히든이 아닌 일반 장소만 조회합니다")
    @GetMapping("/visible")
    public ApiResponse<List<LocationResponse>> getVisibleLocations(@RequestParam Long userId) {
        List<LocationResponse> response = locationService.getVisibleLocations(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "히든 장소 조회", description = "해금된 히든 장소를 조회합니다")
    @GetMapping("/hidden")
    public ApiResponse<List<LocationResponse>> getHiddenLocations(@RequestParam Long userId) {
        List<LocationResponse> response = locationService.getHiddenLocations(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "장소 상세 조회", description = "특정 장소의 상세 정보를 조회합니다")
    @GetMapping("/{locationType}")
    public ApiResponse<LocationResponse> getLocation(
            @PathVariable LocationType locationType,
            @RequestParam Long userId) {
        
        LocationResponse response = locationService.getLocation(userId, locationType);
        return ApiResponse.success(response);
    }

}
