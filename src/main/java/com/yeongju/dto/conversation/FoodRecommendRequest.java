package com.yeongju.dto.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LLM 맛집 추천 요청 DTO (RAG)
 * - 사용자 GPS와 자연어 요청을 받아
 * - DB의 공공데이터 기반 맛집 리스트를 프롬프트에 주입하여
 * - Gemini에게 추천을 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecommendRequest {

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotBlank(message = "메시지는 필수입니다.")
    private String message;

    private Double radiusKm;   // null 허용 (기본 3.0)
    private Integer limit;     // null 허용 (기본 10)
}
