package com.hunnit_beasts.hlog.user.domain.model.vo;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class RoleId implements Serializable {
    private final UUID value;

    private RoleId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static RoleId of(UUID value) {
        return new RoleId(value);
    }

    public static RoleId create() {
        return new RoleId(UUID.randomUUID());
    }

}
