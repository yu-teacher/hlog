package com.hunnit_beasts.hlog.user.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final CustomUserInfoDTO customUserInfoDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + customUserInfoDTO.getRole()));
    }

    @Override
    public String getPassword() {
        return customUserInfoDTO.getPassword();
    }

    @Override
    public String getUsername() {
        // userId 또는 username 사용 (요구사항에 따라)
        return customUserInfoDTO.getUsername();
    }

    public String getUserId() {
        return customUserInfoDTO.getUserId();
    }

    public UUID getUserUuid() {
        try {
            // UserId가 UUID 형태일 경우
            return UUID.fromString(customUserInfoDTO.getUserId());
        } catch (IllegalArgumentException e) {
            // UserId가 UUID 형태가 아닌 경우
            return null;
        }
    }

    public String getEmail() {
        return customUserInfoDTO.getEmail();
    }

    public String getRole() {
        return customUserInfoDTO.getRole().name();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 - 만료되지 않았으면 true
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부 - 잠기지 않았으면 true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부 - 만료되지 않았으면 true
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 활성화 여부 - 활성화되었으면 true
        return customUserInfoDTO.isEnabled();
    }
}