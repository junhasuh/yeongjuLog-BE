package com.yeongju.dto.conversation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yeongju.dto.restaurant.RestaurantResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * LLM 맛집 추천 응답 DTO (RAG)
 * - LLM이 생성한 자연어 응답 + 실제로 프롬프트에 주입된 후보 식당 목록 (검증용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodRecommendResponse {

    /** LLM이 생성한 추천 응답 (사투리 포함) */
    private String response;

    /** 프롬프트에 주입된 후보 식당 수 */
    private Integer candidateCount;

    /** 주입된 후보 식당 전체 (UI에서 함께 표시해서 RAG 검증 가능) */
    private List<RestaurantResponse> candidates;

    /** 에러 발생 여부 */
    private Boolean hasError;

    private String errorMessage;
}
