package com.hunnit_beasts.hlog.user.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 인증이 필요없는 경로 확인 및 OPTION 요청 처리
        String path = request.getRequestURI();
        if (isPublicEndpoint(path) || request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Authorization 헤더 체크
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 토큰 추출 및 검증
        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            handleInvalidToken(response, "Invalid or expired token");
            return;
        }

        try {
            // 4. 토큰에서 사용자 ID 추출
            Username username = jwtUtil.getUsername(token);

            // 5. 사용자 정보 로드
            CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                handleInvalidToken(response, "User not found");
                return;
            }

            // 6. 사용자가 활성화 상태인지 확인
            if (!userDetails.isEnabled()) {
                handleInvalidToken(response, "User account is disabled");
                return;
            }

            // 7. 인증 객체 생성 및 SecurityContext에 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 요청 속성에 사용자 ID 추가 (컨트롤러에서 접근 가능)
            request.setAttribute("username", username.getValue());

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            handleInvalidToken(response, "Authentication failed: " + e.getMessage());
            return;
        }

        // 8. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 공개 엔드포인트인지 확인
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/error");
    }

    /**
     * 유효하지 않은 토큰 처리
     */
    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}
