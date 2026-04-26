package com.yeongju.repository;

import com.yeongju.domain.mission.Mission;
import com.yeongju.domain.mission.MissionProgress;
import com.yeongju.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionProgressRepository extends JpaRepository<MissionProgress, Long> {

    Optional<MissionProgress> findByUserAndMission(User user, Mission mission);

    List<MissionProgress> findByUserAndIsCompletedTrue(User user);

    @Query("SELECT COUNT(mp) FROM MissionProgress mp WHERE mp.user = :user AND mp.isCompleted = true")
    Long countCompletedMissionsByUser(@Param("user") User user);

    boolean existsByUserAndMissionAndIsCompletedTrue(User user, Mission mission);

}
