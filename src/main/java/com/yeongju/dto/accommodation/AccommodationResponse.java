package com.yeongju.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.domain.accommodation.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 숙박시설 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccommodationResponse {

    private Long id;
    private String name;
    private String roadAddress;
    private Integer roomCount;
    private LocalDate businessStartDate;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;

    public static AccommodationResponse from(Accommodation accommodation) {
        return AccommodationResponse.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .roadAddress(accommodation.getRoadAddress())
                .roomCount(accommodation.getRoomCount())
                .businessStartDate(accommodation.getBusinessStartDate())
                .latitude(accommodation.getLatitude())
                .longitude(accommodation.getLongitude())
                .phoneNumber(accommodation.getPhoneNumber())
                .build();
    }

}
