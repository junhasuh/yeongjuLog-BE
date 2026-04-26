package com.yeongju.dto.restaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.domain.restaurant.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
 * 맛집 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String roadAddress;
    private String phoneNumber;
    private String category;
    private Double latitude;
    private Double longitude;
    private List<String> badges;  // 🏷️ ⭐ 🧼 💰
    private Boolean isHighlyRecommended;  // 페르소나 강추 (2개 이상 배지)
    private String menuInfo;
    private String kakaoMapUrl;   // 카카오맵 링크
    private String naverMapUrl;   // 네이버지도 링크

    public static RestaurantResponse from(Restaurant restaurant) {
        List<String> badges = new ArrayList<>();
        if (restaurant.getIsGiftCertificateStore()) badges.add("🏷️ 지역사랑상품권");
        if (restaurant.getIsYeongjuRestaurant()) badges.add("⭐ 영주맛집");
        if (restaurant.getIsSafeRestaurant()) badges.add("🧼 안심식당");
        if (restaurant.getIsFairPriceStore()) badges.add("💰 착한가격");

        String encodedName = URLEncoder.encode(restaurant.getName(), StandardCharsets.UTF_8);
        String kakaoMapUrl = "https://map.kakao.com/?q=" + encodedName;
        String naverMapUrl = "https://map.naver.com/v5/search/" + encodedName;

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .roadAddress(restaurant.getRoadAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .category(restaurant.getCategory())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .badges(badges)
                .isHighlyRecommended(restaurant.isHighlyRecommended())
                .menuInfo(restaurant.getMenuInfo())
                .kakaoMapUrl(kakaoMapUrl)
                .naverMapUrl(naverMapUrl)
                .build();
    }

}
