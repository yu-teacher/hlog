package com.hunnit_beasts.hlog.post.api.facade;

import com.hunnit_beasts.hlog.post.application.dto.PostDto;

import java.util.List;
import java.util.UUID;

public interface PostServiceFacade {
    boolean existsById(UUID postId);
    PostDto getPostById(UUID postId);
    List<PostDto> getRecentPosts(int limit);
    List<PostDto> getPostsByAuthorId(UUID authorId, int limit);
}