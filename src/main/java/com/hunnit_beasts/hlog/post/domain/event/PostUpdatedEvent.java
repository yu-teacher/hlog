package com.hunnit_beasts.hlog.post.domain.event;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.Title;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PostUpdatedEvent implements PostEvent {
    private final PostId postId;
    private final UUID authorId;
    private final Title title;
    private final LocalDateTime timestamp;

    public PostUpdatedEvent(Post post) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.timestamp = LocalDateTime.now();
    }
}