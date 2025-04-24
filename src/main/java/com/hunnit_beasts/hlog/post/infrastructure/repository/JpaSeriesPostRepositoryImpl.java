package com.hunnit_beasts.hlog.post.infrastructure.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesPostId;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesPostRepository;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesPostJpaEntity;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper.SeriesPostEntityMapper;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.repository.SeriesPostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaSeriesPostRepositoryImpl implements SeriesPostRepository {
    private final SeriesPostJpaRepository seriesPostJpaRepository;
    private final SeriesPostEntityMapper seriesPostEntityMapper;

    @Override
    public SeriesPost save(SeriesPost seriesPost) {
        SeriesPostJpaEntity entity = seriesPostEntityMapper.toJpaEntity(seriesPost);
        SeriesPostJpaEntity savedEntity = seriesPostJpaRepository.save(entity);
        return seriesPostEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<SeriesPost> findById(SeriesPostId id) {
        return seriesPostJpaRepository.findById(id.getValue())
                .map(seriesPostEntityMapper::toDomainEntity);
    }

    @Override
    public List<SeriesPost> findBySeriesId(SeriesId seriesId) {
        return seriesPostJpaRepository.findBySeriesId(seriesId.getValue()).stream()
                .map(seriesPostEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeriesPost> findByPostId(PostId postId) {
        return seriesPostJpaRepository.findByPostId(postId.getValue()).stream()
                .map(seriesPostEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SeriesPost> findBySeriesIdAndPostId(SeriesId seriesId, PostId postId) {
        return seriesPostJpaRepository.findBySeriesIdAndPostId(seriesId.getValue(), postId.getValue())
                .map(seriesPostEntityMapper::toDomainEntity);
    }

    @Override
    public void delete(SeriesPost seriesPost) {
        seriesPostJpaRepository.deleteById(seriesPost.getId().getValue());
    }

    @Override
    public void deleteBySeriesId(SeriesId seriesId) {
        seriesPostJpaRepository.deleteBySeriesId(seriesId.getValue());
    }

    @Override
    public void deleteByPostId(PostId postId) {
        seriesPostJpaRepository.deleteByPostId(postId.getValue());
    }

    @Override
    public int getMaxOrderInSeries(SeriesId seriesId) {
        return seriesPostJpaRepository.getMaxOrderInSeries(seriesId.getValue());
    }

    @Override
    public boolean existsBySeriesIdAndPostId(SeriesId seriesId, PostId postId) {
        return seriesPostJpaRepository.existsBySeriesIdAndPostId(seriesId.getValue(), postId.getValue());
    }
}