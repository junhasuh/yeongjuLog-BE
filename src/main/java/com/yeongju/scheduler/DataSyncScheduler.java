package com.yeongju.scheduler;

import com.yeongju.service.AccommodationService;
import com.yeongju.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 공공데이터 주기적 동기화 스케줄러
 * - 매주 월요일 새벽에 자동 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncScheduler {

    private final RestaurantService restaurantService;
    private final AccommodationService accommodationService;

    /**
     * 맛집 데이터 동기화 - 매주 월요일 새벽 2시
     */
    @Scheduled(cron = "0 0 2 * * MON", zone = "Asia/Seoul")
    public void syncRestaurantData() {
        log.info("[Scheduler] 맛집 데이터 주기적 동기화 시작");
        try {
            restaurantService.syncRestaurantData();
            log.info("[Scheduler] 맛집 데이터 주기적 동기화 완료");
        } catch (Exception e) {
            log.error("[Scheduler] 맛집 동기화 실패", e);
        }
    }

    /**
     * 숙박시설 데이터 동기화 - 매주 월요일 새벽 3시
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    public void syncAccommodationData() {
        log.info("[Scheduler] 숙박시설 데이터 주기적 동기화 시작");
        try {
            accommodationService.syncAccommodationData();
            log.info("[Scheduler] 숙박시설 데이터 주기적 동기화 완료");
        } catch (Exception e) {
            log.error("[Scheduler] 숙박시설 동기화 실패", e);
        }
    }
}
