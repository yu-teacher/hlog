package com.hunnit_beasts.hlog.user.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserInfoDTO;
import com.hunnit_beasts.hlog.user.infrastructure.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private String testSecret;
    private SecretKey secretKey;
    private long accessTokenExpTime;
    private long refreshTokenExpTime;
    private UUID testUuid;
    private CustomUserInfoDTO customUserInfoDTO;

    @BeforeEach
    void setUp() {
        testSecret = "testSecretKey12345678901234567890123456789012";
        secretKey = new SecretKeySpec(testSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        accessTokenExpTime = 3600000; // 1 hour
        refreshTokenExpTime = 86400000; // 1 day
        testUuid = UUID.randomUUID();

        // JwtUtil 인스턴스 직접 생성
        jwtUtil = new JwtUtil(refreshTokenExpTime,refreshTokenExpTime);  // 만약 매개변수가 필요하면 여기에 추가하세요

        // JwtUtil에 필요한 필드 설정 - 이 부분은 그대로 유지
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpTime", accessTokenExpTime);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpTime", refreshTokenExpTime);

        // 테스트용 CustomUserInfoDTO 생성 - 이 부분도 그대로 유지
        customUserInfoDTO = CustomUserInfoDTO.builder()
                .username("user_name")
                .userId(testUuid.toString())
                .email("test@example.com")
                .role(CustomUserInfoDTO.Role.USER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("CustomUserInfoDTO로 토큰 생성 테스트")
    void createToken_WithCustomUserInfoDTO() {
        // when
        String token = jwtUtil.createToken(customUserInfoDTO);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // 토큰 검증
        Claims claims = jwtUtil.parseClaims(token);
        System.out.println("claims : " + claims);
        assertThat(claims.get("username")).isEqualTo("user_name");
        assertThat(claims.get("role")).isEqualTo("USER");

        // 만료 시간 검증
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        long expectedExpirationTime = issuedAt.getTime() + accessTokenExpTime;
        long actualExpirationTime = expiration.getTime();
        // 1초 이내 오차 허용
        assertThat(Math.abs(expectedExpirationTime - actualExpirationTime)).isLessThan(1000);
    }

    @Test
    @DisplayName("Authentication으로 토큰 생성 테스트")
    void generateToken_WithAuthentication() {
        // given
        UserDetails userDetails = User.builder()
                .username("username")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(userDetails);

        // when
        String token = jwtUtil.generateToken(authentication);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // 토큰 검증
        Claims claims = jwtUtil.parseClaims(token);
        assertThat(claims.get("username")).isEqualTo("username");
        assertThat(claims.get("role")).isEqualTo("USER");
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createRefreshToken() {
        // when
        String refreshToken = jwtUtil.createRefreshToken(customUserInfoDTO);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();

        // 토큰 검증
        Claims claims = jwtUtil.parseClaims(refreshToken);
        assertThat(claims.get("username")).isEqualTo("user_name");
        assertThat(claims.get("tokenType")).isEqualTo("refresh");
        assertThat(claims.getId()).isNotNull(); // jti claim 확인

        // 만료 시간 검증
        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        long expectedExpirationTime = issuedAt.getTime() + refreshTokenExpTime;
        long actualExpirationTime = expiration.getTime();
        // 1초 이내 오차 허용
        assertThat(Math.abs(expectedExpirationTime - actualExpirationTime)).isLessThan(1000);
    }

    @Test
    @DisplayName("토큰에서 사용자 ID 추출 테스트")
    void getUsername_FromToken() {
        // given
        String token = jwtUtil.createToken(customUserInfoDTO);

        // when
        Username username = jwtUtil.getUsername(token);

        // then
        assertThat(username).isNotNull();
        assertThat(username.getValue()).isEqualTo("user_name");
    }

    @Test
    @DisplayName("토큰에서 사용자명 추출 테스트")
    void getUsernameFromToken() {
        // given
        UserDetails userDetails = User.builder()
                .username(testUuid.toString())
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(userDetails);

        String token = jwtUtil.generateToken(authentication);

        // when
        String username = jwtUtil.getUsernameFromToken(token);

        // then
        assertThat(username).isEqualTo(testUuid.toString());
    }

    @Test
    @DisplayName("유효한 토큰 검증 테스트")
    void validateToken_ValidToken() {
        // given
        String token = jwtUtil.createToken(customUserInfoDTO);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 테스트")
    void validateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validateToken_ExpiredToken() {
        // given
        // 임시로 만료 시간을 -1시간으로 설정
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpTime", -3600000);

        String expiredToken = jwtUtil.createToken(customUserInfoDTO);

        // 다시 원래 설정으로 복원
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpTime", accessTokenExpTime);

        // when
        boolean isValid = jwtUtil.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }
}
