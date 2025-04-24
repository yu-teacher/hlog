package com.hunnit_beasts.hlog.user.service;

import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UpdateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.mapper.UserDtoMapper;
import com.hunnit_beasts.hlog.user.application.service.UserServiceImpl;
import com.hunnit_beasts.hlog.user.domain.event.UserCreatedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserDeletedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserUpdatedEvent;
import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.Password;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.domain.repository.RoleRepository;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.domain.service.PasswordEncryptionService;
import com.hunnit_beasts.hlog.user.infrastructure.messaging.UserEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncryptionService passwordEncryptionService;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private UserEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role userRole;
    private Role adminRole;
    private UserDto userDto;
    private UserId userId;
    private Password rawPassword;
    private Password encryptedPassword;

    @BeforeEach
    void setUp() {
        // UUID
        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        userId = UserId.of(uuid);

        // 패스워드 세팅
        rawPassword = Password.of("password123");
        encryptedPassword = Password.ofEncrypted("encoded_password");

        // 유저 객체 생성
        user = User.create(
                Email.of("test@example.com"),
                Username.of("testuser"),
                rawPassword
        );

        // 리플렉션을 사용하여 private field 설정 (테스트용)
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, userId);

            java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(user, encryptedPassword);
        } catch (Exception e) {
            throw new RuntimeException("테스트 셋업 실패", e);
        }

        // Role 객체 생성
        userRole = Role.user();
        adminRole = Role.admin();

        // UserDto 생성
        userDto = new UserDto(userId.getValue().toString(), "test@example.com", "testuser", "ACTIVE", new HashSet<>(Collections.singletonList("USER")), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("새 사용자 등록 - 성공")
    void registerUser_Success() {
        // given
        CreateUserDto createUserDto = new CreateUserDto("test@example.com", "testuser", "password123");

        given(userRepository.existsByEmail(any(Email.class))).willReturn(false);
        given(userRepository.existsByUsername(any(Username.class))).willReturn(false);
        given(userDtoMapper.toEntity(createUserDto)).willReturn(user);
        given(passwordEncryptionService.encrypt(any(Password.class))).willReturn(encryptedPassword);
        given(userRepository.save(user)).willReturn(user);
        given(userDtoMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.registerUser(createUserDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByUsername(any(Username.class));
        verify(passwordEncryptionService).encrypt(any(Password.class));
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("새 사용자 등록 - 실패 (이메일 중복)")
    void registerUser_EmailAlreadyExists() {
        // given
        CreateUserDto createUserDto = new CreateUserDto("existing@example.com", "testuser", "password123");

        given(userRepository.existsByEmail(any(Email.class))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(createUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
        verify(eventPublisher, never()).publish(any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("새 사용자 등록 - 실패 (사용자명 중복)")
    void registerUser_UsernameAlreadyExists() {
        // given
        CreateUserDto createUserDto = new CreateUserDto("new@example.com", "existinguser", "password123");

        given(userRepository.existsByEmail(any(Email.class))).willReturn(false);
        given(userRepository.existsByUsername(any(Username.class))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(createUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already in use");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository, never()).save(any(User.class));
        verify(eventPublisher, never()).publish(any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 성공")
    void getUserById_Success() {
        // given
        String userIdStr = userId.getValue().toString();
        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        given(userDtoMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.getUserById(userIdStr);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userIdStr);
        verify(userRepository).findById(any(UserId.class));
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 실패 (사용자 없음)")
    void getUserById_UserNotFound() {
        // given
        String userIdStr = userId.getValue().toString();
        given(userRepository.findById(any(UserId.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userIdStr))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with id");

        verify(userRepository).findById(any(UserId.class));
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void getUserByEmail_Success() {
        // given
        String email = "test@example.com";
        given(userRepository.findByEmail(any(Email.class))).willReturn(Optional.of(user));
        given(userDtoMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.getUserByEmail(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("사용자명으로 사용자 조회 - 성공")
    void getUserByUsername_Success() {
        // given
        String username = "testuser";
        given(userRepository.findByUsername(any(Username.class))).willReturn(Optional.of(user));
        given(userDtoMapper.toDto(user)).willReturn(userDto);

        // when
        UserDto result = userService.getUserByUsername(username);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        verify(userRepository).findByUsername(any(Username.class));
    }

    @Test
    @DisplayName("사용자 정보 업데이트 - 성공")
    void updateUser_Success() {
        // given
        String userIdStr = userId.getValue().toString();
        UpdateUserDto updateUserDto = new UpdateUserDto("updated@example.com", "updateduser", "newpassword");

        Password newPassword = Password.of("newpassword");
        Password newEncryptedPassword = Password.ofEncrypted("new_encoded_password");

        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(any(Email.class))).willReturn(false);
        given(userRepository.existsByUsername(any(Username.class))).willReturn(false);
        given(passwordEncryptionService.encrypt(any(Password.class))).willReturn(newEncryptedPassword);
        given(userRepository.save(user)).willReturn(user);

        UserDto updatedUserDto = new UserDto(userIdStr, "updated@example.com", "updateduser", "ACTIVE", new HashSet<>(Collections.singletonList("USER")), LocalDateTime.now(), LocalDateTime.now());

        given(userDtoMapper.toDto(user)).willReturn(updatedUserDto);

        // when
        UserDto result = userService.updateUser(userIdStr, updateUserDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getUsername()).isEqualTo("updateduser");

        verify(userRepository).findById(any(UserId.class));
        verify(passwordEncryptionService).encrypt(any(Password.class));
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserUpdatedEvent.class));
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() {
        // given
        String userIdStr = userId.getValue().toString();
        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // when
        userService.deleteUser(userIdStr);

        // then
        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).delete(user);

        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        UserDeletedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void getAllUsers() {
        // given
        User user2 = User.create(
                Email.of("user2@example.com"),
                Username.of("user2"),
                Password.of("password")
        );

        List<User> users = Arrays.asList(user, user2);

        UserDto user2Dto = new UserDto("another-id", "user2@example.com", "user2", "ACTIVE", new HashSet<>(Collections.singletonList("USER")), LocalDateTime.now(), LocalDateTime.now());

        given(userRepository.findAll()).willReturn(users);
        given(userDtoMapper.toDto(user)).willReturn(userDto);
        given(userDtoMapper.toDto(user2)).willReturn(user2Dto);

        // when
        List<UserDto> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@example.com");

        verify(userRepository).findAll();
        verify(userDtoMapper, times(2)).toDto(any(User.class));
    }

    @Test
    @DisplayName("사용자 상태 변경 - 성공")
    void changeUserStatus_Success() {
        // given
        String userIdStr = userId.getValue().toString();
        String newStatus = "INACTIVE";

        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        UserDto inactiveUserDto = new UserDto(userIdStr,userDto.getEmail(),userDto.getUsername(),"INACTIVE",userDto.getRoles(),LocalDateTime.now(),LocalDateTime.now());

        given(userDtoMapper.toDto(user)).willReturn(inactiveUserDto);

        // when
        UserDto result = userService.changeUserStatus(userIdStr, newStatus);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("INACTIVE");

        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserUpdatedEvent.class));
    }

    @Test
    @DisplayName("사용자에게 역할 추가 - 성공 (기존 역할)")
    void addRoleToUser_ExistingRole() {
        // given
        String userIdStr = userId.getValue().toString();
        String roleName = "ADMIN";

        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        given(roleRepository.findByName(any())).willReturn(Optional.of(adminRole));
        given(userRepository.save(user)).willReturn(user);

        UserDto adminUserDto = new UserDto(userIdStr, userDto.getEmail(), userDto.getUsername(), userDto.getStatus(), new HashSet<>(Arrays.asList("USER", "ADMIN")), LocalDateTime.now(), LocalDateTime.now());

        given(userDtoMapper.toDto(user)).willReturn(adminUserDto);

        // when
        UserDto result = userService.addRoleToUser(userIdStr, roleName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).contains("ADMIN");

        verify(userRepository).findById(any(UserId.class));
        verify(roleRepository).findByName(any());
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserUpdatedEvent.class));
    }

    @Test
    @DisplayName("사용자에게서 역할 제거 - 성공")
    void removeRoleFromUser_Success() {
        // given
        String userIdStr = userId.getValue().toString();
        String roleName = "USER";

        // Add both roles to user
        try {
            java.lang.reflect.Field rolesField = User.class.getDeclaredField("roles");
            rolesField.setAccessible(true);
            Set<Role> roles = new HashSet<>(Arrays.asList(userRole, adminRole));
            rolesField.set(user, roles);
        } catch (Exception e) {
            throw new RuntimeException("테스트 셋업 실패", e);
        }

        given(userRepository.findById(any(UserId.class))).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        UserDto adminOnlyUserDto = new UserDto(userIdStr, userDto.getEmail(), userDto.getUsername(), userDto.getStatus(), new HashSet<>(Collections.singletonList("ADMIN")), LocalDateTime.now(), LocalDateTime.now());

        given(userDtoMapper.toDto(user)).willReturn(adminOnlyUserDto);

        // when
        UserDto result = userService.removeRoleFromUser(userIdStr, roleName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).doesNotContain("USER");
        assertThat(result.getRoles()).contains("ADMIN");

        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(UserUpdatedEvent.class));
    }
}
