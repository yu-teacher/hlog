package com.hunnit_beasts.hlog.user.infrastructure.security;

import com.hunnit_beasts.hlog.user.domain.model.vo.Password;
import com.hunnit_beasts.hlog.user.domain.service.PasswordEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class PasswordEncoderImpl implements PasswordEncryptionService {
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Password encrypt(Password password) {
        if (password.isEncrypted()) {
            return password;
        }
        String encoded = passwordEncoder.encode(password.getValue());
        return Password.ofEncrypted(encoded);
    }

    @Override
    public boolean matches(Password rawPassword, Password encodedPassword) {
        return passwordEncoder.matches(
                rawPassword.getValue(),
                encodedPassword.getValue()
        );
    }
}
