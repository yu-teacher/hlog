package com.hunnit_beasts.hlog.profile.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileStatus;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, UUID> {

    Optional<ProfileJpaEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Optional<ProfileJpaEntity> findByCustomUrlAndStatusNot(String customUrl, ProfileStatus status);

    boolean existsByCustomUrl(String customUrl);

    @Query("SELECT p FROM ProfileJpaEntity p " +
            "LEFT JOIN FETCH p.projects " +
            "LEFT JOIN FETCH p.externalLinks " +
            "WHERE p.id = :id AND p.status != :excludeStatus")
    Optional<ProfileJpaEntity> findByIdWithDetailsAndStatusNot(
            @Param("id") UUID id,
            @Param("excludeStatus") ProfileStatus excludeStatus);
}