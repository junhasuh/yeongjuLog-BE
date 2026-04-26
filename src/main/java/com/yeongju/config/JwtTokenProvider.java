package com.yeongju.config;

import com.yeongju.exception.BusinessException;
import com.yeongju.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성/검증 유틸.
 * <p>
 * - HS256 서명, 대칭키 방식.
 * - subject = userId, claim "provider" = 인증 제공자("LOCAL" | "KAKAO"), claim "nickname" = 사용자 닉네임.
 * - 검증 실패 시 BusinessException(INVALID_TOKEN | TOKEN_EXPIRED) throw.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secretString;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        byte[] keyBytes;
        try {
            // base64로 제공된 경우 우선 시도
            keyBytes = Decoders.BASE64.decode(secretString);
        } catch (Exception e) {
            // Base64 디코딩 실패 시 UTF-8로 처리
            keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            log.warn("JWT secret 이 32바이트 미만입니다. 배포 전 반드시 더 긴 키로 교체하세요.");
            // HS256 요구사항(최소 32바이트) 충족을 위해 패딩
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(Long userId, String nickname, String provider) {
        return createToken(userId, nickname, provider, accessTokenExpirationMs);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null, null, refreshTokenExpirationMs);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    private String createToken(Long userId, String nickname, String provider, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);

        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey, Jwts.SIG.HS256);

        if (nickname != null) builder.claim("nickname", nickname);
        if (provider != null) builder.claim("provider", provider);

        return builder.compact();
    }

    /**
     * 토큰에서 userId(sub) 추출. 유효하지 않으면 BusinessException throw.
     */
    public Long extractUserId(String token) {
        Claims claims = parseClaims(token);
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Claims parseClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return jws.getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * "Authorization: Bearer xxx" 헤더에서 토큰만 추출. 형식 어긋나면 null 반환.
     */
    public static String resolveBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) return null;
        if (!authorizationHeader.regionMatches(true, 0, "Bearer ", 0, 7)) return null;
        return authorizationHeader.substring(7).trim();
    }

}
