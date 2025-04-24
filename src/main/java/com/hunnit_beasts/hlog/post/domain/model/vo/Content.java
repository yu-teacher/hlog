package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Content {
    private final String value;
    private final ContentFormat format;

    private Content(String value, ContentFormat format) {
        if (value == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.value = value;
        this.format = Objects.requireNonNull(format);
    }

    public static Content ofMarkdown(String value) {
        return new Content(value, ContentFormat.MARKDOWN);
    }

    public static Content ofHtml(String value) {
        return new Content(value, ContentFormat.HTML);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return value.equals(content.value) && format == content.format;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, format);
    }
}
