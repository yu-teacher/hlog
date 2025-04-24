package com.hunnit_beasts.hlog.comment.domain;

import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    @DisplayName("최상위 댓글을 생성할 수 있다")
    void testCreateRootComment() {
        // given
        String content = "This is a comment";
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        // when
        Comment comment = Comment.create(
                CommentContent.of(content),
                postId,
                authorId
        );

        // then
        assertNotNull(comment.getId());
        assertEquals(content, comment.getContent().getValue());
        assertEquals(postId, comment.getTargetId());
        assertEquals(authorId, comment.getAuthorId());
        assertNull(comment.getParentId());
        assertEquals(0, comment.getDepth().getValue());
        assertEquals(CommentStatus.ACTIVE, comment.getStatus());
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
        assertEquals(comment.getCreatedAt(), comment.getUpdatedAt());
    }

    @Test
    @DisplayName("대댓글을 생성할 수 있다")
    void testCreateReplyComment() {
        // given
        String content = "This is a reply";
        UUID postId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CommentId parentId = CommentId.create();
        CommentDepth parentDepth = CommentDepth.of(1);

        // when
        Comment comment = Comment.createReply(
                CommentContent.of(content),
                postId,
                authorId,
                parentId,
                parentDepth
        );

        // then
        assertNotNull(comment.getId());
        assertEquals(content, comment.getContent().getValue());
        assertEquals(postId, comment.getTargetId());
        assertEquals(authorId, comment.getAuthorId());
        assertEquals(parentId, comment.getParentId());
        assertEquals(2, comment.getDepth().getValue());
        assertEquals(CommentStatus.ACTIVE, comment.getStatus());
    }

    @Test
    @DisplayName("댓글 내용을 수정할 수 있다")
    void testUpdateContent() {
        // given
        Comment comment = Comment.create(
                CommentContent.of("Original content"),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        String updatedContent = "Updated content";
        LocalDateTime originalUpdatedAt = comment.getUpdatedAt();

        // 시간 지연을 위한 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // when
        comment.updateContent(CommentContent.of(updatedContent));

        // then
        assertEquals(updatedContent, comment.getContent().getValue());
        assertTrue(comment.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다")
    void testDeleteComment() {
        // given
        Comment comment = Comment.create(
                CommentContent.of("Content to be deleted"),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        LocalDateTime originalUpdatedAt = comment.getUpdatedAt();

        // 시간 지연을 위한 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // when
        comment.delete();

        // then
        assertEquals(CommentStatus.DELETED, comment.getStatus());
        assertTrue(comment.getUpdatedAt().isAfter(originalUpdatedAt));
        assertTrue(comment.isDeleted());
        assertFalse(comment.isActive());
    }

    @Test
    @DisplayName("엔티티를 복원할 수 있다")
    void testReconstituteComment() {
        // given
        CommentId id = CommentId.create();
        CommentContent content = CommentContent.of("Reconstituted content");
        UUID targetId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CommentId parentId = CommentId.create();
        CommentDepth depth = CommentDepth.of(2);
        CommentStatus status = CommentStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        Comment comment = Comment.reconstitute(
                id, content, targetId, authorId, parentId, depth, status, createdAt, updatedAt
        );

        // then
        assertEquals(id, comment.getId());
        assertEquals(content, comment.getContent());
        assertEquals(targetId, comment.getTargetId());
        assertEquals(authorId, comment.getAuthorId());
        assertEquals(parentId, comment.getParentId());
        assertEquals(depth, comment.getDepth());
        assertEquals(status, comment.getStatus());
        assertEquals(createdAt, comment.getCreatedAt());
        assertEquals(updatedAt, comment.getUpdatedAt());
    }
}
