package com.yeongju.controller;

import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.conversation.ConversationRequest;
import com.yeongju.dto.conversation.ConversationResponse;
import com.yeongju.dto.conversation.FoodRecommendRequest;
import com.yeongju.dto.conversation.FoodRecommendResponse;
import com.yeongju.service.LLMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * LLM 대화 API
 */
@Tag(name = "Conversation", description = "LLM 대화 API")
@RestController
@RequestMapping("/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final LLMService llmService;

    @Operation(summary = "LLM 대화", description = "캐릭터와 대화합니다")
    @PostMapping("/chat")
    public ApiResponse<ConversationResponse> chat(@Valid @RequestBody ConversationRequest request) {
        ConversationResponse response = llmService.chat(request);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "LLM 맛집 추천 (RAG)",
            description = "GPS와 자연어 요청을 받아 공공데이터 기반 식당 리스트를 프롬프트로 주입 후 Gemini 추천을 반환합니다."
    )
    @PostMapping("/recommend-food")
    public ApiResponse<FoodRecommendResponse> recommendFood(@Valid @RequestBody FoodRecommendRequest request) {
        FoodRecommendResponse response = llmService.recommendFood(request);
        return ApiResponse.success(response);
    }

}
