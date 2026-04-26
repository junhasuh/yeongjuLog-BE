package com.yeongju.service;

import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    
    private final S3Client s3Client;
    
    @Value("${app.aws.s3.bucket}")
    private String bucketName;
    
    @Value("${spring.cloud.aws.region.static}")
    private String region;
    
    /**
     * MultipartFile을 S3에 업로드
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = generateFileName(file.getOriginalFilename(), folder);
            String contentType = file.getContentType();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String fileUrl = getFileUrl(fileName);
            log.info("S3 파일 업로드 성공 - URL: {}", fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패", e);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }
    
    /**
     * byte[] 데이터를 S3에 업로드 (AI 생성 이미지용)
     */
    public String uploadBytes(byte[] data, String fileName, String contentType, String folder) {
        try {
            String fullFileName = folder + "/" + fileName;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullFileName)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
            
            String fileUrl = getFileUrl(fullFileName);
            log.info("S3 바이트 업로드 성공 - URL: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("S3 바이트 업로드 실패", e);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }
    
    /**
     * S3에서 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("S3 파일 삭제 성공 - fileName: {}", fileName);
            
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 - fileUrl: {}", fileUrl, e);
            throw new BusinessException(ErrorCode.S3_DELETE_FAILED);
        }
    }
    
    /**
     * 파일명 생성 (UUID 사용)
     */
    private String generateFileName(String originalFileName, String folder) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        return folder + "/" + uniqueFileName;
    }
    
    /**
     * S3 파일 URL 생성
     */
    private String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", 
                bucketName, region, fileName);
    }
    
    /**
     * URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        // https://bucket-name.s3.region.amazonaws.com/folder/filename.png
        // -> folder/filename.png
        String baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", 
                bucketName, region);
        return fileUrl.replace(baseUrl, "");
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.headObject(headObjectRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("S3 파일 존재 확인 실패", e);
            return false;
        }
    }
}
