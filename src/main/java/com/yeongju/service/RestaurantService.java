package com.yeongju.service;

import com.yeongju.domain.restaurant.Restaurant;
import com.yeongju.dto.restaurant.RestaurantResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.external.datago.DataGoClient;
import com.yeongju.external.datago.dto.DataGoResponse;
import com.yeongju.external.odcloud.ODCloudClient;
import com.yeongju.external.odcloud.dto.ODCloudResponse;
import com.yeongju.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 맛집 서비스
 * - 4개 공공데이터 통합 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ODCloudClient odCloudClient;
    private final DataGoClient dataGoClient;
    private final GeocodingService geocodingService;

    @Value("${app.api.odcloud.service-key}")
    private String odcloudServiceKey;

    @Value("${app.api.dataGo.service-key}")
    private String dataGoServiceKey;

    /**
     * GPS 기준 근처 맛집 조회 (가중치 높은 순)
     */
    public List<RestaurantResponse> getNearbyRestaurants(
            Double latitude, 
            Double longitude, 
            Double radiusKm,
            Integer limit) {
        
        List<Restaurant> restaurants = restaurantRepository
                .findNearbyRestaurantsSortedByWeight(latitude, longitude, radiusKm, limit);

        return restaurants.stream()
                .map(RestaurantResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 맛집 상세 조회
     */
    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESTAURANT_NOT_FOUND));
        return RestaurantResponse.from(restaurant);
    }

    /**
     * 공공데이터에서 맛집 정보 동기화
     */
    @Transactional
    public void syncRestaurantData() {
        log.info("맛집 데이터 동기화 시작");

        try {
            // 1. 착한가격업소 (ODCloud)
            syncFairPriceStores();

            // 2. 지역사랑상품권 가맹점 (ODCloud)
            syncGiftCertificateStores();

            // 3. 안심식당 (Data.go.kr)
            syncSafeRestaurants();

            // 4. 영주맛집 (Data.go.kr)
            syncGoodRestaurants();

            log.info("맛집 데이터 동기화 완료");
        } catch (Exception e) {
            log.error("맛집 데이터 동기화 중 오류 발생", e);
        }
    }

    /**
     * 착한가격업소 동기화
     */
    private void syncFairPriceStores() {
        try {
            ODCloudResponse response = odCloudClient.getFairPriceStores(1, 1000, odcloudServiceKey);
            
            for (Map<String, Object> data : response.getData()) {
                String name = (String) data.get("업소명");
                String address = (String) data.get("도로명 주소");
                
                Restaurant existing = restaurantRepository
                        .findByNameAndAddress(name, address)
                        .orElse(null);

                Double lat = existing != null ? existing.getLatitude() : null;
                Double lon = existing != null ? existing.getLongitude() : null;
                if (lat == null) {
                    double[] coords = geocodingService.geocode(address);
                    if (coords != null) { lat = coords[0]; lon = coords[1]; }
                }

                // 착한가격업소 플래그 설정
                Restaurant.RestaurantBuilder builder = Restaurant.builder()
                        .name(name)
                        .address(address)
                        .phoneNumber((String) data.get("전화번호"))
                        .category((String) data.get("업종"))
                        .latitude(lat)
                        .longitude(lon)
                        .isFairPriceStore(true)
                        .isGiftCertificateStore(existing != null && existing.getIsGiftCertificateStore())
                        .isYeongjuRestaurant(existing != null && existing.getIsYeongjuRestaurant())
                        .isSafeRestaurant(existing != null && existing.getIsSafeRestaurant())
                        .menuInfo(buildMenuInfo(data));
                if (existing != null) builder.id(existing.getId());

                restaurantRepository.save(builder.build());
            }
            log.info("착한가격업소 동기화 완료: {} 건", response.getData().size());
        } catch (Exception e) {
            log.error("착한가격업소 동기화 실패", e);
        }
    }

    /**
     * 지역사랑상품권 가맹점 동기화
     */
    private void syncGiftCertificateStores() {
        try {
            ODCloudResponse response = odCloudClient.getGiftCertificateStores(1, 1000, odcloudServiceKey);
            
            for (Map<String, Object> data : response.getData()) {
                String name = (String) data.get("상호");
                String address = (String) data.get("소재지");
                
                // 음식점만 필터링
                String category = (String) data.get("업종(품목)");
                if (category != null && (category.contains("식당") || category.contains("음식"))) {
                    Restaurant existing = restaurantRepository
                            .findByNameAndAddress(name, address)
                            .orElse(null);

                    String roadAddress = (String) data.get("소재지도로명주소");
                    String queryAddr = roadAddress != null ? roadAddress : address;

                    Double lat = existing != null ? existing.getLatitude() : null;
                    Double lon = existing != null ? existing.getLongitude() : null;
                    if (lat == null) {
                        double[] coords = geocodingService.geocode(queryAddr);
                        if (coords != null) { lat = coords[0]; lon = coords[1]; }
                    }

                    Restaurant.RestaurantBuilder builder = Restaurant.builder()
                            .name(name)
                            .address(address)
                            .roadAddress(roadAddress)
                            .category(category)
                            .latitude(lat)
                            .longitude(lon)
                            .isGiftCertificateStore(true)
                            .isFairPriceStore(existing != null && existing.getIsFairPriceStore())
                            .isYeongjuRestaurant(existing != null && existing.getIsYeongjuRestaurant())
                            .isSafeRestaurant(existing != null && existing.getIsSafeRestaurant());
                    if (existing != null) builder.id(existing.getId());

                    restaurantRepository.save(builder.build());
                }
            }
            log.info("지역사랑상품권 가맹점 동기화 완료");
        } catch (Exception e) {
            log.error("지역사랑상품권 가맹점 동기화 실패", e);
        }
    }

    /**
     * 안심식당 동기화
     */
    private void syncSafeRestaurants() {
        try {
            DataGoResponse response = dataGoClient.getSafeRestaurants(dataGoServiceKey, 1, 1000);
            
            if (response.getBody() != null && response.getBody().getItems() != null) {
                for (Map<String, Object> data : response.getBody().getItems().getItem()) {
                    String name = (String) data.get("BSNES_NM");
                    String address = (String) data.get("ADRES");

                    Restaurant existing = restaurantRepository
                            .findByNameAndAddress(name, address)
                            .orElse(null);

                    Double lat = existing != null ? existing.getLatitude() : null;
                    Double lon = existing != null ? existing.getLongitude() : null;
                    if (lat == null) {
                        double[] coords = geocodingService.geocode(address);
                        if (coords != null) { lat = coords[0]; lon = coords[1]; }
                    }

                    Restaurant.RestaurantBuilder builder = Restaurant.builder()
                            .name(name)
                            .address(address)
                            .phoneNumber((String) data.get("TELNO"))
                            .category((String) data.get("INDUTY_DETAIL"))
                            .latitude(lat)
                            .longitude(lon)
                            .isSafeRestaurant(true)
                            .isGiftCertificateStore(existing != null && existing.getIsGiftCertificateStore())
                            .isFairPriceStore(existing != null && existing.getIsFairPriceStore())
                            .isYeongjuRestaurant(existing != null && existing.getIsYeongjuRestaurant());
                    if (existing != null) builder.id(existing.getId());

                    restaurantRepository.save(builder.build());
                }
                log.info("안심식당 동기화 완료");
            }
        } catch (Exception e) {
            log.error("안심식당 동기화 실패", e);
        }
    }

    /**
     * 영주맛집 동기화
     */
    private void syncGoodRestaurants() {
        try {
            DataGoResponse response = dataGoClient.getGoodRestaurants(dataGoServiceKey, 1, 1000);
            
            if (response.getBody() != null && response.getBody().getItems() != null) {
                for (Map<String, Object> data : response.getBody().getItems().getItem()) {
                    String name = (String) data.get("BSSH_NM");
                    String address = (String) data.get("ADRES");

                    Restaurant existing = restaurantRepository
                            .findByNameAndAddress(name, address)
                            .orElse(null);

                    Double lat = existing != null ? existing.getLatitude() : null;
                    Double lon = existing != null ? existing.getLongitude() : null;
                    if (lat == null) {
                        double[] coords = geocodingService.geocode(address);
                        if (coords != null) { lat = coords[0]; lon = coords[1]; }
                    }

                    Restaurant.RestaurantBuilder builder = Restaurant.builder()
                            .name(name)
                            .address(address)
                            .phoneNumber((String) data.get("TELNO"))
                            .latitude(lat)
                            .longitude(lon)
                            .isYeongjuRestaurant(true)
                            .isGiftCertificateStore(existing != null && existing.getIsGiftCertificateStore())
                            .isFairPriceStore(existing != null && existing.getIsFairPriceStore())
                            .isSafeRestaurant(existing != null && existing.getIsSafeRestaurant());
                    if (existing != null) builder.id(existing.getId());

                    restaurantRepository.save(builder.build());
                }
                log.info("영주맛집 동기화 완료");
            }
        } catch (Exception e) {
            log.error("영주맛집 동기화 실패", e);
        }
    }

    /**
     * 메뉴 정보 문자열 생성 (착한가격업소)
     */
    private String buildMenuInfo(Map<String, Object> data) {
        StringBuilder menuInfo = new StringBuilder();
        
        for (int i = 1; i <= 3; i++) {
            String item = (String) data.get("품목" + i);
            String price = (String) data.get("가격" + i);
            
            if (item != null && !item.isEmpty()) {
                if (menuInfo.length() > 0) {
                    menuInfo.append(", ");
                }
                menuInfo.append(item).append(": ").append(price);
            }
        }
        
        return menuInfo.toString();
    }

}
