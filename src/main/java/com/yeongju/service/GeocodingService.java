package com.yeongju.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * 카카오 Geocoding 서비스
 * - 주소 → 위도/경도 변환
 * - application.yml에 app.api.kakao.rest-api-key 설정 필요
 */
@Slf4j
@Service
public class GeocodingService {

    @Value("${app.api.kakao.rest-api-key:}")
    private String kakaoApiKey;

    private final WebClient webClient;

    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://dapi.kakao.com")
                .build();
    }

    /**
     * 주소를 위도/경도로 변환
     * @param address 변환할 주소 (도로명 또는 지번)
     * @return double[]{latitude, longitude} 또는 null (실패 시)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public double[] geocode(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        if (kakaoApiKey == null || kakaoApiKey.isBlank()) {
            log.debug("Kakao API 키가 설정되지 않아 Geocoding을 건너뜁니다.");
            return null;
        }

        try {
            Map response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .queryParam("size", 1)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                List<Map<String, Object>> documents =
                        (List<Map<String, Object>>) response.get("documents");
                if (documents != null && !documents.isEmpty()) {
                    Map<String, Object> doc = documents.get(0);
                    double longitude = Double.parseDouble((String) doc.get("x"));
                    double latitude  = Double.parseDouble((String) doc.get("y"));
                    log.debug("Geocoding 성공: address={}, lat={}, lon={}", address, latitude, longitude);
                    return new double[]{latitude, longitude};
                }
            }
            log.warn("Geocoding 결과 없음: address={}", address);
        } catch (Exception e) {
            log.warn("Geocoding 실패: address={}, error={}", address, e.getMessage());
        }
        return null;
    }
}
