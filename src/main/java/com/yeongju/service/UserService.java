package com.yeongju.service;

import com.yeongju.domain.user.User;
import com.yeongju.dto.user.UserCreateRequest;
import com.yeongju.dto.user.UserResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 생성 (회원가입)
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .nickname(request.getNickname())
                .points(0)
                .secretLetterCount(0)
                .isGoldShrineUnlocked(false)
                .build();

        User savedUser = userRepository.save(user);
        log.info("사용자 생성 완료: {}", savedUser.getNickname());

        return UserResponse.from(savedUser);
    }

    /**
     * 사용자 조회 (ID)
     */
    public UserResponse getUser(Long userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    /**
     * 사용자 조회 (닉네임)
     */
    public UserResponse getUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    /**
     * 내부용: 사용자 엔티티 조회
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}
