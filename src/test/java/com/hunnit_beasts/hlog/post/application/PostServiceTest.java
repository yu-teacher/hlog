package com.hunnit_beasts.hlog.post.application;

import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.application.mapper.PostDtoMapper;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import com.hunnit_beasts.hlog.post.application.service.PostServiceImpl;
import com.hunnit_beasts.hlog.post.domain.event.PostCreatedEvent;
import com.hunnit_beasts.hlog.post.domain.event.PostEvent;
import com.hunnit_beasts.hlog.post.domain.model.entity.Post;
import com.hunnit_beasts.hlog.post.domain.model.vo.Content;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostId;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import com.hunnit_beasts.hlog.post.domain.model.vo.Title;
import com.hunnit_beasts.hlog.post.domain.repository.PostRepository;
import com.hunnit_beasts.hlog.post.domain.service.TagManagementService;
import com.hunnit_beasts.hlog.post.infrastructure.messaging.PostEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("포스트 서비스 테스트")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostDtoMapper postDtoMapper;

    @Mock
    private TagManagementService tagManagementService;

    @Mock
    private PostEventPublisher eventPublisher;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, postDtoMapper, tagManagementService, eventPublisher);
    }

    @Test
    @DisplayName("포스트 생성 시 포스트가 저장되고 이벤트가 발행되어야 한다")
    void createPost_ShouldSavePostAndPublishEvent() {
        // Given
        CreatePostDto createPostDto = CreatePostDto.builder()
                .title("Test Title")
                .content("Test Content")
                .contentFormat("MARKDOWN")
                .authorId(UUID.randomUUID())
                .tagNames(new HashSet<>(Arrays.asList("java", "spring")))
                .publish(false)
                .build();

        Post post = Post.create(
                Title.of(createPostDto.getTitle()),
                Content.ofMarkdown(createPostDto.getContent()),
                createPostDto.getAuthorId(),
                createPostDto.getTagNames()
        );

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .contentFormat(post.getContent().getFormat().toString())
                .status(post.getStatus().toString())
                .authorId(post.getAuthorId())
                .tagNames(post.getTagNames())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        when(postDtoMapper.toEntity(createPostDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postDtoMapper.toDto(post)).thenReturn(expectedPostDto);

        // When
        PostDto result = postService.createPost(createPostDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostDto.getId(), result.getId());
        assertEquals(expectedPostDto.getTitle(), result.getTitle());

        verify(tagManagementService).processPostTags(any(Post.class), anySet());
        verify(postRepository).save(post);

        ArgumentCaptor<PostCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PostCreatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());

        PostCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(post.getId(), capturedEvent.getPostId());
    }

    @Test
    @DisplayName("ID로 포스트 조회 시 포스트가 반환되어야 한다")
    void getPostById_ShouldReturnPost() {
        // Given
        UUID postId = UUID.randomUUID();
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        // 포스트를 활성 상태로 만들기 - 옵션 1
        post.publish(); // post를 PUBLISHED 상태로 변경

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId().getValue())
                .title(post.getTitle().getValue())
                .content(post.getContent().getValue())
                .build();

        when(postRepository.findById(any(PostId.class))).thenReturn(Optional.of(post));
        when(postDtoMapper.toDto(post)).thenReturn(expectedPostDto);

        // When
        PostDto result = postService.getPostById(postId);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostDto.getId(), result.getId());
        assertEquals(expectedPostDto.getTitle(), result.getTitle());

        verify(postRepository).findById(any(PostId.class));
    }

    @Test
    @DisplayName("포스트 업데이트 시 포스트가 수정되고 저장되어야 한다")
    void updatePost_ShouldUpdateAndSavePost() {
        // Given
        UUID postId = UUID.randomUUID();
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .title("Updated Title")
                .content("Updated Content")
                .contentFormat("MARKDOWN")
                .tagNames(new HashSet<>(Arrays.asList("java", "spring")))
                .build();

        Post post = Post.create(
                Title.of("Old Title"),
                Content.ofMarkdown("Old Content"),
                UUID.randomUUID(),
                new HashSet<>(Collections.singletonList("java"))
        );

        // 포스트를 PUBLISHED 상태로 변경
        post.publish();

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId().getValue())
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(postRepository.findById(any(PostId.class))).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postDtoMapper.toDto(any(Post.class))).thenReturn(expectedPostDto);

        // When
        PostDto result = postService.updatePost(postId, updatePostDto);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostDto.getId(), result.getId());
        assertEquals(expectedPostDto.getTitle(), result.getTitle());
        assertEquals(expectedPostDto.getContent(), result.getContent());

        verify(postDtoMapper).updateEntityFromDto(post, updatePostDto);
        verify(tagManagementService).processPostTags(eq(post), anySet());
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).publish(any(PostEvent.class));
    }

    @Test
    @DisplayName("포스트 발행 시 상태가 변경되고 저장되어야 한다")
    void publishPost_ShouldChangeStatusAndSave() {
        // Given
        UUID postId = UUID.randomUUID();
        Post post = Post.create(
                Title.of("Test Title"),
                Content.ofMarkdown("Test Content"),
                UUID.randomUUID(),
                new HashSet<>()
        );

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId().getValue())
                .title(post.getTitle().getValue())
                .status(PostStatus.PUBLISHED.toString())
                .build();

        when(postRepository.findById(any(PostId.class))).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postDtoMapper.toDto(post)).thenReturn(expectedPostDto);

        // When
        PostDto result = postService.publishPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(expectedPostDto.getId(), result.getId());
        assertEquals(PostStatus.PUBLISHED.toString(), result.getStatus());

        verify(postRepository).save(post);
        verify(eventPublisher).publish((PostEvent) any());
    }
}
