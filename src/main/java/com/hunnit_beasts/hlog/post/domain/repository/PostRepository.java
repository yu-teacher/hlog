package com.hunnit_beasts.hlog.post.domain.repository;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(PostId id);
    List<Post> findByAuthorId(UUID authorId);
    List<Post> findByStatus(PostStatus status);
    List<Post> findByStatusAndAuthorId(PostStatus status, UUID authorId);
    List<Post> findByTagName(String tagName);
    List<Post> findAll();
    void delete(Post post);
    long countByAuthorId(UUID authorId);
    boolean existsById(PostId id);

    // 삭제되지 않은 포스트만 조회하는 메서드 추가
    List<Post> findActiveByAuthorId(UUID authorId);
    List<Post> findActiveByTagName(String tagName);
    List<Post> findActiveAll();
    List<Post> findPublishedPosts();
    List<Post> findPublishedByAuthorId(UUID authorId);
    Optional<Post> findActiveById(PostId id);
}