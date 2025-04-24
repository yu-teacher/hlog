package com.hunnit_beasts.hlog.profile.infrastructure.repository;

import com.hunnit_beasts.hlog.profile.domain.model.entity.Profile;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileStatus;
import com.hunnit_beasts.hlog.profile.domain.repository.ProfileRepository;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProfileJpaEntity;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.mapper.ProfileEntityMapper;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.repository.ProfileJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProfileRepositoryImpl implements ProfileRepository {

    private final ProfileJpaRepository profileJpaRepository;
    private final ProfileEntityMapper profileEntityMapper;

    public JpaProfileRepositoryImpl(ProfileJpaRepository profileJpaRepository,
                                    ProfileEntityMapper profileEntityMapper) {
        this.profileJpaRepository = profileJpaRepository;
        this.profileEntityMapper = profileEntityMapper;
    }

    @Override
    @Transactional
    public Profile save(Profile profile) {
        ProfileJpaEntity entity = profileEntityMapper.toJpaEntity(profile);
        ProfileJpaEntity savedEntity = profileJpaRepository.save(entity);
        return profileEntityMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(ProfileId id) {
        return profileJpaRepository.findByIdWithDetailsAndStatusNot(
                id.getValue(),
                ProfileStatus.DELETED
        ).map(profileEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByUserId(UUID userId) {
        return profileJpaRepository.findByUserId(userId)
                .filter(entity -> entity.getStatus() != ProfileStatus.DELETED)
                .map(profileEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return profileJpaRepository.existsByUserId(userId);
    }

    @Override
    @Transactional
    public void delete(Profile profile) {
        // Using soft delete by updating status
        profile.delete();
        save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByCustomUrl(String customUrl) {
        return profileJpaRepository.findByCustomUrlAndStatusNot(
                customUrl,
                ProfileStatus.DELETED
        ).map(profileEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCustomUrl(String customUrl) {
        return profileJpaRepository.existsByCustomUrl(customUrl);
    }
}