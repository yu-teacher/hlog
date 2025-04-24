package com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.TagJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TagEntityMapper {

    public TagJpaEntity toJpaEntity(Tag tag) {
        TagJpaEntity entity = new TagJpaEntity();
        entity.setName(tag.getName());
        entity.setUsageCount(tag.getUsageCount());
        entity.setCreatedAt(tag.getCreatedAt());
        return entity;
    }

    public Tag toDomainEntity(TagJpaEntity entity) {
        return Tag.reconstitute(
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUsageCount()
        );
    }
}