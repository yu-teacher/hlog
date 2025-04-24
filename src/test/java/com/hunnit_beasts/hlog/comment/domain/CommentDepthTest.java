package com.hunnit_beasts.hlog.comment.domain;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CommentDepthTest {

    @Test
    @DisplayName("유효한 깊이로 CommentDepth를 생성할 수 있다")
    void testCreateValidDepth() {
        // given
        int validDepth = 2;

        // when
        CommentDepth depth = CommentDepth.of(validDepth);

        // then
        assertEquals(validDepth, depth.getValue());
    }

    @Test
    @DisplayName("음수 깊이로 CommentDepth를 생성할 수 없다")
    void testCreateNegativeDepth() {
        // given
        int negativeDepth = -1;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommentDepth.of(negativeDepth);
        });

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("최대 깊이를 초과하는 값으로 CommentDepth를 생성할 수 없다")
    void testCreateExceedingMaxDepth() {
        // given
        int exceedingDepth = 4; // 최대 값은 3

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CommentDepth.of(exceedingDepth);
        });

        assertTrue(exception.getMessage().contains("Maximum comment depth"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    @DisplayName("댓글 깊이를 증가시킬 수 있다")
    void testIncrementDepth(int initialDepth) {
        // given
        CommentDepth depth = CommentDepth.of(initialDepth);

        // when
        CommentDepth incremented = depth.increment();

        // then
        assertEquals(initialDepth + 1, incremented.getValue());
    }

    @Test
    @DisplayName("최대 깊이에서는 더 이상 증가시킬 수 없다")
    void testIncrementMaxDepth() {
        // given
        CommentDepth maxDepth = CommentDepth.of(3); // 최대 값

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            maxDepth.increment();
        });

        assertTrue(exception.getMessage().contains("Maximum comment depth"));
    }
}
