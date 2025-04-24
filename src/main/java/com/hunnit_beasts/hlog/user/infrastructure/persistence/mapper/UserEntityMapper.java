package com.hunnit_beasts.hlog.user.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.*;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.RoleJpaEntity;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {
    public UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity jpaEntity = UserJpaEntity.builder()
                .id(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .password(user.getPassword().getValue())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // 역할 매핑
        Set<RoleJpaEntity> roleEntities = user.getRoles().stream()
                .map(role -> RoleJpaEntity.builder()
                        .id(role.getId().getValue())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        jpaEntity.setRoles(roleEntities);

        return jpaEntity;
    }

    public User toDomainEntity(UserJpaEntity jpaEntity) {
        UserId id = UserId.of(jpaEntity.getId());
        Email email = Email.of(jpaEntity.getEmail());
        Username username = Username.of(jpaEntity.getUsername());
        Password password = Password.ofEncrypted(jpaEntity.getPassword());
        UserStatus status = jpaEntity.getStatus();

        Set<Role> roles = jpaEntity.getRoles().stream()
                .map(roleEntity -> Role.of(
                        RoleId.of(roleEntity.getId()),
                        roleEntity.getName()
                ))
                .collect(Collectors.toSet());

        return User.reconstitute(
                id, email, username, password, status,
                roles, jpaEntity.getCreatedAt(), jpaEntity.getUpdatedAt()
        );
    }
}
