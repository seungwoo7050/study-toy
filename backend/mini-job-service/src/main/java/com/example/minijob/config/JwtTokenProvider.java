// [FILE]
// - 목적: JWT 토큰 생성 및 검증
// - 주요 역할: JWT 토큰 발급, 파싱, 유효성 검증
// - 관련 토이 버전: [BE-v0.6]
// - 권장 읽는 순서: createToken() → validateToken() → getEmail()
//
// [LEARN] JWT(Json Web Token)는 stateless 인증에 사용된다.
//         서버가 세션을 저장하지 않아도 토큰으로 인증할 수 있다.

package com.example.minijob.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// [Order 1] JWT 토큰 제공자
// - 토이 버전: [BE-v0.6]+
// [LEARN] HS256 알고리즘을 사용하여 토큰을 서명한다.
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:mySecretKeyForJwtTokenGenerationMinimum256BitsLongForHS256Algorithm}")
    private String secretString;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey secretKey;

    @PostConstruct
    // [LEARN] 빈 생성 직후 초기화 로직은 @PostConstruct로 실행되며,
    //         이곳에서 SecretKey 같은 필수 자원을 준비합니다.
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    // [Order 2] 토큰 생성
    // - email과 role을 포함하여 토큰 생성
    // [LEARN] Claims에 사용자 정보를 포함한다.
    //         만료 시간을 설정하여 보안을 강화한다.
    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // [Order 3] 토큰 검증
    // [LEARN] 서명 검증과 만료 시간을 확인한다.
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // [Order 4] 토큰에서 이메일 추출
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // [Order 5] 토큰에서 역할 추출
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}
