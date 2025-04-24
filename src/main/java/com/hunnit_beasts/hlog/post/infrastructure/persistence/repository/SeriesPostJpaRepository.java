package com.hunnit_beasts.hlog.post.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesPostJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriesPostJpaRepository extends JpaRepository<SeriesPostJpaEntity, UUID> {
    List<SeriesPostJpaEntity> findBySeriesId(UUID seriesId);
    List<SeriesPostJpaEntity> findByPostId(UUID postId);
    Optional<SeriesPostJpaEntity> findBySeriesIdAndPostId(UUID seriesId, UUID postId);
    void deleteBySeriesId(UUID seriesId);
    void deleteByPostId(UUID postId);

    @Query("SELECT COALESCE(MAX(sp.order), 0) FROM SeriesPostJpaEntity sp WHERE sp.seriesId = :seriesId")
    int getMaxOrderInSeries(@Param("seriesId") UUID seriesId);

    boolean existsBySeriesIdAndPostId(UUID seriesId, UUID postId);
}