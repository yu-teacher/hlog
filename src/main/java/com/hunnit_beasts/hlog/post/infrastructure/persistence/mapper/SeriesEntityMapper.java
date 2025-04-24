package com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.vo.Description;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesName;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SeriesEntityMapper {

    public SeriesJpaEntity toJpaEntity(Series series) {
        SeriesJpaEntity entity = new SeriesJpaEntity();
        entity.setId(series.getId().getValue());
        entity.setName(series.getName().getValue());
        entity.setDescription(series.getDescription() != null ? series.getDescription().getValue() : null);
        entity.setAuthorId(series.getAuthorId());
        entity.setStatus(series.getStatus());
        entity.setCreatedAt(series.getCreatedAt());
        entity.setUpdatedAt(series.getUpdatedAt());
        return entity;
    }

    public Series toDomainEntity(SeriesJpaEntity entity) {
        SeriesId id = SeriesId.of(entity.getId());
        SeriesName name = SeriesName.of(entity.getName());
        Description description = entity.getDescription() != null
                ? Description.of(entity.getDescription())
                : Description.empty();
        SeriesStatus status = entity.getStatus();

        return Series.reconstitute(
                id,
                name,
                description,
                entity.getAuthorId(),
                status,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}