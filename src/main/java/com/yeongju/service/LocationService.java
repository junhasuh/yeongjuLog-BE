package com.yeongju.service;

import com.yeongju.domain.location.Location;
import com.yeongju.domain.location.LocationType;
import com.yeongju.domain.user.User;
import com.yeongju.dto.location.LocationResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.LocationRepository;
import com.yeongju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 장소 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Value("${app.mission.night-time-hour:20}")
    private Integer nightTimeHour;

    /**
     * 모든 장소 조회 (유저별 해금 상태 포함)
     */
    public List<LocationResponse> getAllLocations(Long userId) {
        User user = findUserById(userId);
        List<Location> locations = locationRepository.findAll();

        return locations.stream()
                .map(location -> LocationResponse.from(location, isLocationUnlocked(user, location)))
                .collect(Collectors.toList());
    }

    /**
     * 일반 장소만 조회 (히든 제외)
     */
    public List<LocationResponse> getVisibleLocations(Long userId) {
        User user = findUserById(userId);
        List<Location> locations = locationRepository.findByIsHiddenFalse();

        return locations.stream()
                .map(location -> LocationResponse.from(location, isLocationUnlocked(user, location)))
                .collect(Collectors.toList());
    }

    /**
     * 히든 장소만 조회 (해금된 경우만)
     */
    public List<LocationResponse> getHiddenLocations(Long userId) {
        User user = findUserById(userId);
        
        // 금성대군 신단을 클리어하지 않았으면 빈 리스트
        if (!user.getIsGoldShrineUnlocked()) {
            return List.of();
        }

        List<Location> hiddenLocations = locationRepository.findByIsHiddenTrue();

        return hiddenLocations.stream()
                .filter(location -> isLocationUnlocked(user, location))
                .map(location -> LocationResponse.from(location, true))
                .collect(Collectors.toList());
    }

    /**
     * 특정 장소 상세 조회
     */
    public LocationResponse getLocation(Long userId, LocationType locationType) {
        User user = findUserById(userId);
        Location location = locationRepository.findByType(locationType)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        boolean isUnlocked = isLocationUnlocked(user, location);

        if (!isUnlocked) {
            throw new BusinessException(ErrorCode.MISSION_NOT_UNLOCKED);
        }

        return LocationResponse.from(location, true);
    }

    /**
     * 장소 해금 여부 판단
     */
    private boolean isLocationUnlocked(User user, Location location) {
        // 일반 장소는 항상 해금
        if (!location.getIsHidden()) {
            return true;
        }

        // 히든 장소는 금성대군 신단 클리어 필요
        if (!user.getIsGoldShrineUnlocked()) {
            return false;
        }

        // 순흥향교는 야간 시간에만 해금
        if (location.getRequiresNightTime()) {
            return isNightTime();
        }

        return true;
    }

    /**
     * 현재 야간 시간인지 체크
     */
    private boolean isNightTime() {
        LocalTime now = LocalTime.now();
        return now.getHour() >= nightTimeHour;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}
