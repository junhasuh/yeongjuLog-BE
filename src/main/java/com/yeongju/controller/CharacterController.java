package com.yeongju.controller;

import com.yeongju.dto.character.CharacterFeatureRequest;
import com.yeongju.dto.character.CharacterResponse;
import com.yeongju.dto.common.ApiResponse;
import com.yeongju.service.CharacterGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/characters")
@RequiredArgsConstructor
@Tag(name = "Character", description = "캐릭터 생성 API")
public class CharacterController {
    
    private final CharacterGenerationService characterService;
    
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "도트 캐릭터 생성", description = "사용자 사진과 특징을 기반으로 조선시대 도트 캐릭터를 생성합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "캐릭터 생성 성공",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이미지 형식 오류, 크기 초과 등)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "캐릭터 생성 실패"
            )
    })
    public ResponseEntity<ApiResponse<CharacterResponse>> generateCharacter(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "사용자 얼굴 사진", required = true)
            @RequestPart("photo") MultipartFile photo,
            
            @Parameter(description = "캐릭터 특징 정보", required = true)
            @RequestPart("features") @Valid CharacterFeatureRequest features
    ) {
        log.info("캐릭터 생성 요청 - userId: {}, features: {}", userId, features);
        
        CharacterResponse response = characterService.generateCharacter(userId, photo, features);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{userId}/current")
    @Operation(summary = "현재 활성 캐릭터 조회", description = "사용자의 현재 활성화된 캐릭터를 조회합니다")
    public ResponseEntity<ApiResponse<CharacterResponse>> getCurrentCharacter(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long userId
    ) {
        log.info("현재 캐릭터 조회 요청 - userId: {}", userId);
        
        return characterService.getCurrentCharacter(userId)
                .map(character -> ResponseEntity.ok(ApiResponse.success(character)))
                .orElse(ResponseEntity.ok(ApiResponse.<CharacterResponse>success("활성 캐릭터가 없습니다", null)));
    }
}
