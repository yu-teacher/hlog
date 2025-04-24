package com.hunnit_beasts.hlog.user.infrastructure.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Log4j2
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    public JwtUtil(
            @Value("${jwt.expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time:604800000}") long refreshTokenExpTime // 7일 기본값
    ){
        // 환경 변수에서 시크릿 키 로드 또는 대체 값 사용
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            jwtSecret = "defaultSecretKeyForDevelopmentEnvironmentOnly12345678901234567890";
            log.warn("JWT_SECRET environment variable not set. Using default secret key for development only.");
        }

        this.secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    /**
     * CustomUserInfoDTO 기반으로 액세스 토큰 생성
     */
    public String createToken(CustomUserInfoDTO user){
        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpTime))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 스프링 시큐리티 Authentication 기반으로 액세스 토큰 생성
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = "USER";
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            role = "ADMIN";
        }

        return Jwts.builder()
                .claim("username", userDetails.getUsername())
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpTime))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(CustomUserInfoDTO user) {
        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("tokenType", "refresh")
                .id(UUID.randomUUID().toString()) // jti claim 추가
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpTime))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 토큰에서 username으로 UserId 추출
     */
    public Username getUsername(String token) {
        String username = getUsernameFromToken(token);

        try {
            return Username.of(username);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot convert username to UserId: " + username);
        }
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        String username = (String) claims.get("username");
        if (username == null) {
            username = claims.getSubject();
        }
        return username;
    }

    /**
     * 토큰에서 역할 추출
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return (String) claims.get("role");
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token).getPayload();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT Claims String is Empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰 파싱
     */
    public Claims parseClaims(String token) {
        try {
            String parsedToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            return getJwtParser()
                    .parseSignedClaims(parsedToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 클레임도 반환하여 필요한 정보를 추출할 수 있게 함
            return e.getClaims();
        }
    }

    /**
     * JWT 파서 반환
     */
    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }
}