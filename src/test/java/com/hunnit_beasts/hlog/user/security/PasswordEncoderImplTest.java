package com.hunnit_beasts.hlog.user.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.Password;
import com.hunnit_beasts.hlog.user.infrastructure.security.PasswordEncoderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PasswordEncoderImplTest {
    @Test
    @DisplayName("비밀번호 암호화 - 일반 비밀번호")
    void encrypt_PlainPassword() {
        // given
        BCryptPasswordEncoder mockEncoder = mock(BCryptPasswordEncoder.class);
        when(mockEncoder.encode(anyString())).thenReturn("encoded_password");

        PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl(mockEncoder);

        Password rawPassword = Password.of("password123");

        // when
        Password encryptedPassword = passwordEncoder.encrypt(rawPassword);

        // then
        assertThat(encryptedPassword.getValue()).isEqualTo("encoded_password");
        assertThat(encryptedPassword.isEncrypted()).isTrue();
        verify(mockEncoder).encode("password123");
    }

    @Test
    @DisplayName("비밀번호 암호화 - 이미 암호화된 비밀번호")
    void encrypt_AlreadyEncryptedPassword() {
        // given
        BCryptPasswordEncoder mockEncoder = mock(BCryptPasswordEncoder.class);
        PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl(mockEncoder);

        Password encryptedPassword = Password.ofEncrypted("already_encoded_password");

        // when
        Password result = passwordEncoder.encrypt(encryptedPassword);

        // then
        assertThat(result).isSameAs(encryptedPassword);
        assertThat(result.getValue()).isEqualTo("already_encoded_password");
        assertThat(result.isEncrypted()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 일치하는 경우")
    void matches_PasswordsMatch() {
        // given
        BCryptPasswordEncoder mockEncoder = mock(BCryptPasswordEncoder.class);
        when(mockEncoder.matches(anyString(), anyString())).thenReturn(true);

        PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl(mockEncoder);

        Password rawPassword = Password.of("password123");
        Password encryptedPassword = Password.ofEncrypted("encoded_password");

        // when
        boolean result = passwordEncoder.matches(rawPassword, encryptedPassword);

        // then
        assertThat(result).isTrue();
        verify(mockEncoder).matches("password123", "encoded_password");
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 일치하지 않는 경우")
    void matches_PasswordsDontMatch() {
        // given
        BCryptPasswordEncoder mockEncoder = mock(BCryptPasswordEncoder.class);
        when(mockEncoder.matches(anyString(), anyString())).thenReturn(false);

        PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl(mockEncoder);

        Password rawPassword = Password.of("wrong_password");
        Password encryptedPassword = Password.ofEncrypted("encoded_password");

        // when
        boolean result = passwordEncoder.matches(rawPassword, encryptedPassword);

        // then
        assertThat(result).isFalse();
        verify(mockEncoder).matches("wrong_password", "encoded_password");
    }

    @Test
    @DisplayName("실제 BCryptPasswordEncoder로 암호화 및 검증")
    void realPasswordEncoderTest() {
        // given
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
        PasswordEncoderImpl passwordEncoder = new PasswordEncoderImpl(realEncoder);
        Password rawPassword = Password.of("password123");

        // when
        Password encryptedPassword = passwordEncoder.encrypt(rawPassword);
        boolean matches = passwordEncoder.matches(rawPassword, encryptedPassword);
        boolean wrongMatches = passwordEncoder.matches(Password.of("wrong_password"), encryptedPassword);

        // then
        assertThat(encryptedPassword.getValue()).isNotEqualTo("password123");
        assertThat(encryptedPassword.isEncrypted()).isTrue();
        assertThat(matches).isTrue();
        assertThat(wrongMatches).isFalse();
    }
}
