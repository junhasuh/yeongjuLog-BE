package com.yeongju.service;

import com.yeongju.domain.character.UserCharacter;
import com.yeongju.domain.user.User;
import com.yeongju.dto.character.CharacterFeatureRequest;
import com.yeongju.dto.character.CharacterResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.UserCharacterRepository;
import com.yeongju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterGenerationService {
    
    private final UserCharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    
    @Value("${app.huggingface.api-key}")
    private String hfApiKey;
    
    @Value("${app.huggingface.model:nerijs/pixel-art-xl}")
    private String modelName;
    
    private static final String HF_API_BASE_URL = "https://api-inference.huggingface.co/models/";
    
    /**
     * 사용자 사진과 특징을 기반으로 도트 캐릭터 생성
     */
    @Transactional
    public CharacterResponse generateCharacter(
            Long userId, 
            MultipartFile userPhoto, 
            CharacterFeatureRequest features
    ) {
        log.info("캐릭터 생성 시작 - userId: {}, features: {}", userId, features);
        
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 2. 이미지 검증
        validateImage(userPhoto);
        
        // 3. 기존 활성 캐릭터 비활성화
        characterRepository.findByUserAndIsActiveTrue(user)
                .ifPresent(UserCharacter::deactivate);
        
        try {
            // 4. 얼굴 특징 추출 (간단한 프롬프트 생성)
            String faceDescription = extractFaceFeatures(userPhoto);
            
            // 5. 프롬프트 생성
            String prompt = buildPrompt(faceDescription, features);
            log.info("생성된 프롬프트: {}", prompt);
            
            // 6. Hugging Face API로 도트 캐릭터 이미지 생성
            byte[] generatedImage = generatePixelArt(prompt);
            
            // 7. 이미지 저장 (로컬 또는 S3)
            String imageUrl = saveCharacterImage(userId, generatedImage);
            
            // 8. DB에 캐릭터 정보 저장
            UserCharacter character = UserCharacter.builder()
                    .user(user)
                    .imageUrl(imageUrl)
                    .features(features.getDescription())
                    .gender(features.getGender())
                    .style(features.getStyle())
                    .isActive(true)
                    .build();
            
            UserCharacter savedCharacter = characterRepository.save(character);
            
            log.info("캐릭터 생성 완료 - characterId: {}", savedCharacter.getId());
            
            return CharacterResponse.builder()
                    .characterId(savedCharacter.getId())
                    .userId(userId)
                    .imageUrl(imageUrl)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("캐릭터 생성 중 오류 발생", e);
            throw new BusinessException(ErrorCode.CHARACTER_GENERATION_FAILED);
        }
    }
    
    /**
     * 이미지 검증
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        
        // 최대 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.IMAGE_TOO_LARGE);
        }
    }
    
    /**
     * 얼굴 특징 추출 (간단한 버전 - 실제로는 Vision API 사용 가능)
     */
    private String extractFaceFeatures(MultipartFile photo) {
        // TODO: 실제로는 Hugging Face의 얼굴 인식 모델을 사용할 수 있음
        // 현재는 기본 설명 반환
        return "friendly face with warm smile";
    }
    
    /**
     * 프롬프트 생성
     */
    private String buildPrompt(String faceDescription, CharacterFeatureRequest features) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("pixel art character, cute chibi style, ");
        prompt.append("Korean Joseon dynasty traditional hanbok, ");
        prompt.append("traditional Korean scholar hat gat, ");
        
        // 얼굴 특징 추가
        if (faceDescription != null && !faceDescription.isBlank()) {
            prompt.append(faceDescription).append(", ");
        }
        
        // 사용자 요청 특징 추가
        if (features.getDescription() != null && !features.getDescription().isBlank()) {
            prompt.append(features.getDescription()).append(", ");
        }
        
        // 성별 추가
        if (features.getGender() != null) {
            prompt.append(features.getGender()).append(" character, ");
        }
        
        // 스타일 추가
        if (features.getStyle() != null) {
            switch (features.getStyle()) {
                case "scholar":
                    prompt.append("wise scholar appearance, books, ");
                    break;
                case "royal":
                    prompt.append("elegant royal attire, noble appearance, ");
                    break;
                case "common":
                    prompt.append("simple common people clothes, ");
                    break;
            }
        }
        
        prompt.append("standing on wooden platform, front view, ");
        prompt.append("64x64 pixel art style, vibrant colors, ");
        prompt.append("simple black background, game sprite aesthetic");
        
        return prompt.toString();
    }
    
    /**
     * Hugging Face API로 픽셀 아트 생성
     */
    private byte[] generatePixelArt(String prompt) {
        String apiUrl = HF_API_BASE_URL + modelName;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hfApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", prompt);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("negative_prompt", "blurry, low quality, distorted, modern clothes, realistic, 3d");
        parameters.put("num_inference_steps", 30);
        parameters.put("guidance_scale", 7.5);
        requestBody.put("parameters", parameters);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    byte[].class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Hugging Face API 호출 성공 - 이미지 크기: {} bytes", response.getBody().length);
                return response.getBody();
            } else {
                throw new BusinessException(ErrorCode.CHARACTER_GENERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("Hugging Face API 호출 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
    
    /**
     * 캐릭터 이미지 저장 (S3 업로드)
     */
    private String saveCharacterImage(Long userId, byte[] imageData) {
        String filename = "character_" + userId + "_" + System.currentTimeMillis() + ".png";
        String folder = "characters";
        
        // S3에 업로드
        String s3Url = s3Service.uploadBytes(imageData, filename, "image/png", folder);
        
        log.info("캐릭터 이미지 S3 업로드 완료 - URL: {}", s3Url);
        return s3Url;
    }
    
    /**
     * 사용자의 현재 활성 캐릭터 조회
     */
    @Transactional(readOnly = true)
    public Optional<CharacterResponse> getCurrentCharacter(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        return characterRepository.findByUserAndIsActiveTrue(user)
                .map(character -> CharacterResponse.builder()
                        .characterId(character.getId())
                        .userId(userId)
                        .imageUrl(character.getImageUrl())
                        .createdAt(character.getCreatedAt())
                        .build());
    }
}
