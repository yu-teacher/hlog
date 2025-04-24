package com.hunnit_beasts.hlog.post.api.controller;

import com.hunnit_beasts.hlog.post.api.dto.CreateSeriesRequest;
import com.hunnit_beasts.hlog.post.api.dto.SeriesResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdateSeriesRequest;
import com.hunnit_beasts.hlog.post.api.mapper.SeriesApiMapper;
import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.post.application.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final SeriesApiMapper seriesApiMapper;

    @PostMapping
    public ResponseEntity<SeriesResponse> createSeries(@Valid @RequestBody CreateSeriesRequest request) {
        CreateSeriesDto createSeriesDto = seriesApiMapper.toCreateSeriesDto(request);
        SeriesDto seriesDto = seriesService.createSeries(createSeriesDto);
        return new ResponseEntity<>(seriesApiMapper.toSeriesResponse(seriesDto), HttpStatus.CREATED);
    }

    @GetMapping("/{seriesId}")
    public ResponseEntity<SeriesResponse> getSeriesById(@PathVariable("seriesId") UUID seriesId) {
        SeriesDto seriesDto = seriesService.getSeriesById(seriesId);
        return ResponseEntity.ok(seriesApiMapper.toSeriesResponse(seriesDto));
    }

    @GetMapping
    public ResponseEntity<List<SeriesResponse>> getAllSeries(
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {

        List<SeriesDto> series;
        if (authorId != null) {
            series = seriesService.getSeriesByAuthorId(authorId);
        } else {
            series = activeOnly
                    ? seriesService.getActiveSeries()
                    : seriesService.getActiveSeries(); // 기본적으로 active 시리즈만 조회
        }

        List<SeriesResponse> response = series.stream()
                .map(seriesApiMapper::toSeriesResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{seriesId}")
    public ResponseEntity<SeriesResponse> updateSeries(
            @PathVariable("seriesId") UUID seriesId,
            @Valid @RequestBody UpdateSeriesRequest request) {

        UpdateSeriesDto updateSeriesDto = seriesApiMapper.toUpdateSeriesDto(request);
        SeriesDto updatedSeries = seriesService.updateSeries(seriesId, updateSeriesDto);

        return ResponseEntity.ok(seriesApiMapper.toSeriesResponse(updatedSeries));
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> deleteSeries(@PathVariable("seriesId") UUID seriesId) {
        seriesService.deleteSeries(seriesId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{seriesId}/posts/{postId}")
    public ResponseEntity<SeriesResponse> addPostToSeries(
            @PathVariable("seriesId") UUID seriesId,
            @PathVariable("postId") UUID postId,
            @RequestParam(required = false) Integer order) {

        SeriesDto seriesDto = seriesService.addPostToSeries(seriesId, postId, order);
        return ResponseEntity.ok(seriesApiMapper.toSeriesResponse(seriesDto));
    }

    @DeleteMapping("/{seriesId}/posts/{postId}")
    public ResponseEntity<SeriesResponse> removePostFromSeries(
            @PathVariable("seriesId") UUID seriesId,
            @PathVariable("postId") UUID postId) {

        SeriesDto seriesDto = seriesService.removePostFromSeries(seriesId, postId);
        return ResponseEntity.ok(seriesApiMapper.toSeriesResponse(seriesDto));
    }

    @PutMapping("/{seriesId}/posts/{postId}/order")
    public ResponseEntity<SeriesResponse> updatePostOrderInSeries(
            @PathVariable("seriesId") UUID seriesId,
            @PathVariable("postId") UUID postId,
            @RequestParam Integer order) {

        SeriesDto seriesDto = seriesService.updatePostOrderInSeries(seriesId, postId, order);
        return ResponseEntity.ok(seriesApiMapper.toSeriesResponse(seriesDto));
    }
}