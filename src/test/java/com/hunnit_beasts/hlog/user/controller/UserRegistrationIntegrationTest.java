package com.hunnit_beasts.hlog.user.controller;

import com.hunnit_beasts.hlog.user.application.dto.CreateUserDto;
import com.hunnit_beasts.hlog.user.application.dto.UserDto;
import com.hunnit_beasts.hlog.user.application.mapper.UserDtoMapper;
import com.hunnit_beasts.hlog.user.application.service.UserServiceImpl;
import com.hunnit_beasts.hlog.user.domain.event.UserCreatedEvent;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.Password;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.domain.service.PasswordEncryptionService;
import com.hunnit_beasts.hlog.user.infrastructure.messaging.UserEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDtoMapper userDtoMapper;

    @Mock
    private PasswordEncryptionService passwordEncryptionService;

    @Mock
    private UserEventPublisher eventPublisher;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDto createUserDto;
    private User userEntity;
    private UserDto expectedUserDto;
    private String encryptedPassword;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        createUserDto = new CreateUserDto("test@example.com", "testuser", "password123");

        // 암호화된 비밀번호
        encryptedPassword = "encrypted_password";

        // 도메인 모델 User 생성
        userEntity = User.create(
                Email.of("test@example.com"),
                Username.of("testuser"),
                Password.of("password123")
        );

        // User 엔티티에 ID와 기타 필드 설정
        // 리플렉션이나 setter를 사용하여 테스트용 값 설정
        // (실제 구현에 따라 달라질 수 있음)

        // 예상되는 UserDto 결과
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        String userId = UUID.randomUUID().toString();
        expectedUserDto = new UserDto(
                userId,
                "test@example.com",
                "testuser",
                "ACTIVE",
                roles,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("사용자 등록 성공")
    void registerUser_Success() {
        // given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userDtoMapper.toEntity(createUserDto)).thenReturn(userEntity);

        // 암호화된 비밀번호 객체 생성
        Password encryptedPasswordObj = Password.ofEncrypted(encryptedPassword);

        // 암호화 서비스 모킹 - Password 객체를 받아서 암호화된 Password 객체 반환
        when(passwordEncryptionService.encrypt(any(Password.class))).thenReturn(encryptedPasswordObj);

        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userDtoMapper.toDto(userEntity)).thenReturn(expectedUserDto);

        // when
        UserDto result = userService.registerUser(createUserDto);

        System.out.println(result.toString());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getRoles()).contains("USER");

        // 비밀번호 암호화 호출 검증
        verify(passwordEncryptionService).encrypt(any(Password.class));

        // 저장 호출 검증
        verify(userRepository).save(any(User.class));

        // 이벤트 발행 검증
        verify(eventPublisher).publish(any(UserCreatedEvent.class));
    }

    @Test
    @DisplayName("이메일 중복으로 사용자 등록 실패")
    void registerUser_DuplicateEmail() {
        // given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(createUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    @DisplayName("사용자명 중복으로 사용자 등록 실패")
    void registerUser_DuplicateUsername() {
        // given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(createUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already in use");
    }
}
