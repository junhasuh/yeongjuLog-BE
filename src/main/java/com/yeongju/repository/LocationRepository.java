package com.yeongju.repository;

import com.yeongju.domain.location.Location;
import com.yeongju.domain.location.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByType(LocationType type);

    List<Location> findByIsHiddenFalse();

    List<Location> findByIsHiddenTrue();

}
