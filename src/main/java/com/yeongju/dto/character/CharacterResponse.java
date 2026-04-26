package com.yeongju.dto.character;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "캐릭터 생성 응답")
public class CharacterResponse {
    
    @Schema(description = "생성된 캐릭터 이미지 URL", example = "https://s3.amazonaws.com/characters/user123.png")
    private String imageUrl;
    
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @Schema(description = "캐릭터 ID")
    private Long characterId;
    
    @Schema(description = "사용자 ID")
    private Long userId;
}
