package com.hunnit_beasts.hlog.user.domain.event;

import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDeletedEvent implements UserEvent {
    private final UserId userId;
    private final LocalDateTime timestamp;

    public UserDeletedEvent(UserId userId) {
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
}