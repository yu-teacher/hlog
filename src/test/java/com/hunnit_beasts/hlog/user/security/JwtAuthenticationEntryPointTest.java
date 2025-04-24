package com.hunnit_beasts.hlog.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.infrastructure.security.JwtAuthenticationEntryPoint;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    // Mock 대신 실제 ObjectMapper 사용
    private ObjectMapper objectMapper = new ObjectMapper();

    // InjectMocks 대신 직접 생성자로 주입
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authException = new AuthenticationException("인증 실패") {};
        jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Test
    @DisplayName("인증 실패 시 401 응답 반환")
    void commence_ShouldReturn401Response() throws IOException, ServletException {
        // given
        request.setRequestURI("/api/users");

        // when
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        // 응답 본문을 확인
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("\"status\":401");
        assertThat(responseContent).contains("\"error\":\"Unauthorized\"");
        assertThat(responseContent).contains("\"message\":\"인증 실패\"");
        assertThat(responseContent).contains("\"path\":\"/api/users\"");
    }

    @Test
    @DisplayName("예외 메시지가 null이면 기본 메시지 사용")
    void commence_WithNullExceptionMessage() throws IOException, ServletException {
        // given
        authException = new AuthenticationException(null) {};

        // when
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        // 응답 본문 확인
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("\"message\":\"Authentication is required to access this resource\"");
    }

    @Test
    @DisplayName("요청 URI가 응답에 포함되는지 확인")
    void commence_IncludesRequestURIInResponse() throws IOException, ServletException {
        // given
        String requestURI = "/api/protected-resource";
        request.setRequestURI(requestURI);

        // when
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        // 응답 본문 확인
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("\"path\":\"" + requestURI + "\"");
    }
}
