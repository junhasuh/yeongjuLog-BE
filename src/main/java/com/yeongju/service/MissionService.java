package com.yeongju.service;

import com.yeongju.domain.location.Location;
import com.yeongju.domain.location.LocationType;
import com.yeongju.domain.mission.Mission;
import com.yeongju.domain.mission.MissionProgress;
import com.yeongju.domain.secretletter.SecretLetter;
import com.yeongju.domain.secretletter.UserSecretLetter;
import com.yeongju.domain.user.User;
import com.yeongju.dto.mission.MissionResponse;
import com.yeongju.dto.mission.MissionSubmitRequest;
import com.yeongju.dto.mission.MissionSubmitResponse;
import com.yeongju.dto.secretletter.SecretLetterResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 미션 서비스
 * - 미션 조회, 답안 제출, 밀서 조각 지급
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionProgressRepository missionProgressRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SecretLetterRepository secretLetterRepository;
    private final UserSecretLetterRepository userSecretLetterRepository;

    @Value("${app.mission.night-time-hour:20}")
    private Integer nightTimeHour;

    @Value("${app.mission.reward.default-points:100}")
    private Integer defaultRewardPoints;

    /**
     * 특정 장소의 미션 조회
     */
    public List<MissionResponse> getMissionsByLocation(Long userId, LocationType locationType) {
        User user = findUserById(userId);
        Location location = locationRepository.findByType(locationType)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        // 야간 전용 장소 체크 (순흥향교)
        if (location.getRequiresNightTime() && !isNightTime()) {
            throw new BusinessException(ErrorCode.NIGHT_TIME_REQUIRED);
        }

        List<Mission> missions = missionRepository.findByLocation(location);

        return missions.stream()
                .map(mission -> {
                    boolean isCompleted = missionProgressRepository
                            .existsByUserAndMissionAndIsCompletedTrue(user, mission);
                    return MissionResponse.from(mission, isCompleted);
                })
                .collect(Collectors.toList());
    }

    /**
     * 미션 답안 제출 및 검증
     */
    @Transactional
    public MissionSubmitResponse submitMission(MissionSubmitRequest request) {
        User user = findUserById(request.getUserId());
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MISSION_NOT_FOUND));

        // 이미 완료한 미션인지 체크
        MissionProgress progress = missionProgressRepository
                .findByUserAndMission(user, mission)
                .orElseGet(() -> MissionProgress.builder()
                        .user(user)
                        .mission(mission)
                        .isCompleted(false)
                        .attemptCount(0)
                        .build());

        if (progress.getIsCompleted()) {
            throw new BusinessException(ErrorCode.MISSION_ALREADY_COMPLETED);
        }

        // 시도 횟수 증가
        progress.incrementAttemptCount();

        // 정답 체크 (대소문자 무시, 공백 제거)
        String userAnswer = request.getAnswer().trim().toLowerCase();
        String correctAnswer = mission.getCorrectAnswer().trim().toLowerCase();

        if (!userAnswer.equals(correctAnswer)) {
            missionProgressRepository.save(progress);
            return MissionSubmitResponse.builder()
                    .isCorrect(false)
                    .message("틀렸습니다. 다시 시도해보세요.")
                    .build();
        }

        // 정답! 미션 완료 처리
        progress.complete(request.getAnswer());
        missionProgressRepository.save(progress);

        // 보상 지급
        user.addPoints(mission.getRewardPoints());

        // 밀서 조각 지급 (소수서원, 박물관, 선비촌만)
        SecretLetterResponse secretLetterResponse = null;
        if (mission.getLocation().getType().name().contains("SOSU")) {
            secretLetterResponse = grantSecretLetter(user, mission.getLocation());
        }

        userRepository.save(user);

        log.info("미션 완료: userId={}, missionId={}, points={}", 
                user.getId(), mission.getId(), mission.getRewardPoints());

        return MissionSubmitResponse.builder()
                .isCorrect(true)
                .message(mission.getSuccessMessage())
                .rewardPoints(mission.getRewardPoints())
                .totalPoints(user.getPoints())
                .secretLetter(secretLetterResponse)
                .isGoldShrineUnlocked(user.getIsGoldShrineUnlocked())
                .build();
    }

    /**
     * 밀서 조각 지급
     * - 획득 순서에 따라 #1 -> #2 -> #3 자동 배정
     */
    private SecretLetterResponse grantSecretLetter(User user, Location location) {
        // 이미 3개 모두 수집했으면 null 반환
        if (user.getSecretLetterCount() >= 3) {
            return null;
        }

        // 다음 순서의 밀서 조각 조회
        Integer nextSequence = user.getSecretLetterCount() + 1;
        SecretLetter secretLetter = secretLetterRepository.findBySequenceNumber(nextSequence)
                .orElseThrow(() -> new BusinessException(ErrorCode.SECRET_LETTER_NOT_FOUND));

        // 유저에게 밀서 조각 지급
        UserSecretLetter userSecretLetter = UserSecretLetter.builder()
                .user(user)
                .secretLetter(secretLetter)
                .location(location)
                .collectionOrder(nextSequence)
                .build();

        userSecretLetterRepository.save(userSecretLetter);

        // 유저 밀서 개수 증가 (3개 모이면 금성대군 신단 해금)
        user.incrementSecretLetterCount();

        log.info("밀서 조각 지급: userId={}, sequence={}, location={}", 
                user.getId(), nextSequence, location.getName());

        return SecretLetterResponse.from(secretLetter);
    }

    /**
     * 현재 야간 시간인지 체크 (20시 이후)
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
