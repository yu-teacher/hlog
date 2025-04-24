package com.hunnit_beasts.hlog.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserStatus;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.infrastructure.security.*;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private String validToken;
    private UUID userId;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        // 테스트 전에 SecurityContext 초기화
        SecurityContextHolder.clearContext();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        validToken = "valid.jwt.token";
        userId = UUID.randomUUID();

        // CustomUserInfoDTO 생성
        CustomUserInfoDTO userInfoDTO = CustomUserInfoDTO.builder()
                .username("user_name")
                .userId(userId.toString())
                .email("test@example.com")
                .role(CustomUserInfoDTO.Role.USER)
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .build();

        // CustomUserDetails 생성
        userDetails = new CustomUserDetails(userInfoDTO);
    }

    @Test
    @DisplayName("공개 엔드포인트 요청은 필터 처리 건너뛰기")
    void doFilterInternal_PublicEndpoint() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/auth/login");

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        // 토큰 검증 메서드가 호출되지 않았는지 확인
        verify(jwtUtil, never()).validateToken(anyString());

        // 필터 체인이 계속 실행되었는지 확인
        assertThat(filterChain.getRequest()).isEqualTo(request);
        assertThat(filterChain.getResponse()).isEqualTo(response);
    }

    @Test
    @DisplayName("OPTIONS 요청은 필터 처리 건너뛰기")
    void doFilterInternal_OptionsRequest() throws ServletException, IOException {
        // given
        request.setMethod("OPTIONS");

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil, never()).validateToken(anyString());
        assertThat(filterChain.getRequest()).isEqualTo(request);
    }

    @Test
    @DisplayName("Authorization 헤더가 없는 경우")
    void doFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        // Authorization 헤더 없음

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil, never()).validateToken(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isEqualTo(request);
    }

    @Test
    @DisplayName("잘못된 Bearer 형식의 Authorization 헤더")
    void doFilterInternal_InvalidBearerFormat() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil, never()).validateToken(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isEqualTo(request);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰")
    void doFilterInternal_InvalidToken() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer " + validToken);

        given(jwtUtil.validateToken(validToken)).willReturn(false);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil, never()).getUsername(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // 대신 응답 상태 코드 확인
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증")
    void doFilterInternal_ValidToken() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer " + validToken);

        Username usernameObj = Username.of("user_name");

        given(jwtUtil.validateToken(validToken)).willReturn(true);
        given(jwtUtil.getUsername(validToken)).willReturn(usernameObj);
        given(customUserDetailsService.loadUserByUsername(usernameObj)).willReturn(userDetails);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsername(validToken);
        verify(customUserDetailsService).loadUserByUsername(usernameObj);

        // 인증 객체가 설정되었는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getAuthorities()).isEqualTo(userDetails.getAuthorities());

        // filterChain이 호출되었는지 확인 (이 경우는 호출되어야 함)
        assertThat(filterChain.getRequest()).isEqualTo(request);
        assertThat(response.getStatus()).isEqualTo(200); // 기본 상태 코드
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우")
    void doFilterInternal_UserNotFound() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer " + validToken);

        Username usernameObj = Username.of("user_name");

        given(jwtUtil.validateToken(validToken)).willReturn(true);
        given(jwtUtil.getUsername(validToken)).willReturn(usernameObj);
        given(customUserDetailsService.loadUserByUsername(usernameObj)).willReturn(null);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsername(validToken);
        verify(customUserDetailsService).loadUserByUsername(usernameObj);

        // 인증 객체가 설정되지 않았는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // 응답 상태 코드 확인 (401 Unauthorized)
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("비활성화된 사용자 계정")
    void doFilterInternal_DisabledUserAccount() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer " + validToken);

        Username usernameObj = Username.of("user_name");

        // 비활성화된 CustomUserInfoDTO 생성
        CustomUserInfoDTO disabledUserInfoDTO = CustomUserInfoDTO.builder()
                .username("disable_user_name")
                .userId(userId.toString())
                .email("test@example.com")
                .role(CustomUserInfoDTO.Role.USER)
                .enabled(false) // 비활성화 상태
                .build();

        // 비활성화된 CustomUserDetails 생성
        CustomUserDetails disabledUserDetails = new CustomUserDetails(disabledUserInfoDTO);

        given(jwtUtil.validateToken(validToken)).willReturn(true);
        given(jwtUtil.getUsername(validToken)).willReturn(usernameObj);
        given(customUserDetailsService.loadUserByUsername(usernameObj)).willReturn(disabledUserDetails);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsername(validToken);
        verify(customUserDetailsService).loadUserByUsername(usernameObj);

        // 인증 객체가 설정되지 않았는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // 응답 상태 코드 확인 (401 Unauthorized)
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("예외 발생 시 처리")
    void doFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // given
        request.setRequestURI("/api/users");
        request.addHeader("Authorization", "Bearer " + validToken);

        given(jwtUtil.validateToken(validToken)).willReturn(true);
        given(jwtUtil.getUsername(validToken)).willThrow(new IllegalArgumentException("Invalid token format"));

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).getUsername(validToken);

        // 인증 객체가 설정되지 않았는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // 응답 상태 코드 확인 (401 Unauthorized)
        assertThat(response.getStatus()).isEqualTo(401);
    }
}
