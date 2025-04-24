package com.hunnit_beasts.hlog.post.domain.service;

import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.entity.Series;
import com.hunnit_beasts.hlog.post.domain.model.entity.SeriesPost;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesPostRepository;
import com.hunnit_beasts.hlog.post.domain.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesManagementService {
    private final SeriesRepository seriesRepository;
    private final SeriesPostRepository seriesPostRepository;

    public void addPostToSeries(Series series, Post post, Integer order) {
        // 시리즈에 포스트가 이미 있는지 확인
        boolean exists = seriesPostRepository.existsBySeriesIdAndPostId(series.getId(), post.getId());

        if (!exists) {
            // 순서가 지정되지 않았으면 마지막에 추가
            if (order == null) {
                order = seriesPostRepository.getMaxOrderInSeries(series.getId()) + 1;
            }

            SeriesPost seriesPost = SeriesPost.create(series.getId(), post.getId(), order);
            seriesPostRepository.save(seriesPost);
        }
    }

    public void removePostFromSeries(Series series, Post post) {
        seriesPostRepository.findBySeriesIdAndPostId(series.getId(), post.getId())
                .ifPresent(seriesPostRepository::delete);
    }

    public void updatePostOrder(Series series, Post post, Integer newOrder) {
        seriesPostRepository.findBySeriesIdAndPostId(series.getId(), post.getId())
                .ifPresent(seriesPost -> {
                    seriesPost.updateOrder(newOrder);
                    seriesPostRepository.save(seriesPost);
                });
    }

    public List<Post> getPostsInSeries(Series series, List<Post> allPosts) {
        List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

        return seriesPosts.stream()
                .sorted(Comparator.comparing(SeriesPost::getOrder))
                .map(SeriesPost::getPostId)
                .map(postId -> findPostById(allPosts, postId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Optional<Post> getPreviousPostInSeries(Series series, Post currentPost, List<Post> allPosts) {
        List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

        Optional<SeriesPost> currentSeriesPost = seriesPosts.stream()
                .filter(sp -> sp.getPostId().equals(currentPost.getId()))
                .findFirst();

        if (currentSeriesPost.isPresent()) {
            Integer currentOrder = currentSeriesPost.get().getOrder();

            return seriesPosts.stream()
                    .filter(sp -> sp.getOrder() < currentOrder)
                    .max(Comparator.comparing(SeriesPost::getOrder))
                    .map(SeriesPost::getPostId)
                    .flatMap(postId -> findPostById(allPosts, postId));
        }

        return Optional.empty();
    }

    public Optional<Post> getNextPostInSeries(Series series, Post currentPost, List<Post> allPosts) {
        List<SeriesPost> seriesPosts = seriesPostRepository.findBySeriesId(series.getId());

        Optional<SeriesPost> currentSeriesPost = seriesPosts.stream()
                .filter(sp -> sp.getPostId().equals(currentPost.getId()))
                .findFirst();

        if (currentSeriesPost.isPresent()) {
            Integer currentOrder = currentSeriesPost.get().getOrder();

            return seriesPosts.stream()
                    .filter(sp -> sp.getOrder() > currentOrder)
                    .min(Comparator.comparing(SeriesPost::getOrder))
                    .map(SeriesPost::getPostId)
                    .flatMap(postId -> findPostById(allPosts, postId));
        }

        return Optional.empty();
    }

    private Optional<Post> findPostById(List<Post> posts, PostId postId) {
        return posts.stream()
                .filter(post -> post.getId().equals(postId))
                .findFirst();
    }
}