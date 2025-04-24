package com.hunnit_beasts.hlog.post.infrastructure.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesRepository;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesJpaEntity;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper.SeriesEntityMapper;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.repository.SeriesJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaSeriesRepositoryImpl implements SeriesRepository {
    private final SeriesJpaRepository seriesJpaRepository;
    private final SeriesEntityMapper seriesEntityMapper;

    @Override
    public Series save(Series series) {
        SeriesJpaEntity entity = seriesEntityMapper.toJpaEntity(series);
        SeriesJpaEntity savedEntity = seriesJpaRepository.save(entity);
        return seriesEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Series> findById(SeriesId id) {
        return seriesJpaRepository.findById(id.getValue())
                .map(seriesEntityMapper::toDomainEntity);
    }

    @Override
    public List<Series> findByAuthorId(UUID authorId) {
        return seriesJpaRepository.findByAuthorId(authorId).stream()
                .map(seriesEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Series> findByStatus(SeriesStatus status) {
        return seriesJpaRepository.findByStatus(status.toString()).stream()
                .map(seriesEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Series> findByStatusAndAuthorId(SeriesStatus status, UUID authorId) {
        return seriesJpaRepository.findByStatusAndAuthorId(status.toString(), authorId).stream()
                .map(seriesEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Series> findAll() {
        return seriesJpaRepository.findAll().stream()
                .map(seriesEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Series series) {
        seriesJpaRepository.deleteById(series.getId().getValue());
    }

    @Override
    public boolean existsById(SeriesId id) {
        return seriesJpaRepository.existsById(id.getValue());
    }
}