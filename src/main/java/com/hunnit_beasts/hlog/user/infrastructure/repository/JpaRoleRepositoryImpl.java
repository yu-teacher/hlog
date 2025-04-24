package com.hunnit_beasts.hlog.user.infrastructure.repository;

import com.hunnit_beasts.hlog.user.domain.model.entity.Role;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleId;
import com.hunnit_beasts.hlog.user.domain.model.vo.RoleName;
import com.hunnit_beasts.hlog.user.domain.repository.RoleRepository;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.RoleJpaEntity;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.mapper.RoleEntityMapper;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.repository.RoleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaRoleRepositoryImpl implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;
    private final RoleEntityMapper roleEntityMapper;

    public JpaRoleRepositoryImpl(RoleJpaRepository roleJpaRepository, RoleEntityMapper roleEntityMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleEntityMapper = roleEntityMapper;
    }

    @Override
    public Role save(Role role) {
        RoleJpaEntity jpaEntity = roleEntityMapper.toJpaEntity(role);
        RoleJpaEntity savedEntity = roleJpaRepository.save(jpaEntity);
        return roleEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleJpaRepository.findById(id.getValue())
                .map(roleEntityMapper::toDomainEntity);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleJpaRepository.findByName(name)
                .map(roleEntityMapper::toDomainEntity);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(roleEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }
}
