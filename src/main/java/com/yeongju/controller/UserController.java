package com.yeongju.controller;

import com.yeongju.dto.common.ApiResponse;
import com.yeongju.dto.user.UserCreateRequest;
import com.yeongju.dto.user.UserResponse;
import com.yeongju.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 API
 */
@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다 (회원가입)")
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.success("사용자가 생성되었습니다.", response);
    }

    @Operation(summary = "사용자 조회 (ID)", description = "ID로 사용자 정보를 조회합니다")
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        UserResponse response = userService.getUser(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "사용자 조회 (닉네임)", description = "닉네임으로 사용자 정보를 조회합니다")
    @GetMapping("/nickname/{nickname}")
    public ApiResponse<UserResponse> getUserByNickname(@PathVariable String nickname) {
        UserResponse response = userService.getUserByNickname(nickname);
        return ApiResponse.success(response);
    }

}
