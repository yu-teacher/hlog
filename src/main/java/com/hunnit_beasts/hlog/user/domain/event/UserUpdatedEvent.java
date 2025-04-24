package com.hunnit_beasts.hlog.user.domain.event;

import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserUpdatedEvent implements UserEvent {
    private final UserId userId;
    private final LocalDateTime timestamp;

    public UserUpdatedEvent(User user) {
        this.userId = user.getId();
        this.timestamp = LocalDateTime.now();
    }
}

