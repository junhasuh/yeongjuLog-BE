package com.yeongju.dto.secretletter;

import com.yeongju.domain.secretletter.UserSecretLetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저 밀서 수집 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecretLetterResponse {

    private Long id;
    private SecretLetterResponse secretLetter;
    private String locationName;
    private Integer collectionOrder;

    public static UserSecretLetterResponse from(UserSecretLetter userSecretLetter) {
        return UserSecretLetterResponse.builder()
                .id(userSecretLetter.getId())
                .secretLetter(SecretLetterResponse.from(userSecretLetter.getSecretLetter()))
                .locationName(userSecretLetter.getLocation().getName())
                .collectionOrder(userSecretLetter.getCollectionOrder())
                .build();
    }

}
