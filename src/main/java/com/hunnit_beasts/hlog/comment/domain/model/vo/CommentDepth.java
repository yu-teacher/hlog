package com.hunnit_beasts.hlog.comment.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CommentDepth {
    private static final int MAX_DEPTH = 3; // 최대 3단계까지만 허용

    private final int value;

    private CommentDepth(int value) {
        validateDepth(value);
        this.value = value;
    }

    public static CommentDepth of(int value) {
        return new CommentDepth(value);
    }

    public CommentDepth increment() {
        return new CommentDepth(this.value + 1);
    }

    private void validateDepth(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Comment depth cannot be negative");
        }

        if (value > MAX_DEPTH) {
            throw new IllegalArgumentException("Maximum comment depth of " + MAX_DEPTH + " exceeded");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDepth that = (CommentDepth) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}