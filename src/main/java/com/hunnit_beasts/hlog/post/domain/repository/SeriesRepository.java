package com.hunnit_beasts.hlog.post.domain.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeriesRepository {
    Series save(Series series);
    Optional<Series> findById(SeriesId id);
    List<Series> findByAuthorId(UUID authorId);
    List<Series> findByStatus(SeriesStatus status);
    List<Series> findByStatusAndAuthorId(SeriesStatus status, UUID authorId);
    List<Series> findAll();
    void delete(Series series);
    boolean existsById(SeriesId id);
}