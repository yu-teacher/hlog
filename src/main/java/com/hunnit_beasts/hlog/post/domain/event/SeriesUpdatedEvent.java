package com.hunnit_beasts.hlog.post.domain.event;

import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesName;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SeriesUpdatedEvent implements SeriesEvent {
    private final SeriesId seriesId;
    private final UUID authorId;
    private final SeriesName name;
    private final LocalDateTime timestamp;

    public SeriesUpdatedEvent(Series series) {
        this.seriesId = series.getId();
        this.authorId = series.getAuthorId();
        this.name = series.getName();
        this.timestamp = LocalDateTime.now();
    }
}