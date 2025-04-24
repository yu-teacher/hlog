package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;

import java.util.List;
import java.util.UUID;

public interface SeriesService {
    SeriesDto createSeries(CreateSeriesDto dto);
    SeriesDto getSeriesById(UUID seriesId);
    List<SeriesDto> getSeriesByAuthorId(UUID authorId);
    List<SeriesDto> getActiveSeries();
    SeriesDto updateSeries(UUID seriesId, UpdateSeriesDto dto);
    void deleteSeries(UUID seriesId);
    SeriesDto addPostToSeries(UUID seriesId, UUID postId, Integer order);
    SeriesDto removePostFromSeries(UUID seriesId, UUID postId);
    SeriesDto updatePostOrderInSeries(UUID seriesId, UUID postId, Integer newOrder);
}