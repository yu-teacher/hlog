package com.hunnit_beasts.hlog.comment.domain;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentContentTest {

    @Test
    @DisplayName("유효한 내용으로 CommentContent를 생성할 수 있다")
    void testCreateValidContent() {
        // given
        String validContent = "This is a valid comment content";

        // when
        CommentContent commentContent = CommentContent.of(validContent);

        // then
        assertEquals(validContent, commentContent.getValue());
    }

    @Test
    @DisplayName("빈 내용으로 CommentContent를 생성할 수 없다")
    void testCreateEmptyContent() {
        // given
        String emptyContent = "";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommentContent.of(emptyContent);
        });

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    @DisplayName("null 내용으로 CommentContent를 생성할 수 없다")
    void testCreateNullContent() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommentContent.of(null);
        });

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    @DisplayName("최대 길이를 초과하는 내용으로 CommentContent를 생성할 수 없다")
    void testCreateTooLongContent() {
        // given
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1001; i++) {
            sb.append("a");
        }
        String tooLongContent = sb.toString();

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommentContent.of(tooLongContent);
        });

        assertTrue(exception.getMessage().contains("maximum length"));
    }
}
