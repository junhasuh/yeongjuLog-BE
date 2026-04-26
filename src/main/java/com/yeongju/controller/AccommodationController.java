package com.yeongju.controller;

import com.yeongju.dto.accommodation.AccommodationResponse;
import com.yeongju.dto.common.ApiResponse;
import com.yeongju.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 숙박시설 API
 */
@Tag(name = "Accommodation", description = "숙박시설 API")
@RestController
@RequestMapping("/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @Operation(summary = "근처 숙박시설 조회", description = "GPS 기준 반경 내 숙박시설을 조회합니다")
    @GetMapping("/nearby")
    public ApiResponse<List<AccommodationResponse>> getNearbyAccommodations(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "10") Integer limit) {

        List<AccommodationResponse> response = accommodationService
                .getNearbyAccommodations(latitude, longitude, radiusKm, limit);

        return ApiResponse.success(response);
    }

    @Operation(summary = "숙박시설 데이터 동기화", description = "공공데이터에서 숙박시설 정보를 동기화합니다 (관리자용)")
    @PostMapping("/sync")
    public ApiResponse<String> syncAccommodationData() {
        accommodationService.syncAccommodationData();
        return ApiResponse.success("숙박시설 데이터 동기화가 완료되었습니다.", "SYNC_COMPLETED");
    }

}
