package com.hunnit_beasts.hlog.post.domain.event;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PostDeletedEvent implements PostEvent {
    private final PostId postId;
    private final UUID authorId;
    private final LocalDateTime timestamp;

    public PostDeletedEvent(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.timestamp = LocalDateTime.now();
    }
}