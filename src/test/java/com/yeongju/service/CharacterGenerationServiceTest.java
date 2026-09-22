package com.yeongju.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;
@Disabled("CI 환경에서는 가짜 AWS 키를 사용하므로 실제 S3 연동 테스트는 제외한다.")

@SpringBootTest
public class CharacterGenerationServiceTest {
    
    @Autowired
    private S3Client s3Client;
    
    @Autowired
    private S3Service s3Service;
    
    @Value("${app.aws.s3.bucket}")
    private String bucketName;
    
    @Value("${app.aws.s3.region}")
    private String region;
    
    /**
     * S3 연결 테스트
     */
    @Test
    public void testS3Connection() {
        System.out.println("=".repeat(60));
        System.out.println("S3 연결 테스트");
        System.out.println("=".repeat(60));
        System.out.println("버킷: " + bucketName);
        System.out.println("리전: " + region);
        
        try {
            // 버킷 존재 확인
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            
            s3Client.headBucket(headBucketRequest);
            System.out.println("✅ S3 버킷 접근 성공!");
            
        } catch (NoSuchBucketException e) {
            fail("❌ 버킷을 찾을 수 없습니다: " + bucketName);
        } catch (Exception e) {
            fail("❌ S3 연결 실패: " + e.getMessage());
        }
    }
    
    /**
     * S3 업로드/다운로드/삭제 테스트
     */
    @Test
    public void testS3UploadAndDelete() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("S3 업로드/다운로드/삭제 테스트");
        System.out.println("=".repeat(60));
        
        String testKey = "characters/test_" + System.currentTimeMillis() + ".txt";
        byte[] testData = "Test character data".getBytes();
        
        try {
            // 1. 업로드
            System.out.println("📤 테스트 파일 업로드 중...");
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .contentType("text/plain")
                    .build();
            
            s3Client.putObject(putRequest, RequestBody.fromBytes(testData));
            
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, region, testKey);
            System.out.println("✅ 업로드 성공!");
            System.out.println("🔗 URL: " + fileUrl);
            
            // 2. 다운로드
            System.out.println("\n📥 업로드된 파일 확인 중...");
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build();
            
            byte[] downloadedData = s3Client.getObject(getRequest).readAllBytes();
            System.out.println("✅ 다운로드 성공! 크기: " + downloadedData.length + " bytes");
            
            assertArrayEquals(testData, downloadedData, "업로드/다운로드 데이터 일치");
            
            // 3. 삭제
            System.out.println("\n🗑️ 테스트 파일 삭제 중...");
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(testKey)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
            System.out.println("✅ 삭제 완료!");
            
        } catch (Exception e) {
            fail("❌ S3 작업 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * S3Service를 통한 이미지 업로드 테스트
     */
    @Test
    public void testS3ServiceImageUpload() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("S3Service 이미지 업로드 테스트");
        System.out.println("=".repeat(60));
        
        // 간단한 테스트 이미지 데이터 (실제로는 PNG/JPEG 바이트)
        byte[] testImageData = new byte[1024];
        for (int i = 0; i < testImageData.length; i++) {
            testImageData[i] = (byte) (i % 256);
        }
        
        try {
            String filename = "test_character_" + System.currentTimeMillis() + ".png";
            String folder = "characters";
            
            System.out.println("📤 이미지 업로드 중...");
            System.out.println("파일명: " + filename);
            System.out.println("폴더: " + folder);
            System.out.println("크기: " + testImageData.length + " bytes");
            
            String imageUrl = s3Service.uploadBytes(testImageData, filename, "image/png", folder);
            
            System.out.println("✅ S3Service 업로드 성공!");
            System.out.println("🔗 이미지 URL: " + imageUrl);
            
            assertNotNull(imageUrl, "이미지 URL이 null이 아니어야 함");
            assertTrue(imageUrl.contains(bucketName), "URL에 버킷 이름 포함");
            assertTrue(imageUrl.contains(folder), "URL에 폴더 이름 포함");
            
            // 업로드 확인
            boolean exists = s3Service.fileExists(imageUrl);
            assertTrue(exists, "업로드된 파일이 S3에 존재해야 함");
            
            // 정리 - 테스트 파일 삭제
            System.out.println("\n🗑️ 테스트 파일 정리 중...");
            s3Service.deleteFile(imageUrl);
            System.out.println("✅ 정리 완료!");
            
        } catch (Exception e) {
            fail("❌ S3Service 업로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 전체 통합 테스트 정보 출력
     */
    @Test
    public void printTestInfo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 캐릭터 생성 API 설정 정보");
        System.out.println("=".repeat(60));
        System.out.println("S3 버킷: " + bucketName);
        System.out.println("S3 리전: " + region);
        System.out.println("S3 클라이언트: " + (s3Client != null ? "✅ 주입됨" : "❌ null"));
        System.out.println("S3 서비스: " + (s3Service != null ? "✅ 주입됨" : "❌ null"));
        System.out.println("=".repeat(60));
    }
}
