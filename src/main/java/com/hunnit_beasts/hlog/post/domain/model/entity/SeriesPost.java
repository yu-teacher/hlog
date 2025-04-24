package com.hunnit_beasts.hlog.post.domain.model.entity;

import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesPostId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class SeriesPost {
    private final SeriesPostId id;
    private final SeriesId seriesId;
    private final PostId postId;
    private Integer order;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private SeriesPost(SeriesPostId id, SeriesId seriesId, PostId postId, Integer order, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.seriesId = Objects.requireNonNull(seriesId);
        this.postId = Objects.requireNonNull(postId);
        this.order = Objects.requireNonNull(order);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
    }

    public static SeriesPost create(SeriesId seriesId, PostId postId, Integer order) {
        return new SeriesPost(
                SeriesPostId.create(),
                seriesId,
                postId,
                order,
                LocalDateTime.now()
        );
    }

    public static SeriesPost reconstitute(
            SeriesPostId id, SeriesId seriesId, PostId postId,
            Integer order, LocalDateTime createdAt, LocalDateTime updatedAt) {

        SeriesPost seriesPost = new SeriesPost(id, seriesId, postId, order, createdAt);
        seriesPost.updatedAt = updatedAt;

        return seriesPost;
    }

    public void updateOrder(Integer order) {
        this.order = Objects.requireNonNull(order);
        this.updatedAt = LocalDateTime.now();
    }
}