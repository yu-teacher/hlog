package com.hunnit_beasts.hlog.user.infrastructure.repository;

import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import com.hunnit_beasts.hlog.user.domain.repository.UserRepository;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.entity.UserJpaEntity;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.hunnit_beasts.hlog.user.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaUserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    public JpaUserRepositoryImpl(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = userEntityMapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
        return userEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.getValue())
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue())
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userJpaRepository.findByUsername(username.getValue())
                .map(userEntityMapper::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userJpaRepository.existsByUsername(username.getValue());
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        userJpaRepository.deleteById(user.getId().getValue());
    }
}
