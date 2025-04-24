package com.hunnit_beasts.hlog.post.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.TagJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagJpaRepository extends JpaRepository<TagJpaEntity, String> {
    @Query("SELECT t FROM TagJpaEntity t ORDER BY t.usageCount DESC LIMIT :limit")
    List<TagJpaEntity> findTopByUsageCount(@Param("limit") int limit);
}