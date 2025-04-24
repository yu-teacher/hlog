package com.hunnit_beasts.hlog.user.security;

import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.*;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserDetails;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserDetailsService;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private User adminUser;
    private UUID userId;
    private CustomUserInfoDTO userInfoDTO;
    private CustomUserInfoDTO adminUserInfoDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // 일반 사용자 준비
        user = User.create(
                Email.of("user@example.com"),
                Username.of("testuser"),
                Password.ofEncrypted("encoded_password")
        );

        try {
            // UserId 설정
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, UserId.of(userId));

            // 유저 상태 설정
            java.lang.reflect.Field statusField = User.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(user, UserStatus.ACTIVE);

            // 역할 설정
            java.lang.reflect.Field rolesField = User.class.getDeclaredField("roles");
            rolesField.setAccessible(true);
            Set<Role> roles = new HashSet<>();
            Role userRole = Role.user();
            // 역할 이름 설정
            java.lang.reflect.Field roleNameField = Role.class.getDeclaredField("name");
            roleNameField.setAccessible(true);
            roleNameField.set(userRole, RoleName.USER);
            roles.add(userRole);
            rolesField.set(user, roles);
        } catch (Exception e) {
            throw new RuntimeException("테스트 셋업 실패", e);
        }

        // 관리자 사용자 준비
        adminUser = User.create(
                Email.of("admin@example.com"),
                Username.of("admin"),
                Password.ofEncrypted("encoded_password")
        );

        try {
            // UserId 설정
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(adminUser, UserId.of(UUID.randomUUID()));

            // 유저 상태 설정
            java.lang.reflect.Field statusField = User.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(adminUser, UserStatus.ACTIVE);

            // 역할 설정
            java.lang.reflect.Field rolesField = User.class.getDeclaredField("roles");
            rolesField.setAccessible(true);
            Set<Role> roles = new HashSet<>();
            Role adminRole = Role.admin();
            // 역할 이름 설정
            java.lang.reflect.Field roleNameField = Role.class.getDeclaredField("name");
            roleNameField.setAccessible(true);
            roleNameField.set(adminRole, RoleName.ADMIN);
            roles.add(adminRole);
            rolesField.set(adminUser, roles);
        } catch (Exception e) {
            throw new RuntimeException("테스트 셋업 실패", e);
        }

        // 일반 사용자 DTO
        userInfoDTO = CustomUserInfoDTO.builder()
                .username("user_name")
                .userId(userId.toString())
                .email("user@example.com")
                .password("encoded_password")
                .role(CustomUserInfoDTO.Role.USER)
                .permissions(CustomUserInfoDTO.getDefaultPermissions(CustomUserInfoDTO.Role.USER))
                .enabled(true)
                .build();

        // 관리자 사용자 DTO
        adminUserInfoDTO = CustomUserInfoDTO.builder()
                .username("admin_name")
                .userId(adminUser.getId().getValue().toString())
                .email("admin@example.com")
                .password("encoded_password")
                .role(CustomUserInfoDTO.Role.ADMIN)
                .permissions(CustomUserInfoDTO.getDefaultPermissions(CustomUserInfoDTO.Role.ADMIN))
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("사용자명으로 UserDetails 로드 - 일반 사용자명")
    void loadUserByUsername_WithRegularUsername() {
        // given
        String username = "testuser";

        given(userRepository.findByUsername(any(Username.class)))
                .willReturn(Optional.of(user));
        given(modelMapper.map(eq(user), eq(CustomUserInfoDTO.class)))
                .willReturn(userInfoDTO);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        assertThat(customUserDetails.getUsername()).isEqualTo("user_name");
        assertThat(customUserDetails.getPassword()).isEqualTo("encoded_password");

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorityNames).containsExactly("ROLE_USER");

        verify(userRepository).findByUsername(any(Username.class));
    }

    @Test
    @DisplayName("사용자명으로 UserDetails 로드 - 이메일")
    void loadUserByUsername_WithEmail() {
        // given
        String email = "user@example.com";

        given(userRepository.findByEmail(any(Email.class)))
                .willReturn(Optional.of(user));
        given(modelMapper.map(eq(user), eq(CustomUserInfoDTO.class)))
                .willReturn(userInfoDTO);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("사용자명으로 UserDetails 로드 - UUID")
    void loadUserByUsername_WithUUID() {
        // given
        String uuidStr = userId.toString();

        given(userRepository.findById(any(UserId.class)))
                .willReturn(Optional.of(user));
        given(modelMapper.map(eq(user), eq(CustomUserInfoDTO.class)))
                .willReturn(userInfoDTO);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(uuidStr);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);

        verify(userRepository).findById(any(UserId.class));
    }

    @Test
    @DisplayName("사용자명으로 UserDetails 로드 - 사용자 없음")
    void loadUserByUsername_UserNotFound() {
        // given
        String username = "nonexistent";

        given(userRepository.findByUsername(any(Username.class)))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    @DisplayName("ID로 CustomUserDetails 로드")
    void loadUserById() {
        // given
        UserId userIdObj = UserId.of(userId);

        given(userRepository.findById(eq(userIdObj)))
                .willReturn(Optional.of(user));
        given(modelMapper.map(eq(user), eq(CustomUserInfoDTO.class)))
                .willReturn(userInfoDTO);

        // when
        CustomUserDetails customUserDetails = customUserDetailsService.loadUserById(userIdObj);

        // then
        assertThat(customUserDetails).isNotNull();
        assertThat(customUserDetails.getUsername()).isEqualTo("user_name");
        assertThat(customUserDetails.getEmail()).isEqualTo("user@example.com");
        assertThat(customUserDetails.getRole()).isEqualTo("USER");

        verify(userRepository).findById(eq(userIdObj));
    }

    @Test
    @DisplayName("ID로 CustomUserDetails 로드 - 사용자 없음")
    void loadUserById_UserNotFound() {
        // given
        UserId userIdObj = UserId.of(UUID.randomUUID());

        given(userRepository.findById(eq(userIdObj)))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserById(userIdObj);
        });
    }

    @Test
    @DisplayName("이메일로 UserDetails 로드")
    void loadUserByEmail() {
        // given
        String email = "user@example.com";

        given(userRepository.findByEmail(any(Email.class)))
                .willReturn(Optional.of(user));
        given(modelMapper.map(eq(user), eq(CustomUserInfoDTO.class)))
                .willReturn(userInfoDTO);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByEmail(email);

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        assertThat(customUserDetails.getUsername()).isEqualTo("user_name");
        assertThat(customUserDetails.getEmail()).isEqualTo("user@example.com");

        verify(userRepository).findByEmail(any(Email.class));
    }

    @Test
    @DisplayName("관리자 사용자 로드")
    void loadAdmin() {
        // given
        given(userRepository.findByUsername(any(Username.class)))
                .willReturn(Optional.of(adminUser));
        given(modelMapper.map(eq(adminUser), eq(CustomUserInfoDTO.class)))
                .willReturn(adminUserInfoDTO);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

        assertThat(customUserDetails.getRole()).isEqualTo("ADMIN");

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorityNames).containsExactly("ROLE_ADMIN");

        verify(userRepository).findByUsername(any(Username.class));
    }
}
