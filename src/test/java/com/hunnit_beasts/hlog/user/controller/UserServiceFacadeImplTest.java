package com.hunnit_beasts.hlog.user.controller;

import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacadeImpl;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceFacadeImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserServiceFacadeImpl userServiceFacade;

    private UserDto userDto;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = "550e8400-e29b-41d4-a716-446655440000";
        userDto = new UserDto(userId, "test@example.com", "testuser", "ACTIVE", new HashSet<>(List.of("USER")), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자 ID로 존재 여부 확인 - 존재함")
    void existsById_UserExists() {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);

        // when
        boolean result = userServiceFacade.existsById(userId);

        // then
        assertThat(result).isTrue();
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 ID로 존재 여부 확인 - 존재하지 않음")
    void existsById_UserNotExists() {
        // given
        given(userService.getUserById(userId)).willThrow(new IllegalArgumentException("User not found"));

        // when
        boolean result = userServiceFacade.existsById(userId);

        // then
        assertThat(result).isFalse();
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 ID로 사용자 정보 조회")
    void getUserById() {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);

        // when
        UserDto result = userServiceFacade.getUserById(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 ID로 사용자명 조회")
    void getUsernameById() {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);

        // when
        String result = userServiceFacade.getUsernameById(userId);

        // then
        assertThat(result).isEqualTo("testuser");
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 권한 확인 - 권한 있음 (일반)")
    void hasPermission_HasUserPermission() {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);

        // when
        boolean result = userServiceFacade.hasPermission(userId, "USER");

        // then
        assertThat(result).isTrue();
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 권한 확인 - 권한 없음")
    void hasPermission_NoPermission() {
        // given
        given(userService.getUserById(userId)).willReturn(userDto);

        // when
        boolean result = userServiceFacade.hasPermission(userId, "EDITOR");

        // then
        assertThat(result).isFalse();
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("사용자 권한 확인 - 권한 있음 (관리자)")
    void hasPermission_AdminHasAllPermissions() {
        // given
        UserDto adminDto = new UserDto(userId, "admin@example.com", "admin", "ACTIVE", new HashSet<>(List.of("ADMIN")), LocalDateTime.now(), LocalDateTime.now() );

        given(userService.getUserById(userId)).willReturn(adminDto);

        // when
        boolean result = userServiceFacade.hasPermission(userId, "ANY_PERMISSION");

        // then
        assertThat(result).isTrue();
        verify(userService, times(1)).getUserById(userId);
    }
}