package com.yeongju.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_NICKNAME("U002", "이미 사용 중인 닉네임입니다."),

    // Mission
    MISSION_NOT_FOUND("M001", "미션을 찾을 수 없습니다."),
    MISSION_ALREADY_COMPLETED("M002", "이미 완료한 미션입니다."),
    WRONG_ANSWER("M003", "정답이 아닙니다."),
    MISSION_NOT_UNLOCKED("M004", "아직 해금되지 않은 미션입니다."),

    // Location
    LOCATION_NOT_FOUND("L001", "장소를 찾을 수 없습니다."),
    NIGHT_TIME_REQUIRED("L002", "이 장소는 밤 8시 이후에만 이용 가능합니다."),

    // Secret Letter
    SECRET_LETTER_NOT_FOUND("S001", "밀서 조각을 찾을 수 없습니다."),

    // Restaurant
    RESTAURANT_NOT_FOUND("R001", "맛집을 찾을 수 없습니다."),

    // External API
    EXTERNAL_API_ERROR("E001", "외부 API 호출 중 오류가 발생했습니다."),

    // Conversation
    INAPPROPRIATE_MESSAGE("C001", "부적절한 메시지가 감지되었습니다."),

    // Auth / OAuth
    KAKAO_AUTH_FAILED("A001", "카카오 인증에 실패했습니다."),
    KAKAO_USER_INFO_FAILED("A002", "카카오 사용자 정보 조회에 실패했습니다."),
    INVALID_TOKEN("A003", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED("A004", "토큰이 만료되었습니다."),

    // Character
    INVALID_IMAGE("CH001", "유효하지 않은 이미지입니다."),
    INVALID_IMAGE_FORMAT("CH002", "지원하지 않는 이미지 형식입니다."),
    IMAGE_TOO_LARGE("CH003", "이미지 크기가 너무 큽니다. (최대 5MB)"),
    CHARACTER_GENERATION_FAILED("CH004", "캐릭터 생성에 실패했습니다."),
    
    // S3
    S3_UPLOAD_FAILED("S3001", "파일 업로드에 실패했습니다."),
    S3_DELETE_FAILED("S3002", "파일 삭제에 실패했습니다."),

    // Common
    INTERNAL_SERVER_ERROR("G001", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

}
