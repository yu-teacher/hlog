package com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesPostId;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.SeriesPostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SeriesPostEntityMapper {

    public SeriesPostJpaEntity toJpaEntity(SeriesPost seriesPost) {
        SeriesPostJpaEntity entity = new SeriesPostJpaEntity();
        entity.setId(seriesPost.getId().getValue());
        entity.setSeriesId(seriesPost.getSeriesId().getValue());
        entity.setPostId(seriesPost.getPostId().getValue());
        entity.setOrder(seriesPost.getOrder());
        entity.setCreatedAt(seriesPost.getCreatedAt());
        entity.setUpdatedAt(seriesPost.getUpdatedAt());
        return entity;
    }

    public SeriesPost toDomainEntity(SeriesPostJpaEntity entity) {
        SeriesPostId id = SeriesPostId.of(entity.getId());
        SeriesId seriesId = SeriesId.of(entity.getSeriesId());
        PostId postId = PostId.of(entity.getPostId());

        return SeriesPost.reconstitute(
                id,
                seriesId,
                postId,
                entity.getOrder(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}