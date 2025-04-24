package com.hunnit_beasts.hlog.user.domain.model.vo;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Email implements Serializable {
    private final String value;

    private Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    private void validateEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"))
            throw new IllegalArgumentException("Invalid email format");
    }

}
