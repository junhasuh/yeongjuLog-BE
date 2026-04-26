package com.yeongju.repository;

import com.yeongju.domain.location.Location;
import com.yeongju.domain.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findByLocation(Location location);

    Optional<Mission> findByLocationAndDisplayOrder(Location location, Integer displayOrder);

}
