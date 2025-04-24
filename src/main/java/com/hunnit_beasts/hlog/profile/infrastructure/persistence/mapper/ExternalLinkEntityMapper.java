package com.hunnit_beasts.hlog.profile.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ExternalLink;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ExternalLinkJpaEntity;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalLinkEntityMapper {

    public ExternalLinkJpaEntity toJpaEntity(ExternalLink domain, ProfileJpaEntity profile) {
        if (domain == null) {
            return null;
        }

        return ExternalLinkJpaEntity.builder()
                .profile(profile)
                .platform(domain.getPlatform())
                .url(domain.getUrl())
                .build();
    }

    public ExternalLink toDomain(ExternalLinkJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return ExternalLink.create(
                entity.getPlatform(),
                entity.getUrl()
        );
    }
}