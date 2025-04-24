package com.hunnit_beasts.hlog.post.infrastructure.messaging;

import com.hunnit_beasts.hlog.post.domain.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostEventListener {

    @EventListener
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        log.info("Post created: {}", event.getPostId());
        // 추가적인 이벤트 처리 로직 (e.g., 알림, 통계 등)
    }

    @EventListener
    public void handlePostUpdatedEvent(PostUpdatedEvent event) {
        log.info("Post updated: {}", event.getPostId());
        // 추가적인 이벤트 처리 로직
    }

    @EventListener
    public void handlePostPublishedEvent(PostPublishedEvent event) {
        log.info("Post published: {}", event.getPostId());
        // 발행 시 특별 처리 (e.g., 피드 업데이트, 검색 인덱스 갱신 등)
    }

    @EventListener
    public void handlePostDeletedEvent(PostDeletedEvent event) {
        log.info("Post deleted: {}", event.getPostId());
        // 삭제 시 추가 처리 (e.g., 연관 데이터 정리 등)
    }

    @EventListener
    public void handleSeriesCreatedEvent(SeriesCreatedEvent event) {
        log.info("Series created: {}", event.getSeriesId());
    }

    @EventListener
    public void handleSeriesUpdatedEvent(SeriesUpdatedEvent event) {
        log.info("Series updated: {}", event.getSeriesId());
    }
}