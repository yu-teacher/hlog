package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectLinks {
    private final String githubUrl;
    private final String demoUrl;

    public static ProjectLinks create(String githubUrl, String demoUrl) {
        validateUrl(githubUrl, "GitHub URL");
        validateUrl(demoUrl, "Demo URL");
        return new ProjectLinks(githubUrl, demoUrl);
    }

    private static void validateUrl(String url, String fieldName) {
        if (url != null && !url.trim().isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new IllegalArgumentException(fieldName + " must start with http:// or https://");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectLinks that = (ProjectLinks) o;
        return Objects.equals(githubUrl, that.githubUrl) &&
                Objects.equals(demoUrl, that.demoUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(githubUrl, demoUrl);
    }
}