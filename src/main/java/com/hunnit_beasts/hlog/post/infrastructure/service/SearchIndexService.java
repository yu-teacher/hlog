package com.hunnit_beasts.hlog.post.infrastructure.service;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 실제 검색 엔진과 통합하기 위한 준비 클래스
 * 현재는 로깅만 하지만 향후 Elasticsearch 등과 연동 가능
 */
@Service
@Slf4j
public class SearchIndexService {

    public void indexPost(Post post) {
        log.info("Indexing post: {}", post.getId());
        // 실제 검색 엔진에 색인하는 로직
    }

    public void updatePostIndex(Post post) {
        log.info("Updating post index: {}", post.getId());
        // 색인 업데이트 로직
    }

    public void removePostFromIndex(Post post) {
        log.info("Removing post from index: {}", post.getId());
        // 색인에서 삭제 로직
    }
}