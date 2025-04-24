package com.hunnit_beasts.hlog.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.user.api.dto.UserResponse;
import com.hunnit_beasts.hlog.user.api.dto.UserUpdateRequest;
import com.hunnit_beasts.hlog.user.api.mapper.UserApiMapper;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserApiMapper userApiMapper;

    private UserDto userDto;
    private UserResponse userResponse;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "550e8400-e29b-41d4-a716-446655440000";

        // 테스트용 UserDto 설정
        userDto = new UserDto(userId, "test@example.com", "testuser", "ACTIVE", new HashSet<>(List.of("USER")), LocalDateTime.now(), LocalDateTime.now());

        // 테스트용 UserResponse 설정
        userResponse = new UserResponse(userId, "test@example.com", "testuser", "ACTIVE", new HashSet<>(List.of("USER")), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 ID로 사용자 조회 - 성공")
    void getUserById_Success() throws Exception {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);
        given(userApiMapper.toResponse(userDto)).willReturn(userResponse);

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 ID로 사용자 조회 - 실패 (사용자 없음)")
    void getUserById_NotFound() throws Exception {
        // given
        given(userService.getUserById(userId)).willThrow(new IllegalArgumentException("User not found with id: " + userId));

        // when & then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));
    }

    @Test
    @WithMockUser
    @DisplayName("모든 사용자 조회")
    void getAllUsers() throws Exception {
        // given
        UserDto user2 = new UserDto("550e8400-e29b-41d4-a716-446655440001","user2@example.com","user2","ACTIVE",new HashSet<>(List.of("USER")),LocalDateTime.now(),LocalDateTime.now());

        List<UserDto> userDtos = Arrays.asList(userDto, user2);

        UserResponse user2Response = new UserResponse();
        user2Response.setId(user2.getId());
        user2Response.setEmail(user2.getEmail());
        user2Response.setUsername(user2.getUsername());
        user2Response.setStatus(user2.getStatus());
        user2Response.setRoles(user2.getRoles());

        List<UserResponse> userResponses = Arrays.asList(userResponse, user2Response);

        given(userService.getAllUsers()).willReturn(userDtos);
        given(userApiMapper.toResponse(userDto)).willReturn(userResponse);
        given(userApiMapper.toResponse(user2)).willReturn(user2Response);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[1].id").value("550e8400-e29b-41d4-a716-446655440001"));
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 정보 업데이트 - 성공")
    void updateUser_Success() throws Exception {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("updated@example.com", "updateduser",null);

        UpdateUserDto updateUserDto = new UpdateUserDto("updated@example.com","updateduser",null);

        UserDto updatedUserDto = new UserDto(userId, "updated@example.com", "updateduser", "ACTIVE", new HashSet<>(List.of("USER")), LocalDateTime.now(), LocalDateTime.now());

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(userId);
        updatedResponse.setEmail("updated@example.com");
        updatedResponse.setUsername("updateduser");
        updatedResponse.setStatus("ACTIVE");
        updatedResponse.setRoles(userDto.getRoles());

        given(userApiMapper.toUpdateDto(any(UserUpdateRequest.class))).willReturn(updateUserDto);
        given(userService.updateUser(eq(userId), any(UpdateUserDto.class))).willReturn(updatedUserDto);
        given(userApiMapper.toResponse(updatedUserDto)).willReturn(updatedResponse);

        // when & then
        mockMvc.perform(put("/api/users/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() throws Exception {
        // given
        doNothing().when(userService).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 삭제 - 실패 (사용자 없음)")
    void deleteUser_NotFound() throws Exception {
        // given
        doThrow(new IllegalArgumentException("User not found with id: " + userId))
                .when(userService).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));
    }

    @Test
    @WithMockUser
    @DisplayName("사용자 상태 변경 - 성공")
    void changeUserStatus_Success() throws Exception {
        // given
        UserDto statusChangedDto = new UserDto(userId, userDto.getEmail(), userDto.getUsername(), "INACTIVE", userDto.getRoles(), LocalDateTime.now(), LocalDateTime.now());

        UserResponse statusChangedResponse = new UserResponse();
        statusChangedResponse.setId(userId);
        statusChangedResponse.setEmail(userDto.getEmail());
        statusChangedResponse.setUsername(userDto.getUsername());
        statusChangedResponse.setStatus("INACTIVE");
        statusChangedResponse.setRoles(userDto.getRoles());

        given(userService.changeUserStatus(userId, "INACTIVE")).willReturn(statusChangedDto);
        given(userApiMapper.toResponse(statusChangedDto)).willReturn(statusChangedResponse);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/status", userId)
                        .with(csrf())
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("사용자에게 역할 추가 - 성공")
    void addRoleToUser_Success() throws Exception {
        // given
        String roleName = "ADMIN";

        UserDto roleAddedDto = new UserDto(userId, userDto.getEmail(), userDto.getUsername(), userDto.getStatus(), new HashSet<>(Arrays.asList("USER", "ADMIN")), LocalDateTime.now(), LocalDateTime.now());


        UserResponse roleAddedResponse = new UserResponse();
        roleAddedResponse.setId(userId);
        roleAddedResponse.setEmail(userDto.getEmail());
        roleAddedResponse.setUsername(userDto.getUsername());
        roleAddedResponse.setStatus(userDto.getStatus());
        roleAddedResponse.setRoles(new HashSet<>(Arrays.asList("USER", "ADMIN")));

        given(userService.addRoleToUser(userId, roleName)).willReturn(roleAddedDto);
        given(userApiMapper.toResponse(roleAddedDto)).willReturn(roleAddedResponse);

        // when & then
        mockMvc.perform(post("/api/users/{userId}/roles/{roleName}", userId, roleName)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[?(@=='ADMIN')]").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("사용자에게서 역할 제거 - 성공")
    void removeRoleFromUser_Success() throws Exception {
        // given
        String roleName = "USER";
        UserDto initialDto = new UserDto(userId, userDto.getEmail(), userDto.getUsername(), userDto.getStatus(), new HashSet<>(Arrays.asList("USER", "ADMIN")), LocalDateTime.now(), LocalDateTime.now());

        UserDto roleRemovedDto = new UserDto(userId, userDto.getEmail(), userDto.getUsername(), userDto.getStatus(), new HashSet<>(List.of("ADMIN")), LocalDateTime.now(), LocalDateTime.now());

        UserResponse roleRemovedResponse = new UserResponse();
        roleRemovedResponse.setId(userId);
        roleRemovedResponse.setEmail(userDto.getEmail());
        roleRemovedResponse.setUsername(userDto.getUsername());
        roleRemovedResponse.setStatus(userDto.getStatus());
        roleRemovedResponse.setRoles(new HashSet<>(List.of("ADMIN")));

        given(userService.removeRoleFromUser(userId, roleName)).willReturn(roleRemovedDto);
        given(userApiMapper.toResponse(roleRemovedDto)).willReturn(roleRemovedResponse);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}/roles/{roleName}", userId, roleName)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[?(@=='USER')]").doesNotExist())
                .andExpect(jsonPath("$.roles[?(@=='ADMIN')]").exists());
    }
}
