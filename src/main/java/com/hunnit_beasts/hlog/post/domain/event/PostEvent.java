package com.hunnit_beasts.hlog.post.domain.event;

import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;

import java.time.LocalDateTime;

public interface PostEvent {
    PostId getPostId();
    LocalDateTime getTimestamp();
}
