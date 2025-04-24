package com.hunnit_beasts.hlog.user.domain.model.vo;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Username implements Serializable {
    private final String value;

    private Username(String value) {
        validateUsername(value);
        this.value = value;
    }

    public static Username of(String value) {
        return new Username(value);
    }

    private void validateUsername(String username) {
        if (username.length() < 3 || username.length() > 20)
            throw new IllegalArgumentException("3글자 초과 20글자 미만");
        if (!username.matches("^[a-zA-Z0-9_-]+$"))
            throw new IllegalArgumentException("영어 대소문자 및 숫자로만 이루어져야 합니다.");
    }

}
