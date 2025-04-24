package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Introduction {
    private final String shortBio;
    private final String detailedBio;

    public static Introduction create(String shortBio, String detailedBio) {
        validateShortBio(shortBio);
        validateDetailedBio(detailedBio);
        return new Introduction(shortBio, detailedBio);
    }

    private static void validateShortBio(String shortBio) {
        if (shortBio == null || shortBio.trim().isEmpty()) {
            throw new IllegalArgumentException("Short bio cannot be empty");
        }
        if (shortBio.length() > 100) {
            throw new IllegalArgumentException("Short bio cannot exceed 100 characters");
        }
    }

    private static void validateDetailedBio(String detailedBio) {
        if (detailedBio == null) {
            throw new IllegalArgumentException("Detailed bio cannot be null");
        }
        if (detailedBio.length() > 5000) {
            throw new IllegalArgumentException("Detailed bio cannot exceed 5000 characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Introduction that = (Introduction) o;
        return Objects.equals(shortBio, that.shortBio) &&
                Objects.equals(detailedBio, that.detailedBio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortBio, detailedBio);
    }
}