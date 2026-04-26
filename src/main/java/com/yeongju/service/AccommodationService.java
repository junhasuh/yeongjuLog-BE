package com.yeongju.service;

import com.yeongju.domain.accommodation.Accommodation;
import com.yeongju.dto.accommodation.AccommodationResponse;
import com.yeongju.external.datago.DataGoClient;
import com.yeongju.external.datago.dto.DataGoResponse;
import com.yeongju.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 숙박시설 서비스
 * - 농어촌민박 데이터 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final DataGoClient dataGoClient;

    @Value("${app.api.dataGo.service-key}")
    private String dataGoServiceKey;

    /**
     * GPS 기준 근처 숙박시설 조회
     */
    public List<AccommodationResponse> getNearbyAccommodations(
            Double latitude,
            Double longitude,
            Double radiusKm,
            Integer limit) {

        List<Accommodation> accommodations = accommodationRepository
                .findNearbyAccommodations(latitude, longitude, radiusKm, limit);

        return accommodations.stream()
                .map(AccommodationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 공공데이터에서 숙박시설 정보 동기화
     */
    @Transactional
    public void syncAccommodationData() {
        log.info("숙박시설 데이터 동기화 시작");

        try {
            DataGoResponse response = dataGoClient.getRuralHomestays(dataGoServiceKey, 1, 1000);

            if (response.getBody() != null && response.getBody().getItems() != null) {
                for (Map<String, Object> data : response.getBody().getItems().getItem()) {
                    String name = (String) data.get("NM");
                    String roadAddress = (String) data.get("RDNMADR");

                    Accommodation accommodation = Accommodation.builder()
                            .name(name)
                            .roadAddress(roadAddress)
                            .roomCount(parseInteger(data.get("RUM_CO")))
                            .businessStartDate(parseDate(data.get("BSN_BEGIN_DE")))
                            .build();

                    accommodationRepository.save(accommodation);
                }
                log.info("숙박시설 동기화 완료: {} 건", response.getBody().getItems().getItem().size());
            }
        } catch (Exception e) {
            log.error("숙박시설 동기화 실패", e);
        }
    }

    private Integer parseInteger(Object value) {
        if (value == null) return null;
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(Object value) {
        if (value == null) return null;
        try {
            long timestamp = Long.parseLong(value.toString());
            return Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

}
