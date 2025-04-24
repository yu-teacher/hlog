package com.hunnit_beasts.hlog.comment.application;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.UpdateCommentDto;
import com.hunnit_beasts.hlog.comment.application.mapper.CommentDtoMapper;
import com.hunnit_beasts.hlog.comment.application.service.CommentServiceImpl;
import com.hunnit_beasts.hlog.comment.domain.event.CommentCreatedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentDeletedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentUpdatedEvent;
import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.repository.CommentRepository;
import com.hunnit_beasts.hlog.comment.infrastructure.messaging.CommentEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentDtoMapper commentDtoMapper;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("최상위 댓글 생성 서비스 테스트")
    void testCreateRootComment() {
        // given
        CreateCommentDto createDto = CreateCommentDto.builder()
                .content("Test comment")
                .targetId(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .parentId(null)
                .build();

        Comment createdComment = mock(Comment.class);
        CommentId commentId = mock(CommentId.class);
        when(createdComment.getId()).thenReturn(commentId);

        CommentDto expectedDto = mock(CommentDto.class);

        when(commentRepository.save(any(Comment.class))).thenReturn(createdComment);
        when(commentDtoMapper.toDto(createdComment)).thenReturn(expectedDto);

        // when
        CommentDto resultDto = commentService.createComment(createDto);

        // then
        assertEquals(expectedDto, resultDto);
        verify(commentRepository).save(any(Comment.class));
        verify(commentDtoMapper).toDto(createdComment);
        verify(commentEventPublisher).publish(any(CommentCreatedEvent.class));
    }

    @Test
    @DisplayName("대댓글 생성 서비스 테스트")
    void testCreateReplyComment() {
        // given
        UUID parentIdValue = UUID.randomUUID();
        CommentId parentCommentId = CommentId.of(parentIdValue);
        CreateCommentDto createDto = CreateCommentDto.builder()
                .content("Reply comment")
                .targetId(UUID.randomUUID())
                .authorId(UUID.randomUUID())
                .parentId(parentIdValue)
                .build();

        Comment parentComment = mock(Comment.class);
        CommentDepth parentDepth = CommentDepth.of(1);
        when(parentComment.getDepth()).thenReturn(parentDepth);

        Comment createdComment = mock(Comment.class);
        CommentId commentId = mock(CommentId.class);
        when(createdComment.getId()).thenReturn(commentId);

        CommentDto expectedDto = mock(CommentDto.class);

        when(commentRepository.findById(any(CommentId.class))).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(createdComment);
        when(commentDtoMapper.toDto(createdComment)).thenReturn(expectedDto);

        // when
        CommentDto resultDto = commentService.createComment(createDto);

        // then
        assertEquals(expectedDto, resultDto);
        verify(commentRepository).findById(any(CommentId.class));
        verify(commentRepository).save(any(Comment.class));
        verify(commentDtoMapper).toDto(createdComment);
        verify(commentEventPublisher).publish(any(CommentCreatedEvent.class));
    }
    @Test
    @DisplayName("댓글 내용 수정 서비스 테스트")
    void testUpdateComment() {
        // given
        UUID commentIdValue = UUID.randomUUID();
        String updatedContent = "Updated content";
        UpdateCommentDto updateDto = UpdateCommentDto.builder()
                .id(commentIdValue)
                .content(updatedContent)
                .build();

        CommentId commentId = CommentId.of(commentIdValue);
        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getTargetId()).thenReturn(UUID.randomUUID());
        when(comment.getAuthorId()).thenReturn(UUID.randomUUID());

        CommentDto expectedDto = mock(CommentDto.class);

        when(commentRepository.findById(any(CommentId.class))).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentDtoMapper.toDto(comment)).thenReturn(expectedDto);

        // when
        CommentDto resultDto = commentService.updateComment(updateDto);

        // then
        assertEquals(expectedDto, resultDto);
        verify(comment).updateContent(any(CommentContent.class));
        verify(commentRepository).findById(any(CommentId.class));
        verify(commentRepository).save(comment);
        verify(commentDtoMapper).toDto(comment);
        verify(commentEventPublisher).publish(any(CommentUpdatedEvent.class));
    }

    @Test
    @DisplayName("댓글 삭제 서비스 테스트")
    void testDeleteComment() {
        // given
        UUID commentIdValue = UUID.randomUUID();
        CommentId commentId = CommentId.of(commentIdValue);
        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getTargetId()).thenReturn(UUID.randomUUID());
        when(comment.getAuthorId()).thenReturn(UUID.randomUUID());

        when(commentRepository.findById(any(CommentId.class))).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        // when
        commentService.deleteComment(commentIdValue);

        // then
        verify(comment).delete();
        verify(commentRepository).findById(any(CommentId.class));
        verify(commentRepository).save(comment);
        verify(commentEventPublisher).publish(any(CommentDeletedEvent.class));
    }

    @Test
    @DisplayName("ID로 댓글 조회 테스트")
    void testGetComment() {
        // given
        UUID commentIdValue = UUID.randomUUID();
        CommentId commentId = CommentId.of(commentIdValue);
        Comment comment = mock(Comment.class);
        CommentDto expectedDto = mock(CommentDto.class);

        when(commentRepository.findById(any(CommentId.class))).thenReturn(Optional.of(comment));
        when(commentDtoMapper.toDto(comment)).thenReturn(expectedDto);

        // when
        CommentDto resultDto = commentService.getComment(commentIdValue);

        // then
        assertEquals(expectedDto, resultDto);
        verify(commentRepository).findById(any(CommentId.class));
        verify(commentDtoMapper).toDto(comment);
    }

    @Test
    @DisplayName("타겟 ID로 댓글 목록 조회 테스트")
    void testGetCommentsByTarget() {
        // given
        UUID targetId = UUID.randomUUID();
        Comment comment1 = mock(Comment.class);
        Comment comment2 = mock(Comment.class);
        List<Comment> comments = Arrays.asList(comment1, comment2);

        CommentDto dto1 = mock(CommentDto.class);
        CommentDto dto2 = mock(CommentDto.class);

        // findActiveCommentByTargetId를 모킹해야 함
        when(commentRepository.findActiveCommentByTargetId(targetId)).thenReturn(comments);
        when(commentDtoMapper.toDto(comment1)).thenReturn(dto1);
        when(commentDtoMapper.toDto(comment2)).thenReturn(dto2);

        // when
        List<CommentDto> resultDtos = commentService.getCommentsByTarget(targetId);

        // then
        assertEquals(2, resultDtos.size());
        assertTrue(resultDtos.contains(dto1));
        assertTrue(resultDtos.contains(dto2));
        verify(commentRepository).findActiveCommentByTargetId(targetId);
        verify(commentDtoMapper, times(2)).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("부모 ID로 댓글 목록 조회 테스트")
    void testGetCommentsByParent() {
        // given
        UUID parentIdValue = UUID.randomUUID();
        CommentId parentId = CommentId.of(parentIdValue);
        Comment reply1 = mock(Comment.class);
        Comment reply2 = mock(Comment.class);
        List<Comment> replies = Arrays.asList(reply1, reply2);

        CommentDto dto1 = mock(CommentDto.class);
        CommentDto dto2 = mock(CommentDto.class);

        when(commentRepository.findActiveByParentId(any(CommentId.class))).thenReturn(replies);
        when(commentDtoMapper.toDto(reply1)).thenReturn(dto1);
        when(commentDtoMapper.toDto(reply2)).thenReturn(dto2);

        // when
        List<CommentDto> resultDtos = commentService.getCommentsByParent(parentIdValue);

        // then
        assertEquals(2, resultDtos.size());
        assertTrue(resultDtos.contains(dto1));
        assertTrue(resultDtos.contains(dto2));
        verify(commentRepository).findActiveByParentId(any(CommentId.class));  // findByParentId -> findActiveByParentId로 수정
        verify(commentDtoMapper, times(2)).toDto(any(Comment.class));
    }
}
