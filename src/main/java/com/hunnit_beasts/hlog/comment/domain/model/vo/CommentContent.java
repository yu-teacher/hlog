package com.hunnit_beasts.hlog.comment.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CommentContent {
    private static final int MAX_LENGTH = 1000;

    private final String value;

    private CommentContent(String value) {
        validateContent(value);
        this.value = value;
    }

    public static CommentContent of(String value) {
        return new CommentContent(value);
    }

    private void validateContent(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Comment content exceeds maximum length of " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentContent that = (CommentContent) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}