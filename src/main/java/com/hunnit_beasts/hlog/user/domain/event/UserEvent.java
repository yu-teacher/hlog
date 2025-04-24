package com.hunnit_beasts.hlog.user.domain.event;

import com.hunnit_beasts.hlog.user.domain.model.vo.UserId;

import java.time.LocalDateTime;

public interface UserEvent {
    UserId getUserId();
    LocalDateTime getTimestamp();
}
