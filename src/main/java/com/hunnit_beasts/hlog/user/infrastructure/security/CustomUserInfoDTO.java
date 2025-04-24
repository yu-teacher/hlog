package com.hunnit_beasts.hlog.user.infrastructure.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Log4j2
public class CustomUserInfoDTO {
    private String userId;  // UUID 형태의 문자열
    private String email;
    private String password;
    private String username;
    private Role role;
    private Set<String> permissions;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    @Builder.Default
    private boolean enabled = true;

    // 상태에 따른 활성화 여부 반환
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE && enabled;
    }

    // 역할 열거형
    public enum Role {
        USER, ADMIN, GUEST
    }

    // 기본 권한 세트 생성
    public static Set<String> getDefaultPermissions(Role role) {
        return switch (role) {
            case ADMIN -> Set.of("READ", "WRITE", "DELETE", "ADMIN");
            case USER -> Set.of("READ", "WRITE");
            case GUEST -> Set.of("READ");
        };
    }
}
