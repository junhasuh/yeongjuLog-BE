package com.yeongju.repository;

import com.yeongju.domain.secretletter.SecretLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretLetterRepository extends JpaRepository<SecretLetter, Long> {

    Optional<SecretLetter> findBySequenceNumber(Integer sequenceNumber);

}
