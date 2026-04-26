package com.yeongju.dto.character;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "캐릭터 특징 요청")
public class CharacterFeatureRequest {
    
    @Schema(description = "사용자가 원하는 캐릭터 특징", example = "웃는 표정, 안경, 수염")
    @NotBlank(message = "캐릭터 특징을 입력해주세요")
    private String description;
    
    @Schema(description = "성별", example = "male", allowableValues = {"male", "female"})
    private String gender;
    
    @Schema(description = "캐릭터 스타일", example = "scholar", allowableValues = {"scholar", "royal", "common"})
    private String style;
}
