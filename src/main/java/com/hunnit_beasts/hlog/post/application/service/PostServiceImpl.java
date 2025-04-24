package com.hunnit_beasts.hlog.post.application.service;

import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.application.mapper.PostDtoMapper;
import com.hunnit_beasts.hlog.post.domain.event.PostCreatedEvent;
import com.hunnit_beasts.hlog.post.domain.event.PostDeletedEvent;
import com.hunnit_beasts.hlog.post.domain.event.PostPublishedEvent;
import com.hunnit_beasts.hlog.post.domain.event.PostUpdatedEvent;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.domain.service.TagManagementService;
import com.hunnit_beasts.hlog.post.infrastructure.messaging.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostDtoMapper postDtoMapper;
    private final TagManagementService tagManagementService;
    private final PostEventPublisher eventPublisher;

    @Override
    @Transactional
    public PostDto createPost(CreatePostDto dto) {
        Post post = postDtoMapper.toEntity(dto);

        // Process tags
        tagManagementService.processPostTags(post, new HashSet<>());

        Post savedPost = postRepository.save(post);

        // Publish event
        eventPublisher.publish(new PostCreatedEvent(savedPost));

        return postDtoMapper.toDto(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPostById(UUID postId) {
        Post post = findPostEntityById(postId);
        return postDtoMapper.toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByAuthorId(UUID authorId) {
        return postRepository.findActiveByAuthorId(authorId).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPublishedPosts() {
        return postRepository.findPublishedPosts().stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPublishedPostsByAuthorId(UUID authorId) {
        return postRepository.findPublishedByAuthorId(authorId).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByTagName(String tagName) {
        return postRepository.findActiveByTagName(tagName).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDto updatePost(UUID postId, UpdatePostDto dto) {
        Post post = findPostEntityById(postId);

        // Store old tags for comparison
        Set<String> oldTags = new HashSet<>(post.getTagNames());

        // Update post
        postDtoMapper.updateEntityFromDto(post, dto);

        // Process tags
        tagManagementService.processPostTags(post, oldTags);

        Post updatedPost = postRepository.save(post);

        // Publish event
        eventPublisher.publish(new PostUpdatedEvent(updatedPost));

        return postDtoMapper.toDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        Post post = findPostEntityById(postId);

        // 소프트 삭제로 변경
        post.delete();
        Post deletedPost = postRepository.save(post);

        // Publish event
        eventPublisher.publish(new PostDeletedEvent(deletedPost));
    }

    @Override
    @Transactional
    public PostDto publishPost(UUID postId) {
        Post post = findPostEntityById(postId);
        post.publish();
        Post savedPost = postRepository.save(post);

        // Publish event
        eventPublisher.publish(new PostPublishedEvent(savedPost));

        return postDtoMapper.toDto(savedPost);
    }

    @Override
    @Transactional
    public PostDto unpublishPost(UUID postId) {
        Post post = findPostEntityById(postId);
        post.unpublish();
        Post savedPost = postRepository.save(post);

        // Publish event
        eventPublisher.publish(new PostUpdatedEvent(savedPost));

        return postDtoMapper.toDto(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getTagsForPost(UUID postId) {
        Post post = findActivePostEntityById(postId);
        return post.getTagNames();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByStatus(PostStatus status) {
        return postRepository.findByStatus(status).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByStatusAndAuthor(PostStatus status, UUID authorId) {
        return postRepository.findByStatusAndAuthorId(status, authorId).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsIncludingDeleted() {
        return postRepository.findAll().stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByAuthorIdIncludingDeleted(UUID authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .map(postDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private Post findActivePostEntityById(UUID postId) {
        return postRepository.findActiveById(PostId.of(postId))
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }

    private Post findPostEntityById(UUID postId) {
        return postRepository.findById(PostId.of(postId))
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }
}