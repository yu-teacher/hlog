package com.hunnit_beasts.hlog.user.domain.repository;

import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleId;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(RoleId id);
    Optional<Role> findByName(RoleName name);
    List<Role> findAll();
}
