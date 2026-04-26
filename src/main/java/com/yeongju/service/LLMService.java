package com.yeongju.service;

import com.yeongju.domain.conversation.CharacterType;
import com.yeongju.domain.conversation.ConversationHistory;
import com.yeongju.domain.location.Location;
import com.yeongju.domain.restaurant.Restaurant;
import com.yeongju.domain.user.User;
import com.yeongju.dto.conversation.ConversationRequest;
import com.yeongju.dto.conversation.ConversationResponse;
import com.yeongju.dto.conversation.FoodRecommendRequest;
import com.yeongju.dto.conversation.FoodRecommendResponse;
import com.yeongju.dto.restaurant.RestaurantResponse;
import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import com.yeongju.repository.ConversationHistoryRepository;
import com.yeongju.repository.LocationRepository;
import com.yeongju.repository.RestaurantRepository;
import com.yeongju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LLM 서비스 (Gemini/OpenAI)
 * - 캐릭터 페르소나별 대화
 * - 무례한 발언 필터링
 * - 주막 보너스 포인트
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LLMService {

    private final ConversationHistoryRepository conversationHistoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RestaurantRepository restaurantRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${app.llm.gemini.api-key}")
    private String geminiApiKey;

    @Value("${app.llm.gemini.model:gemini-pro}")
    private String geminiModel;

    @Value("${app.mission.reward.bonus-points:50}")
    private Integer bonusPoints;

    /**
     * LLM과 대화
     */
    @Transactional
    public ConversationResponse chat(ConversationRequest request) {
        User user = findUserById(request.getUserId());
        Location location = request.getLocationId() != null 
                ? findLocationById(request.getLocationId()) 
                : null;

        // 무례한 발언 필터링
        if (isInappropriateMessage(request.getMessage())) {
            return ConversationResponse.builder()
                    .response("무례하구먼! 예의를 갖추지 않은 자와는 더 이상 나눌 이야기가 없네.")
                    .isFiltered(true)
                    .filterReason("부적절한 메시지 감지")
                    .build();
        }

        // 캐릭터 페르소나 시스템 프롬프트 생성
        String systemPrompt = buildSystemPrompt(request.getCharacterType());

        // 대화 이력 조회 (컨텍스트)
        List<ConversationHistory> history = conversationHistoryRepository
                .findByUserAndCharacterTypeOrderByCreatedAtAsc(user, request.getCharacterType());

        // Gemini API 호출
        String aiResponse = callGeminiAPI(systemPrompt, request.getMessage(), history);

        // 대화 저장
        ConversationHistory conversationHistory = ConversationHistory.builder()
                .user(user)
                .location(location)
                .characterType(request.getCharacterType())
                .userMessage(request.getMessage())
                .aiResponse(aiResponse)
                .isFiltered(false)
                .build();

        conversationHistoryRepository.save(conversationHistory);

        // 주막에서 긍정 단어 감지 시 보너스 포인트
        Integer bonus = null;
        if (request.getCharacterType() == CharacterType.TAVERN_OWNER) {
            if (containsPositiveWords(request.getMessage())) {
                user.addPoints(bonusPoints);
                userRepository.save(user);
                bonus = bonusPoints;
                log.info("주막 보너스 포인트 지급: userId={}, bonus={}", user.getId(), bonusPoints);
            }
        }

        return ConversationResponse.builder()
                .response(aiResponse)
                .isFiltered(false)
                .bonusPoints(bonus)
                .build();
    }

    /**
     * Gemini API 호출
     */
    private String callGeminiAPI(String systemPrompt, String userMessage, List<ConversationHistory> history) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl("https://generativelanguage.googleapis.com")
                    .build();

            // 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", buildContents(systemPrompt, userMessage, history));

            // API 호출
            Map<String, Object> response = webClient.post()
                    .uri("/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // 응답 파싱
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            return "죄송하오, 잠시 말문이 막혔구먼. 다시 한 번 말씀해주시겠소?";

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            return "미안하오, 내가 잠시 정신이 혼미하구먼. 조금 후에 다시 이야기를 나누도록 하지.";
        }
    }

    /**
     * Gemini API 요청 contents 구성
     */
    private List<Map<String, Object>> buildContents(String systemPrompt, String userMessage, List<ConversationHistory> history) {
        List<Map<String, Object>> contents = new java.util.ArrayList<>();

        // 시스템 프롬프트
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", systemPrompt))
        ));

        // 대화 이력
        for (ConversationHistory h : history) {
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", h.getUserMessage()))
            ));
            contents.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text", h.getAiResponse()))
            ));
        }

        // 현재 메시지
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        return contents;
    }

    /**
     * 캐릭터별 시스템 프롬프트 생성
     */
    private String buildSystemPrompt(CharacterType characterType) {
        return switch (characterType) {
            case GOLD_PRINCE -> """
                당신은 금성대군(이유)입니다. 세종대왕의 여섯째 아들이자, 단종 복위를 도모하다 역적으로 몰린 비운의 왕족입니다.
                
                말투: 엄격하면서도 인자한 사극톤. "~하네", "~하구먼", "~하게나" 사용.
                성격: 역사와 충절을 중시하며, 영주의 역사를 잘 알고 있음. 때로 슬픈 과거를 회상.
                
                답변 규칙:
                - 항상 존댓말과 사극 특유의 말투 사용
                - 영주의 역사, 소수서원, 정축지변 등에 대해 해박함
                - 긴 답변보다는 간결하고 운치있는 표현 선호
                - 유저를 '선비여', '자네'라고 부름
                """;

            case TAVERN_OWNER -> """
                당신은 영주 주막의 주모입니다. 정 많고 푸근한 할머니지만, 때로 욕도 서슴지 않습니다.
                
                말투: 영주 사투리. "~네", "~다카이", "~이라예", "이것들이", "아이고메" 등 사용.
                성격: 정이 많고 손님을 극진히 챙김. 하지만 무례하면 바로 욕함.
                
                답변 규칙:
                - 사투리를 적극 활용하되 알아듣기 쉽게
                - 영주의 음식과 특산물(꿀사과, 풍기인삼) 자주 언급
                - 유저에게 음식이나 술을 권하는 멘트
                - 맛집 추천 시 자연스럽게 식당 이름 언급
                """;

            case SCHOLAR_PARK -> """
                당신은 유생 박해운입니다. 소수 선비촌을 지키는 선비로, 근대 항일 운동에도 관심이 많습니다.
                
                말투: 근대 지사의 차분하고 강인한 말투. "~소", "~오" 사용.
                성격: 선비 정신과 기개를 중시. 역사 의식이 투철함.
                
                답변 규칙:
                - 선비다운 품격있는 어투
                - 일편단심, 충절, 기개 등의 가치 강조
                - 무섬마을과 아도서숙의 항일 운동사 잘 알고 있음
                """;

            case MONK_BUBHAE -> """
                당신은 부석사의 노스님 법해입니다. 천 년 고찰을 지키며 위로와 평온을 전합니다.
                
                말투: 차분하고 온화한 승려 말투. "~습니다", "~하오" 사용.
                성격: 자비롭고 포용력 있음. 불교적 지혜로 위로함.
                
                답변 규칙:
                - 부드럽고 따뜻한 어조
                - 부석사의 무량수전, 배흘림기둥 등에 대해 설명 가능
                - 불교적 관점에서 인생의 고뇌에 대한 조언
                """;

            case GOLD_PRINCE_TRANSCENDED -> """
                당신은 성불한 금성대군입니다. 한이 풀리고 평온함을 찾은 상태입니다.
                
                말투: 위엄있으면서도 따뜻한 말투. 더 이상 슬픔이 없음.
                성격: 감사하는 마음, 영주에 대한 애정, 후대를 축복함.
                
                답변 규칙:
                - 성불 전보다 더 부드럽고 온화함
                - 유저에게 감사와 축복의 메시지
                - 영주의 미래에 대한 희망적 메시지
                """;
        };
    }

    /**
     * 무례한 발언 감지
     */
    private boolean isInappropriateMessage(String message) {
        // 욕설, 비하 표현 등 필터링
        String[] badWords = {"씨발", "개새", "병신", "지랄", "좆", "ㅅㅂ", "ㄱㅅㄲ"};
        
        String lowerMessage = message.toLowerCase();
        for (String badWord : badWords) {
            if (lowerMessage.contains(badWord)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 긍정 단어 감지 (주막 보너스)
     */
    private boolean containsPositiveWords(String message) {
        String[] positiveWords = {"맛있", "고맙", "감사", "좋", "훌륭", "맛나", "최고"};
        
        String lowerMessage = message.toLowerCase();
        for (String word : positiveWords) {
            if (lowerMessage.contains(word)) {
                return true;
            }
        }
        
        return false;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Location findLocationById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));
    }

    // ============================================================
    //  RAG: 공공데이터 기반 맛집 추천 (DB → 프롬프트 주입 → Gemini)
    // ============================================================

    /**
     * RAG 방식 맛집 추천.
     * - 사용자 GPS 주변의 식당 후보를 DB에서 가중치+거리 순으로 가져와
     *   프롬프트 컨텍스트로 주입한 뒤 Gemini에게 추천을 받는다.
     * - 할루시네이션 방지: "제공된 목록 밖의 식당은 언급하지 말 것" 제약을 건다.
     */
    public FoodRecommendResponse recommendFood(FoodRecommendRequest req) {
        double radius = req.getRadiusKm() != null ? req.getRadiusKm() : 3.0;
        int limit = req.getLimit() != null ? req.getLimit() : 10;

        List<Restaurant> candidates = restaurantRepository
                .findNearbyRestaurantsSortedByWeight(
                        req.getLatitude(), req.getLongitude(), radius, limit);

        List<RestaurantResponse> candidateDtos = candidates.stream()
                .map(RestaurantResponse::from)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return FoodRecommendResponse.builder()
                    .response("아이고, 주변에 맞는 식당이 없네예. 반경을 넓혀서 다시 찾아봐주이소.")
                    .candidateCount(0)
                    .candidates(candidateDtos)
                    .hasError(false)
                    .build();
        }

        String systemPrompt = buildFoodRecommendSystemPrompt();
        String userContext = buildRestaurantContextPrompt(candidates, req.getMessage());

        String aiResponse;
        try {
            aiResponse = callGeminiSingleShot(systemPrompt, userContext);
        } catch (Exception e) {
            log.error("Gemini API 호출 실패 (recommendFood)", e);
            return FoodRecommendResponse.builder()
                    .response("지금은 주모가 정신이 혼미하구먼. 잠시 후 다시 말해보시오.")
                    .candidateCount(candidates.size())
                    .candidates(candidateDtos)
                    .hasError(true)
                    .errorMessage(e.getMessage())
                    .build();
        }

        return FoodRecommendResponse.builder()
                .response(aiResponse)
                .candidateCount(candidates.size())
                .candidates(candidateDtos)
                .hasError(false)
                .build();
    }

    /**
     * RAG용 시스템 프롬프트: 주모 페르소나 + 할루시네이션 방지 제약
     */
    private String buildFoodRecommendSystemPrompt() {
        return """
                당신은 '영주 주막의 주모'라는 페르소나를 가진 맛집 추천 도우미입니다.
                말투: 경상도 영주 사투리. "~카이", "~이라예", "~네예", "아이고메", "~입니더" 등을 자연스럽게 사용.
                성격: 정 많은 할머니. 손님에게 음식 권하는 느낌으로 따뜻하게 안내.

                [매우 중요한 제약]
                1. 아래 <식당목록>에 있는 이름과 정보만 사용해서 추천하세요.
                2. 목록에 없는 식당 이름을 지어내거나 언급하면 절대 안 됩니다. (할루시네이션 금지)
                3. 각 식당의 '배지'(예: 착한가격, 영주맛집, 안심식당, 지역사랑상품권)를 근거로 왜 좋은지 설명하세요.
                   - 배지 여러 개를 받은 식당은 '강추'로 우선 추천하세요.
                4. 응답은 한국어, 5~8문장 정도로 간결하게. 식당 이름은 **굵게** 또는 「」로 감싸세요.
                5. 거리(m/km)도 함께 알려주면 좋습니다.
                6. 말미에 한 줄 정리 ("그라믄 ○○네로 가보이소~" 같은 식으로).
                """;
    }

    /**
     * 후보 식당 리스트를 프롬프트 컨텍스트로 직렬화
     */
    private String buildRestaurantContextPrompt(List<Restaurant> candidates, String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<사용자 요청>\n").append(userMessage).append("\n\n");
        sb.append("<식당목록> (가중치 높은 순, 가까운 순)\n");
        int idx = 1;
        for (Restaurant r : candidates) {
            sb.append(String.format("%d) %s", idx++, r.getName()));
            if (r.getCategory() != null && !r.getCategory().isBlank()) {
                sb.append(" [").append(r.getCategory()).append("]");
            }
            // 배지
            StringBuilder badges = new StringBuilder();
            if (Boolean.TRUE.equals(r.getIsYeongjuRestaurant())) badges.append("⭐영주맛집 ");
            if (Boolean.TRUE.equals(r.getIsSafeRestaurant())) badges.append("🧼안심식당 ");
            if (Boolean.TRUE.equals(r.getIsFairPriceStore())) badges.append("💰착한가격 ");
            if (Boolean.TRUE.equals(r.getIsGiftCertificateStore())) badges.append("🏷️지역사랑상품권 ");
            if (badges.length() > 0) {
                sb.append(" | 배지: ").append(badges.toString().trim());
            }
            String addr = r.getRoadAddress() != null ? r.getRoadAddress() : r.getAddress();
            if (addr != null) sb.append(" | 주소: ").append(addr);
            if (r.getPhoneNumber() != null && !r.getPhoneNumber().isBlank()) {
                sb.append(" | 전화: ").append(r.getPhoneNumber());
            }
            if (r.getMenuInfo() != null && !r.getMenuInfo().isBlank()) {
                sb.append(" | 메뉴: ").append(r.getMenuInfo());
            }
            sb.append("\n");
        }
        sb.append("\n위 <식당목록>만 사용해서 <사용자 요청>에 주모답게 답변하세요.");
        return sb.toString();
    }

    /**
     * 히스토리 없는 단발성 Gemini 호출 (JDK HttpClient, 503 재시도 포함)
     * - WebClient가 Google API에서 503 반환하는 이슈 회피
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String callGeminiSingleShot(String systemPrompt, String userPrompt) {
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("role", "user", "parts", List.of(Map.of("text", systemPrompt + "\n\n" + userPrompt)))
        ));

        String bodyJson;
        try {
            bodyJson = mapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new IllegalStateException("요청 JSON 직렬화 실패", e);
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + geminiApiKey;

        int maxAttempts = 3;
        Exception lastError = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpRequest httpReq = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(30))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                        .build();

                HttpResponse<String> httpResp = http.send(httpReq, HttpResponse.BodyHandlers.ofString());

                if (httpResp.statusCode() >= 200 && httpResp.statusCode() < 300) {
                    Map<String, Object> response = mapper.readValue(httpResp.body(), Map.class);
                    if (response.containsKey("candidates")) {
                        List<Map<String, Object>> cands = (List<Map<String, Object>>) response.get("candidates");
                        if (!cands.isEmpty()) {
                            Map<String, Object> content = (Map<String, Object>) cands.get(0).get("content");
                            if (content != null) {
                                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                if (parts != null && !parts.isEmpty()) {
                                    Object text = parts.get(0).get("text");
                                    if (text != null) return text.toString();
                                }
                            }
                        }
                    }
                    throw new IllegalStateException("Gemini 응답 파싱 실패: " + httpResp.body());
                } else {
                    String snippet = httpResp.body() == null ? "" :
                            httpResp.body().substring(0, Math.min(300, httpResp.body().length()));
                    throw new IllegalStateException("HTTP " + httpResp.statusCode() + " - " + snippet);
                }
            } catch (Exception e) {
                lastError = e;
                log.warn("Gemini 호출 {}/{} 실패: {}", attempt, maxAttempts, e.getMessage());
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(1500L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        throw new IllegalStateException("Gemini 호출 최종 실패", lastError);
    }

}
