package com.yeongju.repository;

import com.yeongju.domain.secretletter.UserSecretLetter;
import com.yeongju.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSecretLetterRepository extends JpaRepository<UserSecretLetter, Long> {

    List<UserSecretLetter> findByUserOrderByCollectionOrderAsc(User user);

    Long countByUser(User user);

}
