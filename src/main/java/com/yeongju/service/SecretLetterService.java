package com.yeongju.service;

import com.yeongju.domain.secretletter.UserSecretLetter;
import com.yeongju.domain.user.User;
import com.yeongju.dto.secretletter.SecretLetterResponse;
import com.yeongju.dto.secretletter.UserSecretLetterResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.UserRepository;
import com.yeongju.repository.UserSecretLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 밀서 조각 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecretLetterService {

    private final UserSecretLetterRepository userSecretLetterRepository;
    private final UserRepository userRepository;

    /**
     * 유저가 수집한 밀서 조각 목록 조회
     */
    public List<UserSecretLetterResponse> getUserSecretLetters(Long userId) {
        User user = findUserById(userId);
        
        List<UserSecretLetter> userSecretLetters = userSecretLetterRepository
                .findByUserOrderByCollectionOrderAsc(user);

        return userSecretLetters.stream()
                .map(UserSecretLetterResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 밀서 완성 여부 확인
     */
    public boolean isSecretLetterCompleted(Long userId) {
        User user = findUserById(userId);
        return user.getSecretLetterCount() >= 3;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}
