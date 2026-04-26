package com.yeongju.external.datago;

import com.yeongju.external.datago.dto.DataGoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data.go.kr API Client
 * - 안심식당
 * - 영주맛집
 * - 농어촌민박
 */
@FeignClient(name = "datago", url = "${app.api.dataGo.base-url}")
public interface DataGoClient {

    /**
     * 안심식당 조회
     */
    @GetMapping("/5090000/safeRestaurantService/getSafeRestaurant")
    DataGoResponse getSafeRestaurants(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("numOfRows") Integer numOfRows
    );

    /**
     * 영주맛집 조회
     */
    @GetMapping("/5090000/goodRestaurantStatusService/getGoodRestaurantStatus")
    DataGoResponse getGoodRestaurants(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("numOfRows") Integer numOfRows
    );

    /**
     * 농어촌민박 조회
     */
    @GetMapping("/5090000/ruralHomestayReportListService/getRuralHomestayReportList")
    DataGoResponse getRuralHomestays(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("numOfRows") Integer numOfRows
    );

}
