package com.yeongju.dto.conversation;

import com.yeongju.domain.conversation.CharacterType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LLM 대화 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRequest {

    @NotNull(message = "유저 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "캐릭터 타입은 필수입니다.")
    private CharacterType characterType;

    private Long locationId;  // Optional: 특정 장소에서의 대화인 경우

    @NotBlank(message = "메시지는 필수입니다.")
    private String message;

}
