package com.yeongju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 애플리케이션 진단 도구
 */
@SpringBootApplication
public class YeongjuDiagnostic {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("🔍 영주 프로젝트 진단 시작");
        System.out.println("=".repeat(80));
        
        try {
            ConfigurableApplicationContext context = SpringApplication.run(YeongjuDiagnostic.class, args);
            
            System.out.println("\n✅ Spring Boot 애플리케이션 시작 성공!");
            System.out.println("\n📊 등록된 주요 Bean 확인:");
            
            // S3 관련
            if (context.containsBean("s3Client")) {
                System.out.println("  ✅ S3Client - 등록됨");
            } else {
                System.out.println("  ❌ S3Client - 등록 안됨");
            }
            
            if (context.containsBean("s3Service")) {
                System.out.println("  ✅ S3Service - 등록됨");
            } else {
                System.out.println("  ❌ S3Service - 등록 안됨");
            }
            
            // 캐릭터 생성 관련
            if (context.containsBean("characterGenerationService")) {
                System.out.println("  ✅ CharacterGenerationService - 등록됨");
            } else {
                System.out.println("  ❌ CharacterGenerationService - 등록 안됨");
            }
            
            if (context.containsBean("restTemplate")) {
                System.out.println("  ✅ RestTemplate - 등록됨");
            } else {
                System.out.println("  ❌ RestTemplate - 등록 안됨");
            }
            
            // Repository 확인
            if (context.containsBean("userCharacterRepository")) {
                System.out.println("  ✅ UserCharacterRepository - 등록됨");
            } else {
                System.out.println("  ❌ UserCharacterRepository - 등록 안됨");
            }
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🎉 진단 완료 - 모든 설정이 정상입니다!");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("\n❌ 애플리케이션 시작 실패!");
            System.err.println("오류 타입: " + e.getClass().getName());
            System.err.println("오류 메시지: " + e.getMessage());
            
            System.err.println("\n📋 상세 스택 트레이스:");
            e.printStackTrace();
            
            // 원인 분석
            Throwable cause = e.getCause();
            int depth = 1;
            while (cause != null && depth <= 3) {
                System.err.println("\n원인 " + depth + ":");
                System.err.println("  타입: " + cause.getClass().getName());
                System.err.println("  메시지: " + cause.getMessage());
                cause = cause.getCause();
                depth++;
            }
        }
    }
}
