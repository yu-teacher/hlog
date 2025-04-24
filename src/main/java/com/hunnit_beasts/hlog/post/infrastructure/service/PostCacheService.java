package com.hunnit_beasts.hlog.post.infrastructure.service;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 포스트 캐싱을 위한 간단한 인메모리 캐시 구현
 * 실제 환경에서는 Redis 등의 외부 캐시 시스템과 연동할 수 있음
 */
@Service
@Slf4j
public class PostCacheService {

    private final Map<String, Post> postCache = new ConcurrentHashMap<>();

    public void cachePost(Post post) {
        postCache.put(post.getId().getValue().toString(), post);
        log.debug("Post cached: {}", post.getId());
    }

    public Optional<Post> getFromCache(PostId postId) {
        return Optional.ofNullable(postCache.get(postId.getValue().toString()));
    }

    public void evictFromCache(PostId postId) {
        postCache.remove(postId.getValue().toString());
        log.debug("Post evicted from cache: {}", postId);
    }

    public void clearCache() {
        postCache.clear();
        log.debug("Post cache cleared");
    }
}