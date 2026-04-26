package com.yeongju.repository;

import com.yeongju.domain.character.UserCharacter;
import com.yeongju.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCharacterRepository extends JpaRepository<UserCharacter, Long> {
    
    /**
     * 사용자의 활성화된 캐릭터 조회
     */
    Optional<UserCharacter> findByUserAndIsActiveTrue(User user);
    
    /**
     * 사용자의 모든 캐릭터 개수 조회
     */
    long countByUser(User user);
}
