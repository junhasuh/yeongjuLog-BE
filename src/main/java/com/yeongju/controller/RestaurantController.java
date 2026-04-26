package com.yeongju.controller;

import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.restaurant.RestaurantResponse;
import com.yeongju.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 맛집 API
 */
@Tag(name = "Restaurant", description = "맛집 API")
@RestController
@RequestMapping("/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "근처 맛집 조회", description = "GPS 기준 반경 내 맛집을 조회합니다 (가중치 높은 순)")
    @GetMapping("/nearby")
    public ApiResponse<List<RestaurantResponse>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "2.0") Double radiusKm,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        List<RestaurantResponse> response = restaurantService
                .getNearbyRestaurants(latitude, longitude, radiusKm, limit);
        
        return ApiResponse.success(response);
    }

    @Operation(summary = "맛집 상세 조회", description = "맛집 ID로 상세 정보 및 지도 링크를 조회합니다")
    @GetMapping("/{id}")
    public ApiResponse<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ApiResponse.success(restaurantService.getRestaurantById(id));
    }

    @Operation(summary = "맛집 데이터 동기화", description = "공공데이터에서 맛집 정보를 동기화합니다 (관리자용)")
    @PostMapping("/sync")
    public ApiResponse<String> syncRestaurantData() {
        restaurantService.syncRestaurantData();
        return ApiResponse.success("맛집 데이터 동기화가 완료되었습니다.", "SYNC_COMPLETED");
    }

}
