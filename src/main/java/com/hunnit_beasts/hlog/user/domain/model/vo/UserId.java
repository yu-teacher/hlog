package com.hunnit_beasts.hlog.user.domain.model.vo;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class UserId implements Serializable {
    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

}
