package com.yeongju.repository;

import com.yeongju.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByNameAndAddress(String name, String address);

    /**
     * GPS 기준 반경 내 맛집 조회 (가중치 높은 순으로 정렬)
     * Haversine 공식을 사용한 거리 계산
     */
    @Query(value = """
        SELECT r.*, 
               (6371 * acos(cos(radians(:latitude)) 
               * cos(radians(r.latitude)) 
               * cos(radians(r.longitude) - radians(:longitude)) 
               + sin(radians(:latitude)) 
               * sin(radians(r.latitude)))) AS distance,
               (CAST(r.is_gift_certificate_store AS UNSIGNED) +
                CAST(r.is_yeongju_restaurant AS UNSIGNED) +
                CAST(r.is_safe_restaurant AS UNSIGNED) +
                CAST(r.is_fair_price_store AS UNSIGNED)) AS weight
        FROM restaurants r
        HAVING distance < :radiusKm
        ORDER BY weight DESC, distance ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Restaurant> findNearbyRestaurantsSortedByWeight(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("limit") Integer limit
    );

}
