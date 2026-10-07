# 📚 API 사용 가이드

## 🎯 빠른 시작

### 1. 회원가입

```http
POST /api/v1/users
Content-Type: application/json

{
  "nickname": "선비여행자"
}
```

**응답 예시:**
```json
{
  "success": true,
  "message": "사용자가 생성되었습니다.",
  "data": {
    "id": 1,
    "nickname": "선비여행자",
    "points": 0,
    "secretLetterCount": 0,
    "isGoldShrineUnlocked": false
  }
}
```

---

## 🗺️ 장소 탐색

### 1. 모든 장소 조회

```http
GET /api/v1/locations?userId=1
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "소수서원",
      "type": "SOSU_SEOWON",
      "description": "한국 최초의 사액서원",
      "latitude": 36.9956,
      "longitude": 128.6289,
      "isHidden": false,
      "requiresNightTime": false,
      "isUnlocked": true
    }
  ]
}
```

### 2. 히든 장소 조회 (금성대군 신단 클리어 후)

```http
GET /api/v1/locations/hidden?userId=1
```

---

## 🎯 미션 수행

### 1. 장소별 미션 조회

```http
GET /api/v1/missions/location/SOSU_SEOWON?userId=1
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "locationName": "소수서원",
      "title": "강학당에 숨겨진 충절의 조각",
      "question": "정축년의 그날 밤... 비극 속에 잊힌 이 옛 절의 이름은 무엇인가?",
      "rewardPoints": 100,
      "type": "TEXT_INPUT",
      "isCompleted": false
    }
  ]
}
```

### 2. 미션 답안 제출

```http
POST /api/v1/missions/submit
Content-Type: application/json

{
  "userId": 1,
  "missionId": 1,
  "answer": "숙수사"
}
```

**성공 응답:**
```json
{
  "success": true,
  "message": "정답입니다! 미션을 완료했습니다.",
  "data": {
    "isCorrect": true,
    "message": "오오... 자네 덕분에 잊혔던 이름이 다시 빛을 발하는구먼.",
    "rewardPoints": 100,
    "totalPoints": 100,
    "secretLetter": {
      "id": 1,
      "sequenceNumber": 1,
      "title": "밀서 조각 #1",
      "content": "피로 물든 죽계천의 한(恨)이...",
      "description": "첫 번째 밀서 조각"
    },
    "isGoldShrineUnlocked": false
  }
}
```

---

## 📜 밀서 조각 관리

### 1. 내가 수집한 밀서 조각 조회

```http
GET /api/v1/secret-letters/my?userId=1
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "secretLetter": {
        "id": 1,
        "sequenceNumber": 1,
        "title": "밀서 조각 #1",
        "content": "피로 물든 죽계천의 한(恨)이..."
      },
      "locationName": "소수서원",
      "collectionOrder": 1
    }
  ]
}
```

### 2. 밀서 완성 여부 확인

```http
GET /api/v1/secret-letters/completed?userId=1
```

---

## 🍽️ 맛집 추천

### 1. 근처 맛집 조회 (GPS 기반)

```http
GET /api/v1/restaurants/nearby?latitude=36.9956&longitude=128.6289&radiusKm=2.0&limit=20
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "영주 한정식",
      "address": "경상북도 영주시 중앙로 123",
      "phoneNumber": "054-123-4567",
      "category": "한식",
      "badges": ["🏷️ 지역사랑상품권", "⭐ 영주맛집", "🧼 안심식당"],
      "isHighlyRecommended": true,
      "menuInfo": "비빔밥: 8,000원, 된장찌개: 7,000원"
    }
  ]
}
```

### 2. 맛집 데이터 동기화 (관리자용)

```http
POST /api/v1/restaurants/sync
```

---

## 🏨 숙박시설 조회

### 1. 근처 숙박시설 조회

```http
GET /api/v1/accommodations/nearby?latitude=36.9956&longitude=128.6289&radiusKm=5.0&limit=10
```

**응답 예시:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "금호농장민박",
      "roadAddress": "경상북도 영주시 풍기읍 삼가로 257",
      "roomCount": 4,
      "businessStartDate": "2006-04-01"
    }
  ]
}
```

---

## 💬 LLM 대화

### 1. 금성대군과 대화

```http
POST /api/v1/conversations/chat
Content-Type: application/json

{
  "userId": 1,
  "characterType": "GOLD_PRINCE",
  "locationId": null,
  "message": "대군님, 정축지변에 대해 알려주세요."
}
```

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "response": "정축지변이라... 그때의 아픔을 다시 떠올리는구먼. 1457년 정축년, 나와 순흥부사 이보흠은 단종 임금의 복위를 도모했으나 거사가 발각되어 이 땅이 피로 물들었네. 수백 년간 역적으로 몰려 구천을 떠돌았지만, 숙종 대에 이르러 억울함이 풀렸다네.",
    "isFiltered": false,
    "bonusPoints": null
  }
}
```

### 2. 주막 주모와 대화 (보너스 포인트)

```http
POST /api/v1/conversations/chat
Content-Type: application/json

{
  "userId": 1,
  "characterType": "TAVERN_OWNER",
  "message": "주모님, 꿀사과가 정말 맛있겠네요!"
}
```

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "response": "아이고메, 그라제! 우리 영주 꿀사과가 얼매나 달고 맛나는데! 여그 한 입 먹어보이소. 풍기인삼주도 한 잔 쭉 들이키고 가이소!",
    "isFiltered": false,
    "bonusPoints": 50
  }
}
```

---

## 📊 사용자 정보 조회

### 1. 내 정보 조회

```http
GET /api/v1/users/1
```

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nickname": "선비여행자",
    "points": 350,
    "secretLetterCount": 3,
    "isGoldShrineUnlocked": true
  }
}
```

---

## 🔥 게임 플레이 시나리오

### 시나리오 1: 첫 미션 완료

1. **회원가입**
   ```
   POST /api/v1/users
   { "nickname": "선비여행자" }
   ```

2. **소수서원 미션 조회**
   ```
   GET /api/v1/missions/location/SOSU_SEOWON?userId=1
   ```

3. **답안 제출 (숙수사)**
   ```
   POST /api/v1/missions/submit
   { "userId": 1, "missionId": 1, "answer": "숙수사" }
   ```

4. **밀서 조각 확인**
   ```
   GET /api/v1/secret-letters/my?userId=1
   ```

5. **근처 맛집 보기**
   ```
   GET /api/v1/restaurants/nearby?latitude=36.9956&longitude=128.6289
   ```

---

### 시나리오 2: 금성대군 신단 해금

1. **소수서원 미션 완료** → 밀서 #1
2. **소수박물관 미션 완료** (답: 1909) → 밀서 #2
3. **선비촌 미션 완료** (답: 일편단심) → 밀서 #3

4. **금성대군 신단 자동 해금**
   ```
   GET /api/v1/users/1
   // isGoldShrineUnlocked: true
   ```

5. **신단 미션 조회**
   ```
   GET /api/v1/missions/location/GOLD_SHRINE?userId=1
   ```

6. **신단 미션 완료** (답: 이보흠)

7. **히든 장소 해금 확인**
   ```
   GET /api/v1/locations/hidden?userId=1
   // 무섬마을, 부석사 활성화
   ```

---

### 시나리오 3: 야간 히든 미션

1. **순흥향교 접근 시도 (20시 이전)**
   ```
   GET /api/v1/missions/location/SUNHEUNG_HYANGGYO?userId=1
   ```
   
   **오류 응답:**
   ```json
   {
     "success": false,
     "message": "이 장소는 밤 8시 이후에만 이용 가능합니다.",
     "errorCode": "L002"
   }
   ```

2. **20시 이후 재접근**
   ```
   GET /api/v1/missions/location/SUNHEUNG_HYANGGYO?userId=1
   // 정상 조회
   ```

---

## 🛠️ 관리자 기능

### 1. 공공데이터 동기화

```http
# 맛집 데이터 동기화
POST /api/v1/restaurants/sync

# 숙박시설 데이터 동기화
POST /api/v1/accommodations/sync
```

---

## ⚠️ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| U001 | 사용자를 찾을 수 없습니다 | 존재하지 않는 userId |
| U002 | 이미 사용 중인 닉네임입니다 | 닉네임 중복 |
| M001 | 미션을 찾을 수 없습니다 | 존재하지 않는 missionId |
| M002 | 이미 완료한 미션입니다 | 중복 완료 시도 |
| M003 | 정답이 아닙니다 | 오답 제출 |
| M004 | 아직 해금되지 않은 미션입니다 | 선행 조건 미달 |
| L001 | 장소를 찾을 수 없습니다 | 존재하지 않는 locationId |
| L002 | 밤 8시 이후에만 이용 가능합니다 | 순흥향교 시간 제약 |
| C001 | 부적절한 메시지가 감지되었습니다 | 욕설/비하 필터링 |

---

## 💡 Tips

### 1. GPS 좌표 예시
- **소수서원**: (36.9956, 128.6289)
- **소수박물관**: (36.9961, 128.6295)
- **소수 선비촌**: (36.9945, 128.6310)

### 2. 맛집 가중치 시스템
- **4개 배지**: 최우선 노출 (페르소나 강추 🌟)
- **3개 배지**: 높은 신뢰도
- **2개 배지**: 권장
- **1개 배지**: 일반

### 3. LLM 대화 팁
- 긍정적인 단어 사용 시 주막에서 보너스 포인트
- 무례한 발언 시 대화 강제 종료
- 대화 이력이 컨텍스트로 유지됨

---

**🎮 즐거운 영주 탐방 되세요!**
