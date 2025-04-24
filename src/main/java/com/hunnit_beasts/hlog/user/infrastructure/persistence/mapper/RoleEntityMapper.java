package com.hunnit_beasts.hlog.user.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleId;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.RoleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleEntityMapper {
    public RoleJpaEntity toJpaEntity(Role role) {
        return RoleJpaEntity.builder()
                .id(role.getId().getValue())
                .name(role.getName())
                .build();
    }

    public Role toDomainEntity(RoleJpaEntity jpaEntity) {
        return Role.of(
                RoleId.of(jpaEntity.getId()),
                jpaEntity.getName()
        );
    }
}
