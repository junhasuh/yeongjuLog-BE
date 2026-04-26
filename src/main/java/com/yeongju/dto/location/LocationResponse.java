package com.yeongju.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.domain.location.Location;
import com.yeongju.domain.location.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장소 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationResponse {

    private Long id;
    private String name;
    private LocationType type;
    private String description;
    private Double latitude;
    private Double longitude;
    private Boolean isHidden;
    private Boolean requiresNightTime;
    private Boolean isUnlocked;  // 유저가 해금했는지 여부

    public static LocationResponse from(Location location, Boolean isUnlocked) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .type(location.getType())
                .description(location.getDescription())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .isHidden(location.getIsHidden())
                .requiresNightTime(location.getRequiresNightTime())
                .isUnlocked(isUnlocked)
                .build();
    }

}
