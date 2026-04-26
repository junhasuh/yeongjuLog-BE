package com.yeongju.config;

import com.yeongju.domain.location.Location;
import com.yeongju.domain.location.LocationType;
import com.yeongju.domain.mission.Mission;
import com.yeongju.domain.mission.MissionType;
import com.yeongju.domain.secretletter.SecretLetter;
import com.yeongju.repository.LocationRepository;
import com.yeongju.repository.MissionRepository;
import com.yeongju.repository.SecretLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 초기 데이터 로더
 * - 장소, 미션, 밀서 조각 데이터 초기화
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitDataLoader implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final MissionRepository missionRepository;
    private final SecretLetterRepository secretLetterRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (locationRepository.count() > 0) {
            log.info("초기 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("초기 데이터 로딩 시작...");

        initLocations();
        initMissions();
        initSecretLetters();

        log.info("초기 데이터 로딩 완료!");
    }

    /**
     * 장소 데이터 초기화
     */
    private void initLocations() {
        // 소수서원
        Location sosuSeowon = Location.builder()
                .name("소수서원")
                .type(LocationType.SOSU_SEOWON)
                .description("한국 최초의 사액서원. 정축지변의 아픔이 서린 곳.")
                .latitude(36.9956)
                .longitude(128.6289)
                .isHidden(false)
                .requiresNightTime(false)
                .build();
        locationRepository.save(sosuSeowon);

        // 소수박물관
        Location sosuMuseum = Location.builder()
                .name("소수박물관")
                .type(LocationType.SOSU_MUSEUM)
                .description("금성대군의 기록이 보존된 박물관")
                .latitude(36.9961)
                .longitude(128.6295)
                .isHidden(false)
                .requiresNightTime(false)
                .build();
        locationRepository.save(sosuMuseum);

        // 소수 선비촌
        Location sosuVillage = Location.builder()
                .name("소수 선비촌")
                .type(LocationType.SOSU_VILLAGE)
                .description("선비 정신을 계승한 전통 마을")
                .latitude(36.9945)
                .longitude(128.6310)
                .isHidden(false)
                .requiresNightTime(false)
                .build();
        locationRepository.save(sosuVillage);

        // 금성대군 신단
        Location goldShrine = Location.builder()
                .name("금성대군 신단")
                .type(LocationType.GOLD_SHRINE)
                .description("금성대군과 이보흠을 모신 제단")
                .latitude(36.9950)
                .longitude(128.6300)
                .isHidden(false)
                .requiresNightTime(false)
                .build();
        locationRepository.save(goldShrine);

        // 주막
        Location tavern = Location.builder()
                .name("비밀 주막")
                .type(LocationType.TAVERN)
                .description("언제나 이용 가능한 주막")
                .latitude(36.9940)
                .longitude(128.6285)
                .isHidden(false)
                .requiresNightTime(false)
                .build();
        locationRepository.save(tavern);

        // 무섬마을 (히든)
        Location museomVillage = Location.builder()
                .name("무섬마을")
                .type(LocationType.MUSEOM_VILLAGE)
                .description("내성천이 감싸는 독립운동의 성지")
                .latitude(36.9450)
                .longitude(128.5950)
                .isHidden(true)
                .requiresNightTime(false)
                .build();
        locationRepository.save(museomVillage);

        // 부석사 (히든)
        Location buseokTemple = Location.builder()
                .name("부석사")
                .type(LocationType.BUSEOK_TEMPLE)
                .description("무량수전의 배흘림기둥이 있는 천년 고찰")
                .latitude(36.9994)
                .longitude(128.6856)
                .isHidden(true)
                .requiresNightTime(false)
                .build();
        locationRepository.save(buseokTemple);

        // 순흥향교 (야간 히든)
        Location sunheungHyanggyo = Location.builder()
                .name("순흥향교")
                .type(LocationType.SUNHEUNG_HYANGGYO)
                .description("밤 8시 이후에만 열리는 비밀의 장소")
                .latitude(36.9965)
                .longitude(128.6275)
                .isHidden(true)
                .requiresNightTime(true)
                .build();
        locationRepository.save(sunheungHyanggyo);

        log.info("장소 데이터 초기화 완료: {} 개", locationRepository.count());
    }

    /**
     * 미션 데이터 초기화
     */
    private void initMissions() {
        Location sosuSeowon = locationRepository.findByType(LocationType.SOSU_SEOWON).orElseThrow();
        Location sosuMuseum = locationRepository.findByType(LocationType.SOSU_MUSEUM).orElseThrow();
        Location sosuVillage = locationRepository.findByType(LocationType.SOSU_VILLAGE).orElseThrow();
        Location goldShrine = locationRepository.findByType(LocationType.GOLD_SHRINE).orElseThrow();
        Location museomVillage = locationRepository.findByType(LocationType.MUSEOM_VILLAGE).orElseThrow();
        Location buseokTemple = locationRepository.findByType(LocationType.BUSEOK_TEMPLE).orElseThrow();

        // 소수서원 미션
        Mission sosuSeowonMission = Mission.builder()
                .location(sosuSeowon)
                .title("강학당에 숨겨진 충절의 조각")
                .question("정축년의 그날 밤... 내가 단종 임금을 위해 거사를 도모하던 이 찬란한 절터도 참화 속에 안개처럼 자취를 감추고 말았네. 비극 속에 잊힌 이 옛 절의 이름은 무엇인가?")
                .correctAnswer("숙수사")
                .rewardPoints(100)
                .successMessage("오오... 자네 덕분에 잊혔던 이름이 다시 빛을 발하는구먼. 고맙네, 선비여.")
                .type(MissionType.TEXT_INPUT)
                .displayOrder(1)
                .build();
        missionRepository.save(sosuSeowonMission);

        // 소수박물관 미션
        Mission sosuMuseumMission = Mission.builder()
                .location(sosuMuseum)
                .title("기록의 봉인을 풀어라")
                .question("금성대군의 행적을 기록한 이 문헌, <금성대군실기>의 소수박물관 소장품 번호는 무엇인가?")
                .correctAnswer("1909")
                .rewardPoints(100)
                .successMessage("기록이 다시 숨을 쉬기 시작했군! 자네 덕분에 내가 세상에 남긴 흔적을 다시 마주하게 되었네.")
                .type(MissionType.DATA_SEARCH)
                .displayOrder(1)
                .build();
        missionRepository.save(sosuMuseumMission);

        // 선비촌 미션
        Mission sosuVillageMission = Mission.builder()
                .location(sosuVillage)
                .title("선비촌의 암호를 풀어라")
                .question("이 마을에 복원된 고택들이 상징하는바, 권력 앞에서도 꺾이지 않고 오직 한 분의 임금만을 향했던 선비들의 변치 않는 마음은 무엇인가?")
                .correctAnswer("일편단심")
                .rewardPoints(100)
                .successMessage("허어... 정답이오! 자네 같은 이라면 이 소중한 조각을 맡겨도 마땅하오.")
                .type(MissionType.TEXT_INPUT)
                .displayOrder(1)
                .build();
        missionRepository.save(sosuVillageMission);

        // 금성대군 신단 미션
        Mission goldShrineMission = Mission.builder()
                .location(goldShrine)
                .title("최후의 제향 - 이름을 불러주오")
                .question("금성대군과 함께 단종 복위를 도모하다 희생된 동지이자, 이곳 신단에 함께 모셔진 '순흥부사'의 이름은 무엇인가?")
                .correctAnswer("이보흠")
                .rewardPoints(150)
                .successMessage("고맙네. 이제 정축년의 긴 밤을 끝내고 나는 떠나려 하네. 하지만 우리의 기개는 이 땅 영주에 영원히 흐를 것이야.")
                .type(MissionType.TEXT_INPUT)
                .displayOrder(1)
                .build();
        missionRepository.save(goldShrineMission);

        // 무섬마을 미션
        Mission museomVillageMission = Mission.builder()
                .location(museomVillage)
                .title("단절을 잇는 외나무다리")
                .question("일제강점기 무섬마을 주민들이 세워 항일 운동의 구심점이 되었던 교육 기관의 이름은 무엇인가?")
                .correctAnswer("아도서숙")
                .rewardPoints(120)
                .successMessage("정답이오! 아도서숙의 등불은 꺼지지 않았소. 자네처럼 역사를 기억하는 이가 있기에 영주의 정신은 계속되는 것이지.")
                .type(MissionType.TEXT_INPUT)
                .displayOrder(1)
                .build();
        missionRepository.save(museomVillageMission);

        // 부석사 미션
        Mission buseokTempleMission = Mission.builder()
                .location(buseokTemple)
                .title("무량수전의 배흘림기둥")
                .question("부석사 무량수전의 기둥은 중간이 볼록하게 솟아올라 시각적 안정감을 주는데, 이러한 양식을 무엇이라 부르는가?")
                .correctAnswer("배흘림")
                .rewardPoints(120)
                .successMessage("참으로 단단하고 곧은 마음을 가졌구려. 이 기둥이 천 년을 버텼듯, 자네가 확인한 그 기개 또한 영원할 것이오.")
                .type(MissionType.TEXT_INPUT)
                .displayOrder(1)
                .build();
        missionRepository.save(buseokTempleMission);

        log.info("미션 데이터 초기화 완료: {} 개", missionRepository.count());
    }

    /**
     * 밀서 조각 데이터 초기화
     */
    private void initSecretLetters() {
        // 밀서 #1
        SecretLetter letter1 = SecretLetter.builder()
                .sequenceNumber(1)
                .title("밀서 조각 #1")
                .content("피로 물든 죽계천의 한(恨)이...")
                .description("첫 번째 밀서 조각. 정축지변의 비극이 담겨있다.")
                .build();
        secretLetterRepository.save(letter1);

        // 밀서 #2
        SecretLetter letter2 = SecretLetter.builder()
                .sequenceNumber(2)
                .title("밀서 조각 #2")
                .content("백 년의 세월을 견뎌 꼿꼿한 기개로 피어나...")
                .description("두 번째 밀서 조각. 복설된 서원의 의미가 담겨있다.")
                .build();
        secretLetterRepository.save(letter2);

        // 밀서 #3
        SecretLetter letter3 = SecretLetter.builder()
                .sequenceNumber(3)
                .title("밀서 조각 #3")
                .content("마침내 신단(神壇)의 빛 아래 일편단심(一片丹心)으로 완성되리라.")
                .description("세 번째 밀서 조각. 금성대군의 성불을 예고한다.")
                .build();
        secretLetterRepository.save(letter3);

        log.info("밀서 조각 데이터 초기화 완료: {} 개", secretLetterRepository.count());
    }

}
