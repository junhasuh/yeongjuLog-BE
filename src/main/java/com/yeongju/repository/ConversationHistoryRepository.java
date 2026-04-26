package com.yeongju.repository;

import com.yeongju.domain.conversation.CharacterType;
import com.yeongju.domain.conversation.ConversationHistory;
import com.yeongju.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationHistoryRepository extends JpaRepository<ConversationHistory, Long> {

    List<ConversationHistory> findByUserOrderByCreatedAtDesc(User user);

    List<ConversationHistory> findByUserAndCharacterTypeOrderByCreatedAtAsc(User user, CharacterType characterType);

}
