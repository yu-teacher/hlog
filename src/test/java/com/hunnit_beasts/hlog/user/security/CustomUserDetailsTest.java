package com.hunnit_beasts.hlog.user.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.UserStatus;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserDetails;
import com.hunnit_beasts.hlog.user.infrastructure.security.CustomUserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    private CustomUserInfoDTO userInfoDTO;
    private CustomUserInfoDTO adminInfoDTO;
    private CustomUserInfoDTO disabledUserInfoDTO;
    private UUID userUuid;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();

        // 일반 사용자 DTO
        userInfoDTO = CustomUserInfoDTO.builder()
                .username("user_name")
                .userId(userUuid.toString())
                .email("user@example.com")
                .password("encoded_password")
                .role(CustomUserInfoDTO.Role.USER)
                .permissions(Set.of("READ", "WRITE"))
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .build();

        // 관리자 DTO
        adminInfoDTO = CustomUserInfoDTO.builder()
                .username("admin_name")
                .userId("admin")
                .email("admin@example.com")
                .password("admin_password")
                .role(CustomUserInfoDTO.Role.ADMIN)
                .permissions(Set.of("READ", "WRITE", "DELETE", "ADMIN"))
                .status(UserStatus.ACTIVE)
                .enabled(true)
                .build();

        // 비활성화된 사용자 DTO
        disabledUserInfoDTO = CustomUserInfoDTO.builder()
                .username("disable_user_name")
                .userId("disabled")
                .email("disabled@example.com")
                .password("password")
                .role(CustomUserInfoDTO.Role.USER)
                .status(UserStatus.INACTIVE)
                .enabled(false)
                .build();
    }

    @Test
    @DisplayName("사용자 권한 테스트 - 일반 사용자")
    void getAuthorities_RegularUser() {
        // given
        CustomUserDetails userDetails = new CustomUserDetails(userInfoDTO);

        // when
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("사용자 권한 테스트 - 관리자")
    void getAuthorities_AdminUser() {
        // given
        CustomUserDetails adminDetails = new CustomUserDetails(adminInfoDTO);

        // when
        Collection<? extends GrantedAuthority> authorities = adminDetails.getAuthorities();

        // then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("사용자 계정 상태 테스트 - 활성화된 계정")
    void accountStatus_EnabledUser() {
        // given
        CustomUserDetails userDetails = new CustomUserDetails(userInfoDTO);

        // when & then
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("사용자 계정 상태 테스트 - 비활성화된 계정")
    void accountStatus_DisabledUser() {
        // given
        CustomUserDetails disabledDetails = new CustomUserDetails(disabledUserInfoDTO);

        // when & then
        assertThat(disabledDetails.isAccountNonExpired()).isTrue();
        assertThat(disabledDetails.isAccountNonLocked()).isTrue();
        assertThat(disabledDetails.isCredentialsNonExpired()).isTrue();
        assertThat(disabledDetails.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("사용자 정보 접근 테스트")
    void userInfoAccess() {
        // given
        CustomUserDetails userDetails = new CustomUserDetails(userInfoDTO);

        // when & then
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");
        assertThat(userDetails.getUsername()).isEqualTo("user_name");
        assertThat(userDetails.getEmail()).isEqualTo("user@example.com");
        assertThat(userDetails.getRole()).isEqualTo("USER");
        assertThat(userDetails.getUserUuid()).isEqualTo(userUuid);
    }
}
