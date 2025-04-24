package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostService {
    PostDto createPost(CreatePostDto dto);
    PostDto getPostById(UUID postId);
    List<PostDto> getPostsByAuthorId(UUID authorId);
    List<PostDto> getPublishedPosts();
    List<PostDto> getPublishedPostsByAuthorId(UUID authorId);
    List<PostDto> getPostsByTagName(String tagName);
    PostDto updatePost(UUID postId, UpdatePostDto dto);
    void deletePost(UUID postId);
    PostDto publishPost(UUID postId);
    PostDto unpublishPost(UUID postId);
    Set<String> getTagsForPost(UUID postId);

    List<PostDto> getPostsByStatus(PostStatus status);
    List<PostDto> getPostsByStatusAndAuthor(PostStatus status, UUID authorId);
    List<PostDto> getAllPostsIncludingDeleted();  // 관리자용
    List<PostDto> getPostsByAuthorIdIncludingDeleted(UUID authorId);  // 관리자용
}