package com.hunnit_beasts.hlog.post.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.*;
import com.hunnit_beasts.hlog.post.infrastructure.persistence.entity.PostJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PostEntityMapper {

    public PostJpaEntity toJpaEntity(Post post) {
        PostJpaEntity entity = new PostJpaEntity();
        entity.setId(post.getId().getValue());
        entity.setTitle(post.getTitle().getValue());
        entity.setContent(post.getContent().getValue());
        entity.setContentFormat(post.getContent().getFormat());
        entity.setStatus(post.getStatus());
        entity.setAuthorId(post.getAuthorId());
        entity.setTagNames(post.getTagNames());
        entity.setCreatedAt(post.getCreatedAt());
        entity.setUpdatedAt(post.getUpdatedAt());
        entity.setPublishedAt(post.getPublishedAt());
        return entity;
    }

    public Post toDomainEntity(PostJpaEntity entity) {
        PostId id = PostId.of(entity.getId());
        Title title = Title.of(entity.getTitle());
        Content content = entity.getContentFormat() == ContentFormat.MARKDOWN
                ? Content.ofMarkdown(entity.getContent())
                : Content.ofHtml(entity.getContent());
        PostStatus status = entity.getStatus();

        return Post.reconstitute(
                id,
                title,
                content,
                status,
                entity.getAuthorId(),
                entity.getTagNames(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPublishedAt()
        );
    }
}