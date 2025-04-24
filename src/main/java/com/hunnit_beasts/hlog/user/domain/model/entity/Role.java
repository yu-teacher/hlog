package com.hunnit_beasts.hlog.user.domain.model.entity;

import com.hunnit_beasts.hlog.user.domain.model.vo.RoleId;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleName;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Role {
    private final RoleId id;
    private final RoleName name;

    private Role(RoleId id, RoleName name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public static Role of(RoleId id, RoleName name) {
        return new Role(id, name);
    }

    public static Role admin() {
        return new Role(RoleId.create(), RoleName.ADMIN);
    }

    public static Role user() {
        return new Role(RoleId.create(), RoleName.USER);
    }

}