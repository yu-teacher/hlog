package com.hunnit_beasts.hlog.post.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeriesJpaRepository extends JpaRepository<SeriesJpaEntity, UUID> {
    List<SeriesJpaEntity> findByAuthorId(UUID authorId);
    List<SeriesJpaEntity> findByStatus(String status);
    List<SeriesJpaEntity> findByStatusAndAuthorId(String status, UUID authorId);
}