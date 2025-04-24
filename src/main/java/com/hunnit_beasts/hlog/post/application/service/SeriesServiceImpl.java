package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.post.application.mapper.SeriesDtoMapper;
import com.hunnit_beasts.hlog.post.domain.event.SeriesCreatedEvent;
import com.hunnit_beasts.hlog.post.domain.event.SeriesUpdatedEvent;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesPostRepository;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesRepository;
import com.hunnit_beasts.hlog.post.infrastructure.messaging.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final PostRepository postRepository;
    private final SeriesPostRepository seriesPostRepository;
    private final SeriesDtoMapper seriesDtoMapper;
    private final PostEventPublisher eventPublisher;

    @Override
    @Transactional
    public SeriesDto createSeries(CreateSeriesDto dto) {
        Series series = seriesDtoMapper.toEntity(dto);
        Series savedSeries = seriesRepository.save(series);

        // Publish event
        eventPublisher.publish(new SeriesCreatedEvent(savedSeries));

        return seriesDtoMapper.toDto(savedSeries, new ArrayList<>(), new HashMap<>());
    }

    @Override
    @Transactional(readOnly = true)
    public SeriesDto getSeriesById(UUID seriesId) {
        Series series = findSeriesEntityById(seriesId);
        List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

        // Get all posts in this series
        Map<UUID, Post> postsMap = new HashMap<>();
        if (!seriesPosts.isEmpty()) {
            List<PostId> postIds = seriesPosts.stream()
                    .map(SeriesPost::getPostId)
                    .toList();

            for (PostId postId : postIds) {
                postRepository.findById(postId).ifPresent(post ->
                        postsMap.put(post.getId().getValue(), post)
                );
            }
        }

        return seriesDtoMapper.toDto(series, seriesPosts, postsMap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeriesDto> getSeriesByAuthorId(UUID authorId) {
        List<Series> seriesList = seriesRepository.findByAuthorId(authorId);
        return mapSeriesListToDtoList(seriesList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeriesDto> getActiveSeries() {
        List<Series> seriesList = seriesRepository.findByStatus(SeriesStatus.ACTIVE);
        return mapSeriesListToDtoList(seriesList);
    }

    @Override
    @Transactional
    public SeriesDto updateSeries(UUID seriesId, UpdateSeriesDto dto) {
        Series series = findSeriesEntityById(seriesId);
        seriesDtoMapper.updateEntityFromDto(series, dto);

        Series updatedSeries = seriesRepository.save(series);

        // Publish event
        eventPublisher.publish(new SeriesUpdatedEvent(updatedSeries));

        List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

        // Get all posts in this series
        Map<UUID, Post> postsMap = new HashMap<>();
        if (!seriesPosts.isEmpty()) {
            List<PostId> postIds = seriesPosts.stream()
                    .map(SeriesPost::getPostId)
                    .toList();

            for (PostId postId : postIds) {
                postRepository.findById(postId).ifPresent(post ->
                        postsMap.put(post.getId().getValue(), post)
                );
            }
        }

        return seriesDtoMapper.toDto(updatedSeries, seriesPosts, postsMap);
    }

    @Override
    @Transactional
    public void deleteSeries(UUID seriesId) {
        Series series = findSeriesEntityById(seriesId);

        // Remove all posts from series first
        seriesPostRepository.deleteBySeriesId(series.getId());

        // Then delete the series
        seriesRepository.delete(series);
    }

    @Override
    @Transactional
    public SeriesDto addPostToSeries(UUID seriesId, UUID postId, Integer order) {
        Series series = findSeriesEntityById(seriesId);
        Post post = findPostEntityById(postId);

        // Check if the post is already in the series
        if (!seriesPostRepository.existsBySeriesIdAndPostId(series.getId(), post.getId())) {
            // If order is not specified, add to the end
            if (order == null) {
                order = seriesPostRepository.getMaxOrderInSeries(series.getId()) + 1;
            }

            SeriesPost seriesPost = SeriesPost.create(series.getId(), post.getId(), order);
            seriesPostRepository.save(seriesPost);
        }

        // Publish event
        eventPublisher.publish(new SeriesUpdatedEvent(series));

        return getSeriesById(seriesId);
    }

    @Override
    @Transactional
    public SeriesDto removePostFromSeries(UUID seriesId, UUID postId) {
        Series series = findSeriesEntityById(seriesId);
        Post post = findPostEntityById(postId);

        seriesPostRepository.findBySeriesIdAndPostId(series.getId(), post.getId())
                .ifPresent(seriesPostRepository::delete);

        // Publish event
        eventPublisher.publish(new SeriesUpdatedEvent(series));

        return getSeriesById(seriesId);
    }

    @Override
    @Transactional
    public SeriesDto updatePostOrderInSeries(UUID seriesId, UUID postId, Integer newOrder) {
        Series series = findSeriesEntityById(seriesId);
        Post post = findPostEntityById(postId);

        seriesPostRepository.findBySeriesIdAndPostId(series.getId(), post.getId())
                .ifPresent(seriesPost -> {
                    seriesPost.updateOrder(newOrder);
                    seriesPostRepository.save(seriesPost);
                });

        // Publish event
        eventPublisher.publish(new SeriesUpdatedEvent(series));

        return getSeriesById(seriesId);
    }

    private List<SeriesDto> mapSeriesListToDtoList(List<Series> seriesList) {
        List<SeriesDto> result = new ArrayList<>();

        for (Series series : seriesList) {
            List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

            // Get all posts in this series
            Map<UUID, Post> postsMap = new HashMap<>();
            if (!seriesPosts.isEmpty()) {
                List<PostId> postIds = seriesPosts.stream()
                        .map(SeriesPost::getPostId)
                        .toList();

                for (PostId postId : postIds) {
                    postRepository.findById(postId).ifPresent(post ->
                            postsMap.put(post.getId().getValue(), post)
                    );
                }
            }

            result.add(seriesDtoMapper.toDto(series, seriesPosts, postsMap));
        }

        return result;
    }

    private Series findSeriesEntityById(UUID seriesId) {
        return seriesRepository.findById(SeriesId.of(seriesId))
                .orElseThrow(() -> new IllegalArgumentException("Series not found with ID: " + seriesId));
    }

    private Post findPostEntityById(UUID postId) {
        return postRepository.findById(PostId.of(postId))
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }
}