package com.yeongju;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 설정 값 검증 테스트
 */
@SpringBootTest
public class ConfigurationTest {
    
    @Value("${app.aws.s3.bucket}")
    private String s3Bucket;
    
    @Value("${app.aws.s3.region}")
    private String s3Region;
    
    @Value("${app.aws.s3.access-key}")
    private String s3AccessKey;
    
    @Value("${app.aws.s3.secret-key}")
    private String s3SecretKey;
    
    @Value("${app.huggingface.api-key}")
    private String hfApiKey;
    
    @Value("${app.huggingface.model}")
    private String hfModel;
    
    @Test
    public void testConfigurationValues() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🔧 설정 값 검증");
        System.out.println("=".repeat(70));
        
        // AWS S3 설정
        System.out.println("\n📦 AWS S3 설정:");
        System.out.println("  - Bucket: " + s3Bucket);
        System.out.println("  - Region: " + s3Region);
        System.out.println("  - Access Key: " + maskString(s3AccessKey));
        System.out.println("  - Secret Key: " + maskString(s3SecretKey));
        
        assertNotNull(s3Bucket, "S3 bucket이 설정되어야 함");
        assertNotNull(s3Region, "S3 region이 설정되어야 함");
        assertNotNull(s3AccessKey, "S3 access key가 설정되어야 함");
        assertNotNull(s3SecretKey, "S3 secret key가 설정되어야 함");
        assertFalse(s3AccessKey.isBlank(), "S3 access key가 비어있으면 안됨");
        assertFalse(s3SecretKey.isBlank(), "S3 secret key가 비어있으면 안됨");
        
        // Hugging Face 설정
        System.out.println("\n🤗 Hugging Face 설정:");
        System.out.println("  - API Key: " + maskString(hfApiKey));
        System.out.println("  - Model: " + hfModel);
        
        assertNotNull(hfApiKey, "Hugging Face API key가 설정되어야 함");
        assertNotNull(hfModel, "Hugging Face model이 설정되어야 함");
        assertFalse(hfApiKey.isBlank(), "Hugging Face API key가 비어있으면 안됨");
        assertTrue(hfApiKey.startsWith("hf_"), "Hugging Face API key는 'hf_'로 시작해야 함");
        
        System.out.println("\n✅ 모든 설정 값이 올바르게 로드되었습니다!");
        System.out.println("=".repeat(70) + "\n");
    }
    
    private String maskString(String value) {
        if (value == null || value.length() < 8) {
            return "****";
        }
        return value.substring(0, 4) + "..." + value.substring(value.length() - 4);
    }
}
