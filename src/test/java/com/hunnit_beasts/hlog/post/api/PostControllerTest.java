package com.hunnit_beasts.hlog.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunnit_beasts.hlog.post.api.dto.CreatePostRequest;
import com.hunnit_beasts.hlog.post.api.dto.PostResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdatePostRequest;
import com.hunnit_beasts.hlog.post.api.mapper.PostApiMapper;
import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("포스트 컨트롤러 테스트")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostApiMapper postApiMapper;

    @Test
    @DisplayName("포스트 생성 요청 시 생성된 포스트가 반환되어야 한다")
    void createPost_ShouldReturnCreatedPost() throws Exception {
        // Given
        UUID authorId = UUID.randomUUID();
        CreatePostRequest request = CreatePostRequest.builder()
                .title("Test Title")
                .content("Test Content")
                .contentFormat("MARKDOWN")
                .authorId(authorId)
                .tagNames(new HashSet<>(Arrays.asList("java", "spring")))
                .publish(false)
                .build();

        CreatePostDto createPostDto = CreatePostDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .authorId(request.getAuthorId())
                .tagNames(request.getTagNames())
                .publish(request.isPublish())
                .build();

        UUID postId = UUID.randomUUID();
        PostDto postDto = PostDto.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .status("DRAFT")
                .authorId(authorId)
                .tagNames(request.getTagNames())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PostResponse postResponse = PostResponse.builder()
                .id(postId)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .contentFormat(postDto.getContentFormat())
                .status(postDto.getStatus())
                .authorId(postDto.getAuthorId())
                .authorName("Test Author")
                .tagNames(postDto.getTagNames())
                .createdAt(postDto.getCreatedAt())
                .updatedAt(postDto.getUpdatedAt())
                .build();

        // 여기에 Mock 설정 추가
        when(postApiMapper.toCreatePostDto(any(CreatePostRequest.class))).thenReturn(createPostDto);
        when(postService.createPost(any(CreatePostDto.class))).thenReturn(postDto);
        when(postApiMapper.toPostResponse(any(PostDto.class))).thenReturn(postResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.authorName").value("Test Author"));

        verify(postApiMapper).toCreatePostDto(any(CreatePostRequest.class));
        verify(postService).createPost(any(CreatePostDto.class));
        verify(postApiMapper).toPostResponse(any(PostDto.class));
    }

    @Test
    @DisplayName("ID로 포스트 조회 요청 시 포스트가 반환되어야 한다")
    void getPostById_ShouldReturnPost() throws Exception {
        // Given
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        PostDto postDto = PostDto.builder()
                .id(postId)
                .title("Test Title")
                .content("Test Content")
                .contentFormat("MARKDOWN")
                .status("PUBLISHED")
                .authorId(authorId)
                .tagNames(new HashSet<>(Arrays.asList("java", "spring")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now())
                .build();

        PostResponse postResponse = PostResponse.builder()
                .id(postId)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .contentFormat(postDto.getContentFormat())
                .status(postDto.getStatus())
                .authorId(postDto.getAuthorId())
                .authorName("Test Author")
                .tagNames(postDto.getTagNames())
                .createdAt(postDto.getCreatedAt())
                .updatedAt(postDto.getUpdatedAt())
                .publishedAt(postDto.getPublishedAt())
                .build();

        when(postService.getPostById(postId)).thenReturn(postDto);
        when(postApiMapper.toPostResponse(postDto)).thenReturn(postResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(postDto.getTitle()))
                .andExpect(jsonPath("$.status").value(postDto.getStatus()));

        verify(postService).getPostById(postId);
        verify(postApiMapper).toPostResponse(postDto);
    }

    @Test
    @DisplayName("포스트 업데이트 요청 시 업데이트된 포스트가 반환되어야 한다")
    void updatePost_ShouldReturnUpdatedPost() throws Exception {
        // Given
        UUID postId = UUID.randomUUID();
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .contentFormat("MARKDOWN")
                .tagNames(new HashSet<>(Arrays.asList("java", "spring", "testing")))
                .build();

        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .tagNames(request.getTagNames())
                .build();

        UUID authorId = UUID.randomUUID();
        PostDto updatedPostDto = PostDto.builder()
                .id(postId)
                .title(request.getTitle())
                .content(request.getContent())
                .contentFormat(request.getContentFormat())
                .status("DRAFT")
                .authorId(authorId)
                .tagNames(request.getTagNames())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PostResponse postResponse = PostResponse.builder()
                .id(postId)
                .title(updatedPostDto.getTitle())
                .content(updatedPostDto.getContent())
                .contentFormat(updatedPostDto.getContentFormat())
                .status(updatedPostDto.getStatus())
                .authorId(updatedPostDto.getAuthorId())
                .authorName("Test Author")
                .tagNames(updatedPostDto.getTagNames())
                .createdAt(updatedPostDto.getCreatedAt())
                .updatedAt(updatedPostDto.getUpdatedAt())
                .build();

        // 중복된 when() 호출 제거 - any() 매처만 사용
        when(postApiMapper.toUpdatePostDto(any(UpdatePostRequest.class))).thenReturn(updatePostDto);
        when(postService.updatePost(eq(postId), any(UpdatePostDto.class))).thenReturn(updatedPostDto);
        when(postApiMapper.toPostResponse(any(PostDto.class))).thenReturn(postResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.content").value(request.getContent()));

        // verify도 any() 매처 사용으로 변경
        verify(postApiMapper).toUpdatePostDto(any(UpdatePostRequest.class));
        verify(postService).updatePost(eq(postId), any(UpdatePostDto.class));
        verify(postApiMapper).toPostResponse(any(PostDto.class));
    }

    @Test
    @DisplayName("포스트 발행 요청 시 발행된 포스트가 반환되어야 한다")
    void publishPost_ShouldReturnPublishedPost() throws Exception {
        // Given
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        PostDto publishedPostDto = PostDto.builder()
                .id(postId)
                .title("Test Title")
                .content("Test Content")
                .contentFormat("MARKDOWN")
                .status("PUBLISHED")
                .authorId(authorId)
                .tagNames(new HashSet<>(Collections.singletonList("java")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .publishedAt(LocalDateTime.now())
                .build();

        PostResponse postResponse = PostResponse.builder()
                .id(postId)
                .title(publishedPostDto.getTitle())
                .content(publishedPostDto.getContent())
                .contentFormat(publishedPostDto.getContentFormat())
                .status(publishedPostDto.getStatus())
                .authorId(publishedPostDto.getAuthorId())
                .authorName("Test Author")
                .tagNames(publishedPostDto.getTagNames())
                .createdAt(publishedPostDto.getCreatedAt())
                .updatedAt(publishedPostDto.getUpdatedAt())
                .publishedAt(publishedPostDto.getPublishedAt())
                .build();

        when(postService.publishPost(postId)).thenReturn(publishedPostDto);
        when(postApiMapper.toPostResponse(publishedPostDto)).thenReturn(postResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/posts/{postId}/publish", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        verify(postService).publishPost(postId);
        verify(postApiMapper).toPostResponse(publishedPostDto);
    }

    @Test
    @DisplayName("포스트 삭제 요청 시 204 상태코드가 반환되어야 한다")
    void deletePost_ShouldReturnNoContent() throws Exception {
        // Given
        UUID postId = UUID.randomUUID();
        doNothing().when(postService).deletePost(postId);

        // When & Then
        mockMvc.perform(delete("/api/v1/posts/{postId}", postId))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(postId);
    }
}
