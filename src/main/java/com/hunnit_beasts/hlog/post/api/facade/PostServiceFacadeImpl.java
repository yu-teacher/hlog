package com.hunnit_beasts.hlog.post.api.facade;

import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceFacadeImpl implements PostServiceFacade {

    private final PostService postService;

    @Override
    public boolean existsById(UUID postId) {
        try {
            postService.getPostById(postId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public PostDto getPostById(UUID postId) {
        return postService.getPostById(postId);
    }

    @Override
    public List<PostDto> getRecentPosts(int limit) {
        return postService.getPublishedPosts().stream()
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByAuthorId(UUID authorId, int limit) {
        return postService.getPublishedPostsByAuthorId(authorId).stream()
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}