package com.hunnit_beasts.hlog.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.infrastructure.security.JwtAccessDeniedHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtAccessDeniedHandlerTest {

    // Mock 대신 실제 ObjectMapper 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    // InjectMocks 대신 직접 생성자로 주입
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private AccessDeniedException accessDeniedException;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        accessDeniedException = new AccessDeniedException("접근 권한 없음");
        jwtAccessDeniedHandler = new JwtAccessDeniedHandler(objectMapper);
    }

    @Test
    @DisplayName("접근 거부 시 403 응답 반환")
    void handle_ShouldReturn403Response() throws IOException {
        // given
        request.setRequestURI("/api/admin/users");

        // when
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        String responseContent = response.getContentAsString();

        assertThat(responseContent).contains("\"status\":403");
        assertThat(responseContent).contains("\"error\":\"Forbidden\"");
        assertThat(responseContent).contains("\"message\":\"접근 권한 없음\"");
        assertThat(responseContent).contains("\"path\":\"/api/admin/users\"");
    }

    @Test
    @DisplayName("예외 메시지가 null이면 기본 메시지 사용")
    void handle_WithNullExceptionMessage() throws IOException {
        // given
        accessDeniedException = new AccessDeniedException(null);

        // when
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        // 응답 본문 검증
        String responseContent = response.getContentAsString();

        assertThat(responseContent).contains("\"message\":\"You don't have permission to access this resource\"");
    }

    @Test
    @DisplayName("요청 URI가 응답에 포함되는지 확인")
    void handle_IncludesRequestURIInResponse() throws IOException {
        // given
        String requestURI = "/api/admin/settings";
        request.setRequestURI(requestURI);

        // when
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        // 응답 본문 검증
        String responseContent = response.getContentAsString();

        assertThat(responseContent).contains("\"path\":\"" + requestURI + "\"");
    }

    @Test
    @DisplayName("컨텐츠 타입이 JSON으로 설정되는지 확인")
    void handle_SetsContentTypeToJson() throws IOException {
        // when
        jwtAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }
}
