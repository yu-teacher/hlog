package com.hunnit_beasts.hlog.user.domain.service;

import com.hunnit_beasts.hlog.user.domain.model.vo.Password;

public interface PasswordEncryptionService {
    Password encrypt(Password password);
    boolean matches(Password rawPassword, Password encodedPassword);
}
