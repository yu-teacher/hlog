package com.hunnit_beasts.hlog.user.domain.event;

import com.hunnit_beasts.hlog.user.domain.model.entity.User;
import com.hunnit_beasts.hlog.user.domain.model.vo.Email;
import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;
import com.hunnit_beasts.hlog.user.domain.model.vo.Username;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCreatedEvent implements UserEvent {
    private final UserId userId;
    private final Email email;
    private final Username username;
    private final LocalDateTime timestamp;

    public UserCreatedEvent(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.timestamp = LocalDateTime.now();
    }
}
