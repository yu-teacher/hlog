package com.hunnit_beasts.hlog.post.domain.event;

import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;

import java.time.LocalDateTime;

public interface SeriesEvent {
    SeriesId getSeriesId();
    LocalDateTime getTimestamp();
}