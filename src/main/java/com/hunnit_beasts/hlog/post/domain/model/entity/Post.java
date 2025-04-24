package com.hunnit_beasts.hlog.post.domain.model.entity;

import com.hunnit_beasts.hlog.post.domain.model.vo.Content;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.model.vo.Title;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Post {
    private final PostId id;
    private Title title;
    private Content content;
    private PostStatus status;
    private final UUID authorId;
    private final Set<String> tagNames;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    private Post(PostId id, Title title, Content content, PostStatus status,
                 UUID authorId, Set<String> tagNames, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.content = Objects.requireNonNull(content);
        this.status = Objects.requireNonNull(status);
        this.authorId = Objects.requireNonNull(authorId);
        this.tagNames = new HashSet<>(tagNames != null ? tagNames : new HashSet<>());
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
        this.publishedAt = null;
    }

    public static Post create(Title title, Content content, UUID authorId, Set<String> tagNames) {
        return new Post(
                PostId.create(),
                title,
                content,
                PostStatus.DRAFT,
                authorId,
                tagNames,
                LocalDateTime.now()
        );
    }

    public static Post reconstitute(
            PostId id, Title title, Content content, PostStatus status,
            UUID authorId, Set<String> tagNames,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt) {

        Post post = new Post(id, title, content, status, authorId, tagNames, createdAt);
        post.updatedAt = updatedAt;
        post.publishedAt = publishedAt;

        return post;
    }

    public void updateTitle(Title title) {
        this.title = Objects.requireNonNull(title);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(Content content) {
        this.content = Objects.requireNonNull(content);
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        if (this.status != PostStatus.PUBLISHED) {
            this.status = PostStatus.PUBLISHED;
            this.publishedAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        if (this.status == PostStatus.PUBLISHED) {
            this.status = PostStatus.DRAFT;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addTag(String tagName) {
        if (tagName != null && !tagName.trim().isEmpty()) {
            this.tagNames.add(tagName.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTag(String tagName) {
        if (tagName != null) {
            this.tagNames.remove(tagName.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void delete() {
        if (this.status != PostStatus.DELETED) {
            this.status = PostStatus.DELETED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isDeleted() {
        return this.status == PostStatus.DELETED;
    }

    public Set<String> getTagNames() {
        return Collections.unmodifiableSet(tagNames);
    }
}
