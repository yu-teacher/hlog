package com.hunnit_beasts.hlog.user.infrastructure.messaging;

import com.hunnit_beasts.hlog.user.domain.event.UserCreatedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserDeletedEvent;
import com.hunnit_beasts.hlog.user.domain.event.UserUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventListener {

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Post created: {}", event.getUserId());
        // 추가적인 이벤트 처리 로직 (e.g., 알림, 통계 등)
    }

    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Post updated: {}", event.getUserId());
        // 추가적인 이벤트 처리 로직
    }

    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        log.info("Post published: {}", event.getUserId());
        // 발행 시 특별 처리 (e.g., 피드 업데이트, 검색 인덱스 갱신 등)
    }

}