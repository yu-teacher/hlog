package com.hunnit_beasts.hlog.post.api.controller;

import com.hunnit_beasts.hlog.post.api.dto.CreatePostRequest;
import com.hunnit_beasts.hlog.post.api.dto.PostResponse;
import com.hunnit_beasts.hlog.post.api.dto.PostSummaryResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdatePostRequest;
import com.hunnit_beasts.hlog.post.api.mapper.PostApiMapper;
import com.hunnit_beasts.hlog.post.application.dto.CreatePostDto;
import com.hunnit_beasts.hlog.post.application.dto.PostDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdatePostDto;
import com.hunnit_beasts.hlog.post.application.service.PostService;
import com.hunnit_beasts.hlog.post.domain.model.vo.PostStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostApiMapper postApiMapper;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        CreatePostDto createPostDto = postApiMapper.toCreatePostDto(request);
        PostDto postDto = postService.createPost(createPostDto);
        return new ResponseEntity<>(postApiMapper.toPostResponse(postDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostSummaryResponse>> getPosts(
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) boolean includePrivate,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<PostDto> posts;

        // 인증된 사용자 확인
        boolean isAuthenticated = userDetails != null;
        UUID currentUserId = isAuthenticated ? UUID.fromString(userDetails.getUsername()) : null;
        boolean isAdmin = isAuthenticated && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 관리자는 모든 포스트 접근 가능
        if (isAdmin) {
            if (authorId != null) {
                posts = status != null
                        ? postService.getPostsByStatusAndAuthor(status, authorId)
                        : postService.getPostsByAuthorIdIncludingDeleted(authorId);
            } else {
                posts = status != null
                        ? postService.getPostsByStatus(status)
                        : postService.getAllPostsIncludingDeleted();
            }
        }
        // 일반 사용자는 자신의 모든 포스트와 타인의 PUBLISHED 포스트만 조회 가능
        else if (isAuthenticated) {
            if (status != null) {
                // DRAFT, ARCHIVED는 자신의 것만 조회 가능
                if (status == PostStatus.DRAFT || status == PostStatus.ARCHIVED) {
                    if (authorId != null && authorId.equals(currentUserId)) {
                        posts = postService.getPostsByStatusAndAuthor(status, authorId);
                    } else {
                        posts = List.of(); // 빈 리스트 반환
                    }
                }
                // PUBLISHED는 모두 조회 가능
                else if (status == PostStatus.PUBLISHED) {
                    posts = authorId != null
                            ? postService.getPostsByStatusAndAuthor(status, authorId)
                            : postService.getPostsByStatus(status);
                }
                // DELETED는 조회 불가
                else {
                    posts = List.of();
                }
            } else {
                if (authorId != null) {
                    if (authorId.equals(currentUserId)) {
                        // 자신의 모든 포스트 조회 (DELETED 제외)
                        posts = postService.getPostsByAuthorId(authorId);
                    } else {
                        // 타인의 PUBLISHED 포스트만 조회
                        posts = postService.getPublishedPostsByAuthorId(authorId);
                    }
                } else {
                    // 모든 PUBLISHED 포스트 조회
                    posts = postService.getPublishedPosts();
                }
            }
        }
        // 비인증 사용자는 PUBLISHED 포스트만 조회 가능
        else {
            if (status != null && status == PostStatus.PUBLISHED) {
                posts = authorId != null
                        ? postService.getPostsByStatusAndAuthor(status, authorId)
                        : postService.getPostsByStatus(status);
            } else if (status == null) {
                posts = authorId != null
                        ? postService.getPublishedPostsByAuthorId(authorId)
                        : postService.getPublishedPosts();
            } else {
                posts = List.of();
            }
        }

        List<PostSummaryResponse> response = posts.stream()
                .map(postApiMapper::toPostSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 개별 포스트 조회도 권한에 따라 제한
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostDto postDto = postService.getPostById(postId);
        // 권한 확인
        if (shouldAllowAccess(postDto, userDetails)) {
            return ResponseEntity.ok(postApiMapper.toPostResponse(postDto));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/tag/{tagName}")
    public ResponseEntity<List<PostSummaryResponse>> getPostsByTag(@PathVariable("tagName") String tagName) {
        List<PostDto> posts = postService.getPostsByTagName(tagName);
        List<PostSummaryResponse> response = posts.stream()
                .map(postApiMapper::toPostSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable("postId") UUID postId,
            @Valid @RequestBody UpdatePostRequest request) {
        UpdatePostDto updatePostDto = postApiMapper.toUpdatePostDto(request);
        PostDto updatedPost = postService.updatePost(postId, updatePostDto);
        return ResponseEntity.ok(postApiMapper.toPostResponse(updatedPost));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}/publish")
    public ResponseEntity<PostResponse> publishPost(@PathVariable("postId") UUID postId) {
        PostDto publishedPost = postService.publishPost(postId);
        return ResponseEntity.ok(postApiMapper.toPostResponse(publishedPost));
    }

    @PutMapping("/{postId}/unpublish")
    public ResponseEntity<PostResponse> unpublishPost(@PathVariable("postId") UUID postId) {
        PostDto unpublishedPost = postService.unpublishPost(postId);
        return ResponseEntity.ok(postApiMapper.toPostResponse(unpublishedPost));
    }

    @GetMapping("/{postId}/tags")
    public ResponseEntity<Set<String>> getPostTags(@PathVariable("postId") UUID postId) {
        Set<String> tags = postService.getTagsForPost(postId);
        return ResponseEntity.ok(tags);
    }

    private boolean shouldAllowAccess(PostDto post, UserDetails userDetails) {
        if (userDetails == null) {
            // 비인증 사용자는 PUBLISHED 포스트만 접근 가능
            return Objects.equals(post.getStatus(), PostStatus.PUBLISHED.toString());
        }

        UUID currentUserId = UUID.fromString(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // 관리자는 모든 포스트 접근 가능
            return true;
        } else if (post.getAuthorId().equals(currentUserId)) {
            // 작성자는 DELETED를 제외한 모든 상태 접근 가능
            return !Objects.equals(post.getStatus(), PostStatus.DELETED.toString());
        } else {
            // 일반 사용자는 타인의 PUBLISHED 포스트만 접근 가능
            return Objects.equals(post.getStatus(), PostStatus.PUBLISHED.toString());
        }
    }
}