package com.hunnit_beasts.hlog.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.api.dto.UserLoginRequest;
import com.hunnit_beasts.hlog.user.api.dto.UserRegistrationRequest;
import com.hunnit_beasts.hlog.user.api.dto.UserResponse;
import com.hunnit_beasts.hlog.user.api.mapper.UserApiMapper;
import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import com.hunnit_beasts.hlog.user.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil tokenProvider;

    @MockitoBean
    private UserApiMapper userApiMapper;

    @Test
    @DisplayName("사용자 등록 - 성공")
    void registerUser() throws Exception {
        // given
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("test@example.com", "testuser", "password123");

        CreateUserDto createUserDto = new CreateUserDto("test@example.com", "testuser", "password123");

        UserDto createdUserDto = new UserDto("550e8400-e29b-41d4-a716-446655440000", "test@example.com", "testuser", "ACTIVE", new HashSet<>(Collections.singletonList("USER")), LocalDateTime.now(), LocalDateTime.now());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(createdUserDto.getId());
        userResponse.setEmail(createdUserDto.getEmail());
        userResponse.setUsername(createdUserDto.getUsername());
        userResponse.setStatus(createdUserDto.getStatus());
        userResponse.setRoles(createdUserDto.getRoles());
        userResponse.setCreatedAt(createdUserDto.getCreatedAt());
        userResponse.setUpdatedAt(createdUserDto.getUpdatedAt());

        // given()과 같은 문법으로 통일하고, 정확한 객체 참조 사용
        given(userApiMapper.toCreateDto(any(UserRegistrationRequest.class))).willReturn(createUserDto);
        given(userService.registerUser(any(CreateUserDto.class))).willReturn(createdUserDto);
        given(userApiMapper.toResponse(createdUserDto)).willReturn(userResponse);

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdUserDto.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("사용자 등록 - 성공")
    void registerUser_Success() throws Exception {
        // given
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("test@example.com", "testuser", "password123");

        CreateUserDto createUserDto = new CreateUserDto("test@example.com", "testuser", "password123");

        UserDto createdUserDto = new UserDto("550e8400-e29b-41d4-a716-446655440000", "test@example.com", "testuser", "ACTIVE", new HashSet<>(Collections.singletonList("USER")), LocalDateTime.now(), LocalDateTime.now());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(createdUserDto.getId());
        userResponse.setEmail(createdUserDto.getEmail());
        userResponse.setUsername(createdUserDto.getUsername());
        userResponse.setStatus(createdUserDto.getStatus());
        userResponse.setRoles(createdUserDto.getRoles());
        userResponse.setCreatedAt(createdUserDto.getCreatedAt());
        userResponse.setUpdatedAt(createdUserDto.getUpdatedAt());

        // given()과 같은 문법으로 통일하고, 정확한 객체 참조 사용
        given(userApiMapper.toCreateDto(any(UserRegistrationRequest.class))).willReturn(createUserDto);
        given(userService.registerUser(any(CreateUserDto.class))).willReturn(createdUserDto);
        given(userApiMapper.toResponse(createdUserDto)).willReturn(userResponse);

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdUserDto.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("사용자 등록 - 실패 (이메일 중복)")
    void registerUser_EmailAlreadyExists() throws Exception {
        // given
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("existing@example.com", "newuser", "password123");

        CreateUserDto createUserDto = new CreateUserDto("existing@example.com", "newuser", "password123");

        // 성공한 패턴과 동일하게 수정
        when(userApiMapper.toCreateDto(any(UserRegistrationRequest.class))).thenReturn(createUserDto);
        when(userService.registerUser(any(CreateUserDto.class)))
                .thenThrow(new IllegalArgumentException("Email already in use: existing@example.com"));

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Email already in use: existing@example.com"));
    }

    @Test
    @DisplayName("사용자 등록 - 실패 (사용자명 중복)")
    void registerUser_UsernameAlreadyExists() throws Exception {
        // given
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("new@example.com", "existinguser", "password123");

        CreateUserDto createUserDto = new CreateUserDto("new@example.com", "existinguser", "password123");

        // 성공한 패턴과 동일하게 수정
        when(userApiMapper.toCreateDto(any(UserRegistrationRequest.class))).thenReturn(createUserDto);
        when(userService.registerUser(any(CreateUserDto.class)))
                .thenThrow(new IllegalArgumentException("Username already in use: existinguser"));

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Username already in use: existinguser"));
    }

    @Test
    @DisplayName("사용자 로그인 - 성공")
    void authenticateUser_Success() throws Exception {
        // given
        UserLoginRequest loginRequest = new UserLoginRequest("testuser", "password123");

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("encoded_password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.signature";

        // 성공한 패턴과 동일하게 수정
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(jwtToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("사용자 로그인 - 유효성 검사 실패")
    void authenticateUser_ValidationFailure() throws Exception {
        // given
        UserLoginRequest loginRequest = new UserLoginRequest("","");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
//                .andExpect(jsonPath("$.errors").isArray())
//                .andExpect(jsonPath("$.errors.length()").value(2));
    }
}
