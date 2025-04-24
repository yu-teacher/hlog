package com.hunnit_beasts.hlog.post.domain.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesPostId;

import java.util.List;
import java.util.Optional;

public interface SeriesPostRepository {
    SeriesPost save(SeriesPost seriesPost);
    Optional<SeriesPost> findById(SeriesPostId id);
    List<SeriesPost> findBySeriesId(SeriesId seriesId);
    List<SeriesPost> findByPostId(PostId postId);
    Optional<SeriesPost> findBySeriesIdAndPostId(SeriesId seriesId, PostId postId);
    void delete(SeriesPost seriesPost);
    void deleteBySeriesId(SeriesId seriesId);
    void deleteByPostId(PostId postId);
    int getMaxOrderInSeries(SeriesId seriesId);
    boolean existsBySeriesIdAndPostId(SeriesId seriesId, PostId postId);
}