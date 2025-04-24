package com.hunnit_beasts.hlog.user.domain.model.vo;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

public class Password implements Serializable {
    @Getter
    private final String value;
    private final boolean isEncrypted;

    private Password(String value, boolean isEncrypted) {
        this.value = Objects.requireNonNull(value);
        this.isEncrypted = isEncrypted;
    }

    public static Password of(String value) {
        validatePassword(value);
        return new Password(value, false);
    }

    public static Password ofEncrypted(String encryptedValue) {
        return new Password(encryptedValue, true);
    }

    private static void validatePassword(String password) {
        if (password.length() < 8)
            throw new IllegalArgumentException("패스워드는 8글자를 넘어야 합니다.");
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

}
