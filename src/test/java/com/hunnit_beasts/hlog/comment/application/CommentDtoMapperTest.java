package com.hunnit_beasts.hlog.comment.application;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.mapper.CommentDtoMapper;
import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentDtoMapperTest {

    private final CommentDtoMapper mapper = new CommentDtoMapper();

    @Test
    @DisplayName("Comment 엔티티를 CommentDto로 변환할 수 있다")
    void testToDto() {
        // given
        UUID idValue = UUID.randomUUID();
        CommentId commentId = CommentId.of(idValue);
        UUID targetId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID parentIdValue = UUID.randomUUID();
        CommentId parentId = CommentId.of(parentIdValue);
        String content = "Test content";
        CommentStatus status = CommentStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Comment comment = Comment.reconstitute(
                commentId,
                CommentContent.of(content),
                targetId,
                authorId,
                parentId,
                CommentDepth.of(1),
                status,
                createdAt,
                updatedAt
        );

        // when
        CommentDto dto = mapper.toDto(comment);

        // then
        assertEquals(idValue, dto.getId());
        assertEquals(content, dto.getContent());
        assertEquals(targetId, dto.getTargetId());
        assertEquals(authorId, dto.getAuthorId());
        assertEquals(parentIdValue, dto.getParentId());
        assertEquals(1, dto.getDepth());
        assertEquals(status.toString(), dto.getStatus());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    @DisplayName("CreateCommentDto를 최상위 Comment 엔티티로 변환할 수 있다")
    void testToEntityFromCreateDto() {
        // given
        String content = "New comment";
        UUID targetId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        CreateCommentDto createDto = CreateCommentDto.builder()
                .content(content)
                .targetId(targetId)
                .authorId(authorId)
                .parentId(null) // 최상위 댓글
                .build();

        // when
        Comment comment = mapper.toEntity(createDto);

        // then
        assertNotNull(comment);
        assertEquals(content, comment.getContent().getValue());
        assertEquals(targetId, comment.getTargetId());
        assertEquals(authorId, comment.getAuthorId());
        assertEquals(null, comment.getParentId());
        assertEquals(0, comment.getDepth().getValue());
        assertEquals(CommentStatus.ACTIVE, comment.getStatus());
    }
}
