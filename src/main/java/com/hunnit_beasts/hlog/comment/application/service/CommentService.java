package com.hunnit_beasts.hlog.comment.application.service;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.UpdateCommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    CommentDto createComment(CreateCommentDto createCommentDto);
    CommentDto updateComment(UpdateCommentDto updateCommentDto);
    void deleteComment(UUID commentId);
    CommentDto getComment(UUID commentId);
    List<CommentDto> getCommentsByTarget(UUID targetId);
    List<CommentDto> getCommentsByParent(UUID parentId);
    List<CommentDto> getCommentsByAuthor(UUID authorId);
}