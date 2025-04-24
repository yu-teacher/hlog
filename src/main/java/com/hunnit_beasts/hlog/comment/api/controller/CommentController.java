package com.hunnit_beasts.hlog.comment.api.controller;

import com.hunnit_beasts.hlog.comment.api.dto.CommentListResponse;
import com.hunnit_beasts.hlog.comment.api.dto.CommentResponse;
import com.hunnit_beasts.hlog.comment.api.dto.CreateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.dto.UpdateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.mapper.CommentApiMapper;
import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.service.CommentService;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentApiMapper commentApiMapper;
    private final UserServiceFacade userServiceFacade;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID authorId = UUID.fromString(userDetails.getUsername());
        CommentDto commentDto = commentService.createComment(
                commentApiMapper.toCreateDto(request, authorId)
        );

        String authorName = userServiceFacade.getUsernameById(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentApiMapper.toResponse(commentDto, authorName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 권한 검증 (본인 댓글만 수정 가능)
        UUID authorId = UUID.fromString(userDetails.getUsername());
        CommentDto existingComment = commentService.getComment(id);

        if (!existingComment.getAuthorId().equals(authorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ID 경로 변수와 요청 바디의 ID가 일치하는지 검증
        if (!request.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        CommentDto updatedComment = commentService.updateComment(
                commentApiMapper.toUpdateDto(request)
        );

        String authorName = userServiceFacade.getUsernameById(userDetails.getUsername());

        return ResponseEntity.ok()
                .body(commentApiMapper.toResponse(updatedComment, authorName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 권한 검증 (본인 댓글만 삭제 가능)
        UUID authorId = UUID.fromString(userDetails.getUsername());
        CommentDto existingComment = commentService.getComment(id);

        if (!existingComment.getAuthorId().equals(authorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable UUID id) {
        CommentDto commentDto = commentService.getComment(id);
        String authorName = userServiceFacade.getUsernameById(String.valueOf(commentDto.getAuthorId()));

        return ResponseEntity.ok()
                .body(commentApiMapper.toResponse(commentDto, authorName));
    }

    @GetMapping("/target/{targetId}")
    public ResponseEntity<CommentListResponse> getCommentsByTarget(@PathVariable UUID targetId) {
        List<CommentDto> comments = commentService.getCommentsByTarget(targetId);

        List<CommentResponse> commentResponses = comments.stream()
                .map(dto -> {
                    String authorName = userServiceFacade.getUsernameById(String.valueOf(dto.getAuthorId()));
                    return commentApiMapper.toResponse(dto, authorName);
                })
                .collect(Collectors.toList());

        CommentListResponse response = CommentListResponse.builder()
                .comments(commentResponses)
                .totalCount(commentResponses.size())
                .build();

        return ResponseEntity.ok().body(response);
    }
}