package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileSettings {
    private static final Pattern CUSTOM_URL_PATTERN = Pattern.compile("^[a-z0-9-]+$");
    private static final int MIN_CUSTOM_URL_LENGTH = 3;
    private static final int MAX_CUSTOM_URL_LENGTH = 30;

    private final boolean isPublic;
    private final String customUrl;

    public static ProfileSettings create(boolean isPublic, String customUrl) {
        if (customUrl != null) {
            validateCustomUrl(customUrl);
        }
        return new ProfileSettings(isPublic, customUrl);
    }

    private static void validateCustomUrl(String customUrl) {
        if (customUrl.length() < MIN_CUSTOM_URL_LENGTH) {
            throw new IllegalArgumentException("Custom URL must be at least " + MIN_CUSTOM_URL_LENGTH + " characters");
        }
        if (customUrl.length() > MAX_CUSTOM_URL_LENGTH) {
            throw new IllegalArgumentException("Custom URL cannot exceed " + MAX_CUSTOM_URL_LENGTH + " characters");
        }
        if (!CUSTOM_URL_PATTERN.matcher(customUrl).matches()) {
            throw new IllegalArgumentException("Custom URL can only contain lowercase letters, numbers, and hyphens");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileSettings that = (ProfileSettings) o;
        return isPublic == that.isPublic &&
                Objects.equals(customUrl, that.customUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPublic, customUrl);
    }
}