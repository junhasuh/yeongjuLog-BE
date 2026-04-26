package com.yeongju.repository;

import com.yeongju.domain.accommodation.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    /**
     * GPS 기준 반경 내 숙박시설 조회
     */
    @Query(value = """
        SELECT a.*, 
               (6371 * acos(cos(radians(:latitude)) 
               * cos(radians(a.latitude)) 
               * cos(radians(a.longitude) - radians(:longitude)) 
               + sin(radians(:latitude)) 
               * sin(radians(a.latitude)))) AS distance
        FROM accommodations a
        HAVING distance < :radiusKm
        ORDER BY distance ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Accommodation> findNearbyAccommodations(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("limit") Integer limit
    );

}
