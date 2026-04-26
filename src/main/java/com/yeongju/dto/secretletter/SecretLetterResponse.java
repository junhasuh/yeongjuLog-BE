package com.yeongju.dto.secretletter;

import com.yeongju.domain.secretletter.SecretLetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 밀서 조각 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecretLetterResponse {

    private Long id;
    private Integer sequenceNumber;  // 1, 2, 3
    private String title;
    private String content;
    private String description;

    public static SecretLetterResponse from(SecretLetter secretLetter) {
        return SecretLetterResponse.builder()
                .id(secretLetter.getId())
                .sequenceNumber(secretLetter.getSequenceNumber())
                .title(secretLetter.getTitle())
                .content(secretLetter.getContent())
                .description(secretLetter.getDescription())
                .build();
    }

}
