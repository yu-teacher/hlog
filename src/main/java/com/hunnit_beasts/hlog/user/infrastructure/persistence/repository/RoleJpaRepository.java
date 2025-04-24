package com.hunnit_beasts.hlog.user.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.user.domain.model.vo.RoleName;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    Optional<RoleJpaEntity> findByName(RoleName name);
}
